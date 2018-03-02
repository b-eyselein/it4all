package model.toolMains

import model.core.{CommonUtils, ReadAndSaveResult, ReadAndSaveSuccess, Wrappable}
import model.persistence.ExerciseTableDefs
import model.yaml.MyYamlFormat
import model.{CompleteEx, Exercise, User}
import net.jcazevedo.moultingyaml._
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

abstract class FixedExToolMain(urlPart: String) extends AToolMain(urlPart) {

  // Abstract types

  type ExType <: Exercise

  type CompExType <: CompleteEx[ExType]

  type ReadType <: Wrappable

  type Tables <: ExerciseTableDefs[ExType, CompExType]

  // Reading Yaml

  val yamlFormat: MyYamlFormat[ReadType]

  def readAndSave(yamlFileContent: String)(implicit ec: ExecutionContext): Future[ReadAndSaveResult] = {
    val readTries: Seq[Try[ReadType]] = yamlFileContent.parseYamls map yamlFormat.read

    val (successes, failures) = CommonUtils.splitTries(readTries)

    for (failure <- failures)
      println(failure)

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
