package controllers.exes

import java.nio.file.{Path, Paths}

import model.Enums.ToolState
import model.core.CoreConsts._
import model.core._
import model.persistence.ExerciseTableDefs
import model.yaml.MyYamlFormat
import model.{CompleteEx, _}
import net.jcazevedo.moultingyaml._
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

trait MyWrapper {

  def wrappedObj: Wrappable

}

trait Wrappable {

  def wrapped: MyWrapper

}

abstract class AToolMain(val urlPart: String) extends FileUtils {

  // Abstract types

  type ExType <: Exercise

  type CompExType <: CompleteEx[ExType]

  type R <: EvaluationResult

  type ReadType <: Wrappable

  // Save this ToolMain

  SingleExerciseController.addTool(this)

  // Other members

  val toolname: String

  val consts: Consts

  val hasTags: Boolean = false

  val toolState: ToolState = ToolState.ALPHA

  val pluralName: String = "Aufgaben"

  val rootDir: String = "data"

  val resourcesFolder: Path = Paths.get("conf", "resources")

  lazy val exerciseResourcesFolder: Path = resourcesFolder / urlPart

  lazy val exerciseRootDir: Path = Paths.get(rootDir, urlPart)

  def sampleDirForExercise(id: Int): Path = exerciseRootDir / SAMPLE_SUB_DIRECTORY / String.valueOf(id)

  def templateDirForExercise(id: Int): Path = exerciseRootDir / TEMPLATE_SUB_DIRECTORY / String.valueOf(id)

  def solutionDirForExercise(username: String, id: Int): Path = exerciseRootDir / SOLUTIONS_SUB_DIRECTORY / username / String.valueOf(id)

  // Abstract types

  type Tables <: ExerciseTableDefs[ExType, CompExType]

  // Reading Yaml

  val yamlFormat: MyYamlFormat[ReadType]

  def readAndSave(yamlFileContent: String)(implicit ec: ExecutionContext): Future[ReadAndSaveResult] = {
    val readTries: Seq[Try[ReadType]] = yamlFileContent.parseYamls map yamlFormat.read

    val (successes, failures) = CommonUtils.splitTries(readTries)

    futureSaveRead(successes) map {
      saveResult => ReadAndSaveResult(saveResult map (readAndSave => ReadAndSaveSuccess(readAndSave._1.wrapped, readAndSave._2)), failures)
    }
  }

  def yamlString(implicit ec: ExecutionContext): Future[String]

  // DB Operations

  val tables: Tables

  def futureNumOfExes(implicit ec: ExecutionContext): Future[Int] = tables.futureNumOfExes

  def futureCompleteExes(implicit ec: ExecutionContext): Future[Seq[CompExType]] = tables.futureCompleteExes

  def futureCompleteExesForPage(page: Int)(implicit ec: ExecutionContext): Future[Seq[CompExType]] = tables.futureCompleteExesForPage(page)

  def futureSaveRead(exercises: Seq[ReadType])(implicit ec: ExecutionContext): Future[Seq[(ReadType, Boolean)]]

  def delete(id: Int)(implicit ec: ExecutionContext): Future[Int] = tables.deleteExercise(id)

  def statistics(implicit ec: ExecutionContext): Future[Html] = futureNumOfExes map (num => Html(s"<li>Es existieren insgesamt $num Aufgaben</li>"))

  // Views

  def renderEditRest(exercise: CompExType): Html

  def renderEditForm(id: Int, admin: User)(implicit ec: ExecutionContext): Future[Html]

}
