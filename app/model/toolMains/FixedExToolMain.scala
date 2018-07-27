package model.toolMains

import java.nio.file.Path

import model.core._
import model.persistence.ExerciseTableDefs
import model.yaml.MyYamlFormat
import model.{CompleteEx, Exercise, User}
import net.jcazevedo.moultingyaml._
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Try}

abstract class FixedExToolMain(urlPart: String)(implicit ec: ExecutionContext) extends AToolMain(urlPart) {

  // Abstract types

  type ExType <: Exercise

  type CompExType <: CompleteEx[ExType]

  type ReadType

  override type Tables <: ExerciseTableDefs[ExType, CompExType]

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

  def readImports: Seq[Try[ReadType]] = subDirectoriesOf(exerciseResourcesFolder) map { subDirectory =>
    val filesToRead = filesInDirectory(subDirectory) sortBy (_.getFileName)

    filesToRead find (_.getFileName.toString endsWith ".yaml") match {
      case None                 => Failure(new Exception(s"There is no yaml file in folder ${subDirectory.toString}"))
      case Some(filePath: Path) =>
        readAll(filePath) flatMap { yamlFileContent: String =>
          val yamlValue: YamlValue = yamlFileContent.parseYaml
          yamlFormat.read(yamlValue)
        }
    }
  }

  // DB Operations

  def futureNumOfExes: Future[Int] = tables.futureNumOfExes

  def futureCompleteExes: Future[Seq[CompExType]] = tables.futureCompleteExes

  def futureSaveRead(exercises: Seq[ReadType]): Future[Seq[(ReadType, Boolean)]]

  def statistics: Future[Html] = futureNumOfExes map (num => Html(s"<li>Es existieren insgesamt $num Aufgaben</li>"))

  // Views

  def renderEditRest(exercise: CompExType): Html = Html("")

  def previewExercise(user: User, read: ReadAndSaveResult[ReadType], toolList: ToolList): Html

}
