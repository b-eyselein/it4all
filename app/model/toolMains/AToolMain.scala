package model.toolMains

import java.nio.file.Paths

import better.files.File._
import better.files._
import enumeratum.{EnumEntry, PlayEnum}
import model.Role.RoleUser
import model._
import model.core.CoreConsts._
import model.core.result.EvaluationResult
import model.learningPath.{LearningPath, LearningPathTableDefs, LearningPathYamlProtocol}
import net.jcazevedo.moultingyaml._
import play.api.Logger
import play.api.mvc.Call
import play.twirl.api.Html

import scala.collection.immutable
import scala.concurrent.Future
import scala.util.{Failure, Success}

sealed abstract class ToolState(val german: String, val greek: String, requiredRole: Role) extends EnumEntry {

  def badge: Html = Html(s"<sup>$greek</sup>")

}

object ToolState extends PlayEnum[ToolState] {

  override val values: immutable.IndexedSeq[ToolState] = findValues

  case object LIVE extends ToolState("Verfügbare Tools", "", RoleUser) {
    override def badge: Html = new Html("")
  }

  case object ALPHA extends ToolState("Tools in Alpha-Status", "&alpha;", Role.RoleAdmin)

  case object BETA extends ToolState("Tools in Beta-Status", "&beta;", Role.RoleAdmin)

}

abstract class AToolMain(val toolname: String, val urlPart: String) {

  // Abstract types

  type R <: EvaluationResult

  type Tables <: LearningPathTableDefs

  // Other members

  protected val tables: Tables

  val hasTags      : Boolean = false
  val hasPlayground: Boolean = false

  val toolState: ToolState = ToolState.ALPHA

  val pluralName: String = "Aufgaben"

  private val learningPathsYamlFormat: MyYamlFormat[LearningPath] = LearningPathYamlProtocol.LearningPathYamlFormat(urlPart)

  // Helper methods

  def readLearningPaths: Seq[LearningPath] = {
    val learningPathFile: File = exerciseResourcesFolder / "learningPath.yaml"
    val content: String = learningPathFile.contentAsString

    learningPathsYamlFormat.read(content.parseYaml) match {
      case Success(read)  => Seq(read)
      case Failure(error) =>
        Logger.error("Fehler: ", error)
        Seq[LearningPath]()
    }
  }

  // DB

  def futureLearningPaths: Future[Seq[LearningPath]] = tables.futureLearningPaths(urlPart)

  def futureLearningPathById(id: Int): Future[Option[LearningPath]] = tables.futureLearningPathById(urlPart, id)

  def futureSaveLearningPaths(readLearningPaths: Seq[LearningPath]): Future[Boolean] = tables.futureSaveLearningPaths(readLearningPaths)

  // Folders

  private val rootDir: String = "data"

  private val resourcesFolder: File = Paths.get("conf", "resources")

  lazy val exerciseResourcesFolder: File = resourcesFolder / urlPart

  protected lazy val exerciseRootDir: File = Paths.get(rootDir, urlPart)

  def solutionDirForExercise(username: String, id: Int): File = exerciseRootDir / solutionsSubDir / username / String.valueOf(id)

  // Views

  def exercisesOverviewForIndex: Html

  def adminIndexView(admin: User, toolList: ToolList): Future[Html]

  def playground(user: User): Html = Html("")

  // Calls

  def indexCall: Call

}
