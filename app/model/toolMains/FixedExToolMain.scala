package model.toolMains

import java.nio.file.Path

import better.files.File
import model._
import model.core._
import model.persistence.ExerciseTableDefs
import net.jcazevedo.moultingyaml._
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps
import scala.util.{Failure, Try}

abstract class FixedExToolMain(aToolName: String, aUrlPart: String)(implicit ec: ExecutionContext) extends AToolMain(aToolName, aUrlPart) {

  // Abstract types

  type ExType <: Exercise

  type CompExType <: CompleteEx[ExType]

  type ReadType

  override type Tables <: ExerciseTableDefs[ExType, CompExType]

  // Values

  val usersCanCreateExes: Boolean = false

  // Reading Yaml

  val yamlFormat: MyYamlFormat[ReadType]

  def readAndSave(yamlFileContent: String): Future[ReadAndSaveResult[ReadType]] = {
    val readTries: Seq[Try[ReadType]] = yamlFileContent.parseYamls map {
      yamlValue: YamlValue => yamlFormat.read(yamlValue)
    }

    val (successes, failures) = CommonUtils.splitTriesNew(readTries)

    futureSaveRead(successes) map {
      saveResult => ReadAndSaveResult[ReadType](saveResult map (readAndSave => ReadAndSaveSuccess[ReadType](readAndSave._1, readAndSave._2)), failures)
    }
  }

  def yamlString: Future[String]

  def readImports: Seq[Try[ReadType]] = {
    val subDirectories = exerciseResourcesFolder.list filter (_.isDirectory) toSeq

    subDirectories map { subDirectory: File =>
      val filesToRead: Seq[File] = subDirectory.list.toList sortBy (_.name)

      filesToRead find (_.name.toString endsWith ".yaml") match {
        case None           => Failure(new Exception(s"There is no yaml file in folder ${subDirectory.toString}"))
        case Some(filePath) => yamlFormat.read(filePath.contentAsString.parseYaml)
      }
    }
  }

  // DB Operations

  def futureNumOfExes: Future[Int] = tables.futureNumOfExes

  def futureCompleteExes: Future[Seq[CompExType]] = tables.futureCompleteExes

  def futureSaveCompleteEx(completeEx: CompExType): Future[Boolean] = tables.futureSaveCompleteEx(completeEx)

  def futureInsertCompleteEx(completeEx: CompExType): Future[Boolean] = tables.futureInsertCompleteEx(completeEx)

  def futureSaveRead(exercises: Seq[ReadType]): Future[Seq[(ReadType, Boolean)]]

  def statistics: Future[Html] = futureNumOfExes map (num => Html(s"<li>Es existieren insgesamt $num Aufgaben</li>"))

  // Views

  def renderEditRest(exercise: CompExType): Html = Html("")

  def renderExercisePreview(user: User, newExercise: CompExType, saved: Boolean): Html = {
    println(newExercise)
    ???
  }

  def previewReadAndSaveResult(user: User, read: ReadAndSaveResult[ReadType], toolList: ToolList): Html

}
