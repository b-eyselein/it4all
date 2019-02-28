package model.toolMains

import model._
import model.core.result.{CompleteResult, CompleteResultJsonProtocol}
import model.persistence.ExerciseTableDefs
import play.api.data.Form
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

abstract class FixedExToolMain(aToolName: String, aUrlPart: String)(implicit ec: ExecutionContext) extends AToolMain(aToolName, aUrlPart) {

  // Abstract types

  type ExIdentifierType <: ExerciseIdentifier

  type CollType <: ExerciseCollection

  type ExType <: Exercise

  type PartType <: ExPart

  type SolType

  type SampleSolType <: SampleSolution[SolType]

  type UserSolType <: UserSolution[PartType, SolType]

  type CompResultType <: CompleteResult[ResultType]

  type ReadType

  type ReviewType <: ExerciseReview

  override type Tables <: ExerciseTableDefs[PartType, ExType, CollType, SolType, SampleSolType, UserSolType, ReviewType]

  // Values

  val usersCanCreateExes: Boolean = false

  protected val exParts: Seq[PartType]

  // Yaml, Html forms, Json

  protected val collectionYamlFormat: MyYamlFormat[CollType]
  protected val exerciseYamlFormat  : MyYamlFormat[ExType]

  val collectionForm    : Form[CollType]
  val exerciseForm      : Form[ExType]
  val exerciseReviewForm: Form[ReviewType]

  protected val completeResultJsonProtocol: CompleteResultJsonProtocol[ResultType, CompResultType]

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

  def renderExercisePreview(user: User, collId: Int, newExercise: ExType, saved: Boolean): Html = {
    println(newExercise)
    ???
  }

  //  def previewReadAndSaveResult(user: User, read: ReadAndSaveResult[ReadType], toolList: ToolList): Html

}
