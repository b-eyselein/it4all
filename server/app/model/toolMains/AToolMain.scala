package model.toolMains

import java.nio.file.Paths

import better.files.File._
import better.files._
import model._
import model.core.CoreConsts._
import model.core.result.EvaluationResult
import model.learningPath.{LearningPath, LearningPathTableDefs, LearningPathYamlProtocol}
import net.jcazevedo.moultingyaml._
import play.api.Logger
import play.api.mvc.Call
import play.twirl.api.Html

import scala.concurrent.Future
import scala.util.{Failure, Success}

abstract class AToolMain(val toolname: String, val urlPart: String) {

  private val logger = Logger(classOf[AToolMain])

  // Abstract types

  type ResultType <: EvaluationResult

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
        logger.error("Fehler: ", error)
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

  def solutionDirForExercise(username: String, collId: Int, exId: Int): File =
    exerciseRootDir / solutionsSubDir / username / String.valueOf(collId) / String.valueOf(exId)

  // Views

  // FIXME: remove...
  def exercisesOverviewForIndex: Html

  def adminIndexView(admin: User, toolList: ToolList): Future[Html] = Future.successful(Html("TODO!"))

  def playground(user: User): Html = Html("")

  // Calls

  @deprecated
  def indexCall: Call

}
