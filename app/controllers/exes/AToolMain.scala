package controllers.exes

import java.nio.file.{Path, Paths}

import model.Enums.ToolState
import model._
import model.core.CoreConsts._
import model.core.{CommonUtils, FileUtils}
import model.yaml.MyYamlFormat
import net.jcazevedo.moultingyaml._
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}

trait AToolMain[ExType <: Exercise, CompExType <: CompleteEx[ExType]] extends FileUtils {


  val hasTags: Boolean = false
  val toolname: String
  val exType  : String
  val consts  : Consts
  val toolState: ToolState = ToolState.LIVE

  val pluralName: String = "Aufgaben"

  val rootDir: String = "data"

  val resourcesFolder: Path = Paths.get("conf", "resources")

  lazy val exerciseResourcesFolder: Path = resourcesFolder / exType

  lazy val exerciseRootDir: Path = Paths.get(rootDir, exType)

  val hasExType: Boolean = false


  def sampleDirForExercise(id: Int): Path = exerciseRootDir / SAMPLE_SUB_DIRECTORY / String.valueOf(id)

  def templateDirForExercise(id: Int): Path = exerciseRootDir / TEMPLATE_SUB_DIRECTORY / String.valueOf(id)

  def solutionDirForExercise(username: String, id: Int): Path = exerciseRootDir / SOLUTIONS_SUB_DIRECTORY / username / String.valueOf(id)

  val urlPart: String

  BaseExerciseController.ToolMains += (urlPart -> this)

  // Abstract types

  type Tables <: ExerciseTableDefs[ExType, CompExType]

  // Reading Yaml

  val yamlFormat: MyYamlFormat[CompExType]

  def readAndSave(yamlFileContent: String)(implicit ec: ExecutionContext): (Seq[CompExType], Future[Seq[Boolean]]) = {
    val readTries = yamlFileContent.parseYamls map yamlFormat.read

    val (successes, failures) = CommonUtils.splitTries(readTries)

    (successes, saveRead(successes))
  }

  // DB Operations

  val tables: Tables

  def futureCompleteExes(implicit ec: ExecutionContext): Future[Seq[CompExType]] = tables.futureCompleteExes

  def futureCompleteExById(id: Int)(implicit ec: ExecutionContext): Future[Option[CompExType]] = tables.futureCompleteExById(id)

  def saveRead(read: Seq[CompExType])(implicit ec: ExecutionContext): Future[Seq[Boolean]] = Future.sequence(read map tables.saveCompleteEx)

  def delete(id: Int)(implicit ec: ExecutionContext): Future[Int] = tables.deleteExercise(id)

  def statistics(implicit ec: ExecutionContext): Future[Html] = tables.futureNumOfExes map (num => Html(s"<li>Es existieren insgesamt $num Aufgaben</li>"))

  // Views

  def renderEditRest(exercise: Option[CompExType]): Html = ???

  //FIXME: ugly hack because of type params...
  def renderEditForm(id: Int, admin: User)(implicit ec: ExecutionContext): Future[Html] = futureCompleteExById(id) map {
    ex => views.html.admin.exerciseEditForm(admin, this, ex, renderEditRest(ex))
  }

  // Helper methods for admin

  // TODO: scalarStyle = Folded if fixed...
  def yamlString(implicit ec: ExecutionContext): Future[String] = futureCompleteExes map {
    exes => "%YAML 1.2\n---\n" + (exes map (yamlFormat.write(_).print(Auto /*, Folded*/)) mkString "---\n")
  }

}
