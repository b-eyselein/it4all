package model.toolMains

import better.files.File
import model._
import model.core._
import model.core.result.{CompleteResult, CompleteResultJsonProtocol}
import model.persistence.ExerciseTableDefs
import net.jcazevedo.moultingyaml._
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps
import scala.util.{Failure, Try}

abstract class FixedExToolMain(aToolName: String, aUrlPart: String)(implicit ec: ExecutionContext) extends AToolMain(aToolName, aUrlPart) {

  // Abstract types

  type ExIdentifierType <: ExerciseIdentifier

  type ExType <: Exercise

  type PartType <: ExPart

  type SolType

  type SampleSolType <: SampleSolution[SolType]

  type UserSolType <: UserSolution[PartType, SolType]

  type CompResultType <: CompleteResult[ResultType]

  type ReadType

  override type Tables <: ExerciseTableDefs[ExType, PartType, SolType, SampleSolType, UserSolType]

  type ReviewType <: ExerciseReview[PartType]

  // Values

  val usersCanCreateExes: Boolean = false

  protected val completeResultJsonProtocol: CompleteResultJsonProtocol[ResultType, CompResultType]

  protected val exParts: Seq[PartType]

  // DB

  def futureMaybeOldSolution(user: User, exIdentifier: ExIdentifierType, part: PartType): Future[Option[UserSolType]]

  // Helper methods

  protected def exerciseHasPart(exercise: ExType, partType: PartType): Boolean

  def partTypeFromUrl(urlName: String): Option[PartType] = exParts.find(_.urlName == urlName)

  // Reading Yaml

  def yamlString: Future[String]


  // DB Operations

  def futureNumOfExes: Future[Int] = tables.futureNumOfExes

  def futureAllExercises: Future[Seq[ExType]] = tables.futureAllExes

  def futureInsertExercise(collId: Int, exercise: ExType): Future[Boolean] = tables.futureInsertExercise(collId, exercise)

  //  def futureSaveRead(exercises: Seq[ReadType]): Future[Seq[(ReadType, Boolean)]]

  def statistics: Future[Html] = futureNumOfExes map (num => Html(s"<li>Es existieren insgesamt $num Aufgaben</li>"))

  // Views

  def renderEditRest(exercise: ExType): Html = Html("")

  def renderExercisePreview(user: User, newExercise: ExType, saved: Boolean): Html = {
    println(newExercise)
    ???
  }

//  def previewReadAndSaveResult(user: User, read: ReadAndSaveResult[ReadType], toolList: ToolList): Html

}
