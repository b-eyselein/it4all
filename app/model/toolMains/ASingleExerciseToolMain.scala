package model.toolMains

import model.persistence.IdExerciseTableDefs
import model.{ExPart, ExerciseReview, ExerciseState, SingleCompleteEx, User}
import net.jcazevedo.moultingyaml.Auto
import play.api.data.Form
import play.api.i18n.MessagesProvider
import play.api.mvc.RequestHeader
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}

abstract class ASingleExerciseToolMain(tn: String, up: String)(implicit ec: ExecutionContext) extends FixedExToolMain(tn, up) {

  // Abstract Types

  type PartType <: ExPart

  type ReviewType <: ExerciseReview[PartType]

  override type CompExType <: SingleCompleteEx[ExType, PartType]

  override type Tables <: IdExerciseTableDefs[ExType, CompExType, PartType, ReviewType]

  override type ReadType = CompExType


  // Other members

  val exParts: Seq[PartType]

  // Forms

  def exerciseReviewForm(username: String, completeExercise: CompExType, exercisePart: PartType): Form[ReviewType]

  val compExForm: Form[CompExType]

  // DB

  def futureUpdateExercise(exercise: ExType): Future[Boolean] = tables.futureUpdateExercise(exercise)

  def futureSaveReview(review: ReviewType): Future[Boolean] = tables.futureSaveReview(review)

  def futureCompleteExById(id: Int): Future[Option[CompExType]] = tables.futureCompleteExById(id)

  override def futureSaveRead(exercises: Seq[ReadType]): Future[Seq[(ReadType, Boolean)]] = Future.sequence(exercises map {
    ex => tables.futureSaveCompleteEx(ex) map (saveRes => (ex, saveRes))
  })

  def futureHighestId: Future[Int] = tables.futureHighestExerciseId

  def updateExerciseState(id: Int, newState: ExerciseState): Future[Boolean] = tables.updateExerciseState(id, newState)

  def futureDeleteExercise(id: Int): Future[Int] = tables.deleteExercise(id)

  def futureReviewsForExercise(id: Int): Future[Seq[ReviewType]] = tables.futureReviewsForExercise(id)

  // Helper methods

  def partTypeFromUrl(urlName: String): Option[PartType] = exParts.find(_.urlName == urlName)

  def dataForUserExesOverview(user: User, page: Int): Future[UserExOverviewContent] = for {
    numOfExes <- tables.futureNumOfExes
    exes <- tables.futureCompleteExesForPage(page)
    exesAndRoutes <- Future.sequence(exes map (ex => exerciseRoutesForUser(user, ex) map (rs => ExAndRoute(ex, rs))))
  } yield UserExOverviewContent(numOfExes, exesAndRoutes)

  // Helper methods for admin

  def instantiateExercise(id: Int, author: String, state: ExerciseState): CompExType

  // TODO: scalarStyle = Folded if fixed...
  override def yamlString: Future[String] = futureCompleteExes map {
    exes => "%YAML 1.2\n---\n" + (exes map (yamlFormat.write(_).print(Auto /*, Folded*/)) mkString "---\n")
  }

  // Views

  def renderAdminExerciseEditForm(user: User, newEx: CompExType, isCreation: Boolean, toolList: ToolList): Html =
    views.html.admin.exerciseEditForm(user, newEx, renderEditRest(newEx), isCreation = true, this, toolList)

  def renderUserExerciseEditForm(user: User, newExForm: Form[CompExType], isCreation: Boolean)
                                (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html = ???

  def adminExerciseList(admin: User, exes: Seq[CompExType], toolList: ToolList): Html

  // Routes

  def exerciseRoutesForUser(user: User, exercise: CompExType): Future[Seq[CallForExPart]]

}
