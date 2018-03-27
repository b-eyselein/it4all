package model.toolMains

import java.nio.file.{Files, Path}

import model.core._
import model.persistence.ExerciseTableDefs
import model.yaml.MyYamlFormat
import model.{CompleteEx, Exercise, User}
import net.jcazevedo.moultingyaml._
import play.twirl.api.Html

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

abstract class FixedExToolMain(urlPart: String)(implicit ec: ExecutionContext) extends AToolMain(urlPart) {

  // Abstract types

  type ExType <: Exercise

  type CompExType <: CompleteEx[ExType]

  type ReadType

  type Tables <: ExerciseTableDefs[ExType, CompExType]

  // Reading Yaml

  val yamlFormat: MyYamlFormat[ReadType]

  def readAndSave(yamlFileContent: String): Future[ReadAndSaveResult[ReadType]] = {
    val readTries: Seq[Try[ReadType]] = yamlFileContent.parseYamls map {
      yamlValue: YamlValue => yamlFormat.read(yamlValue)
    }

    val (successes, failures) = CommonUtils.splitTries(readTries)

    futureSaveRead(successes) map {
      saveResult => ReadAndSaveResult[ReadType](saveResult map (readAndSave => ReadAndSaveSuccess[ReadType](readAndSave._1, readAndSave._2)), failures)
    }
  }

  def yamlString: Future[String]

  def readImports: Seq[Try[ReadType]] = Files.newDirectoryStream(exerciseResourcesFolder).asScala
    .map(_.toAbsolutePath)
    .filter(_.getFileName.toString endsWith ".yaml")
    .map { filePath: Path =>
      readAll(filePath) flatMap { yamlFileContent: String =>
        val yamlValue: YamlValue = yamlFileContent.parseYaml
        yamlFormat.read(yamlValue)
      }
    }.toSeq

  // DB Operations

  val tables: Tables

  def futureNumOfExes: Future[Int] = tables.futureNumOfExes

  def futureCompleteExes: Future[Seq[CompExType]] = tables.futureCompleteExes

  def futureSaveRead(exercises: Seq[ReadType]): Future[Seq[(ReadType, Boolean)]]

  def statistics: Future[Html] = futureNumOfExes map (num => Html(s"<li>Es existieren insgesamt $num Aufgaben</li>"))

  // Views

  def renderEditRest(exercise: CompExType): Html = Html("")

  def previewExercise(user: User, read: ReadAndSaveResult[ReadType]): Html

}
