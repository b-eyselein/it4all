package model.toolMains

import model.ExerciseState.APPROVED
import model.persistence.IdExerciseTableDefs
import model.{ExPart, ExerciseReview, ExerciseState, SemanticVersion, SingleCompleteEx, User}
import net.jcazevedo.moultingyaml.Auto
import play.api.data.Form
import play.api.i18n.MessagesProvider
import play.api.mvc.RequestHeader
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

abstract class ASingleExerciseToolMain(aToolName: String, aUrlPart: String)(implicit ec: ExecutionContext) extends FixedExToolMain(aToolName, aUrlPart) {

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

  def compExForm: Form[CompExType]

  // DB

  def futureUpdateExercise(exercise: ExType): Future[Boolean] = tables.futureUpdateExercise(exercise)

  def futureSaveReview(review: ReviewType): Future[Boolean] = tables.futureSaveReview(review)

  def futureCompleteExById(id: Int): Future[Option[CompExType]] = tables.futureCompleteExById(id)

  def futureCompleteExByIdAndVersion(id: Int, semVer: SemanticVersion): Future[Option[CompExType]] =
    tables.futureCompleteExByIdAndVersion(id, semVer)

  override def futureSaveRead(exercises: Seq[ReadType]): Future[Seq[(ReadType, Boolean)]] = Future.sequence(exercises map {
    ex => tables.futureSaveCompleteEx(ex) map (saveRes => (ex, saveRes))
  })

  def futureHighestId: Future[Int] = tables.futureHighestExerciseId

  def updateExerciseState(id: Int, newState: ExerciseState): Future[Boolean] = tables.updateExerciseState(id, newState)

  def futureDeleteExercise(id: Int): Future[Int] = tables.deleteExercise(id)

  def futureAllReviews: Future[Seq[ReviewType]] = tables.futureAllReviews

  def futureReviewsForExercise(id: Int): Future[Seq[ReviewType]] = tables.futureReviewsForExercise(id)

  // Helper methods

  def partTypeFromUrl(urlName: String): Option[PartType] = exParts.find(_.urlName == urlName)

  private def futureCompleteExesForPage(page: Int): Future[Seq[CompExType]] = tables.futureCompleteExes map {
    allExes: Seq[CompExType] =>
      val STEP = 10

      val distinctExes: Seq[CompExType] = allExes.filter(_.ex.state == APPROVED)

      val sliceStart = Math.max(0, (page - 1) * STEP)
      val sliceEnd = Math.min(page * STEP, distinctExes.size)

      distinctExes.slice(sliceStart, sliceEnd)
  }

  private def getRoutesForEx(user: User, exes: Seq[CompExType]): Future[Seq[ExAndRoute]] = Future.sequence {
    exes.groupBy(_.ex.id).map {
      case (id: Int, allVersionsOfEx: Seq[CompExType]) =>
        exerciseRoutesForUser(user, allVersionsOfEx.head).map {
          routes: Seq[CallForExPart] =>
            val versions = allVersionsOfEx.map(_.ex.semanticVersion)
            val exTitle = allVersionsOfEx.head.ex.title

            ExAndRoute(id, exTitle, versions, routes)
        }
    }.toSeq
  }

  def dataForUserExesOverview(user: User, page: Int): Future[UserExOverviewContent] = for {
    numOfExes <- tables.futureNumOfExes
    exes <- futureCompleteExesForPage(page)
    exesAndRoutes <- getRoutesForEx(user, exes)
  } yield UserExOverviewContent(numOfExes, exesAndRoutes)

  // Helper methods for admin

  def instantiateExercise(id: Int, author: String, state: ExerciseState): CompExType

  // TODO: scalarStyle = Folded if fixed...
  override def yamlString: Future[String] = futureCompleteExes map {
    exes => "%YAML 1.2\n---\n" + (exes map (yamlFormat.write(_).print(Auto /*, Folded*/)) mkString "---\n")
  }

  // Views

  def renderAdminExerciseEditForm(user: User, newEx: CompExType, isCreation: Boolean, toolList: ToolList)
                                 (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html =
    views.html.admin.exerciseEditForm(user, newEx, renderEditRest(newEx), isCreation = true, this, toolList)

  def renderUserExerciseEditForm(user: User, newExForm: Form[CompExType], isCreation: Boolean)
                                (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html //= ???

  def adminExerciseList(admin: User, exes: Seq[CompExType], toolList: ToolList): Html

  // Routes

  def exerciseRoutesForUser(user: User, exercise: CompExType): Future[Seq[CallForExPart]]

}
