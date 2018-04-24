package model.toolMains

import java.nio.file.{Path, Paths}

import model.Enums.ToolState
import model._
import model.core.CoreConsts._
import model.core._
import model.learningPath.{LearningPath, LearningPathTableDefs, LearningPathYamlProtocol}
import model.yaml.MyYamlFormat
import net.jcazevedo.moultingyaml._
import play.api.Logger
import play.api.mvc.Call
import play.twirl.api.Html

import scala.concurrent.Future
import scala.util.{Failure, Success}

abstract class AToolMain(val urlPart: String) extends FileUtils {

  // Abstract types

  type R <: EvaluationResult

  type Tables <: LearningPathTableDefs

  // Save this ToolMain

  ToolList.addTool(this)

  // Other members

  val tables: Tables

  val toolname: String

  val consts: Consts

  val hasTags: Boolean = false

  val toolState: ToolState = ToolState.ALPHA

  val pluralName: String = "Aufgaben"

  private val learningPathsYamlFormat: MyYamlFormat[LearningPath] = LearningPathYamlProtocol.LearningPathYamlFormat(urlPart)

  // Helper methods

  def readLearningPaths: Seq[LearningPath] = readAll(exerciseResourcesFolder / "learningPath.yaml") match {
    case Failure(error)       =>
      Logger.error(s"Error while reading learning paths for tool $urlPart", error)
      Seq.empty
    case Success(fileContent) =>
      learningPathsYamlFormat.read(fileContent.parseYaml) match {
        case Success(read)  => Seq(read)
        case Failure(error) =>
          Logger.error("Fehler: ", error)
          Seq.empty
      }
  }

  // DB

  def futureLearningPaths: Future[Seq[LearningPath]] = tables.futureLearningPaths(urlPart)

  def futureLearningPathById(id: Int): Future[Option[LearningPath]] = tables.futureLearningPathById(urlPart, id)

  def futureSaveLearningPaths(readLearningPaths: Seq[LearningPath]): Future[Boolean] = tables.futureSaveLearningPaths(readLearningPaths)

  // Folders

  private val rootDir: String = "data"

  private val resourcesFolder: Path = Paths.get("conf", "resources")

  lazy val exerciseResourcesFolder: Path = resourcesFolder / urlPart

  protected lazy val exerciseRootDir: Path = Paths.get(rootDir, urlPart)

  def solutionDirForExercise(username: String, id: Int): Path = exerciseRootDir / solutionsSubDir / username / String.valueOf(id)

  // Views

  def index(user: User, learningPaths: Seq[LearningPath]): Html

  def adminIndexView(admin: User): Future[Html]

  // Calls

  def indexCall: Call

}
