package model.toolMains

import model.Enums.ExerciseState
import model._
import model.core._
import model.persistence.ExerciseCollectionTableDefs
import net.jcazevedo.moultingyaml.Auto
import play.api.data.Form
import play.api.libs.json.JsValue
import play.api.mvc.{AnyContent, Request}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

abstract class CollectionToolMain(urlPart: String)(implicit ec: ExecutionContext) extends FixedExToolMain(urlPart) {

  // Abstract types

  override type ExType <: ExInColl

  override type CompExType <: CompleteExInColl[ExType]

  type CollType <: ExerciseCollection[ExType, CompExType]

  type CompCollType <: CompleteCollection

  type SolType <: CollectionExSolution

  type CompResult <: CompleteResult[R]

  override type ReadType = CompCollType

  override type Tables <: ExerciseCollectionTableDefs[ExType, CompExType, CollType, CompCollType, SolType]

  // Other members

  val collectionSingularName: String

  val collectionPluralName: String

  protected def compExTypeForm(collId: Int): Form[CompExType]

  // Database queries

  // Numbers

  def numOfExesInColl(id: Int): Future[Int] = tables.futureNumOfExesInColl(id)

  def futureHighestCollectionId: Future[Int] = tables.futureHighestCollectionId

  def futureHighestIdInCollection(collId: Int): Future[Int] = tables.futureHighestIdInCollection(collId)

  // Reading

  def futureCollById(id: Int): Future[Option[CollType]] = tables.futureCollById(id)

  def futureCompleteColls: Future[Seq[CompCollType]] = tables.futureCompleteColls

  def futureCompleteCollById(id: Int): Future[Option[CompCollType]] = tables.futureCompleteCollById(id)

  def futureCompleteExById(collId: Int, id: Int): Future[Option[CompExType]] = tables.futureCompleteExById(collId, id)

  def futureCompleteExesInColl(collId: Int): Future[Seq[CompExType]] = tables.futureCompleteExesInColl(collId)

  // Saving

  override def futureSaveRead(exercises: Seq[CompCollType]): Future[Seq[(CompCollType, Boolean)]] = Future.sequence(exercises map {
    ex => tables.saveCompleteColl(ex) map (saveRes => (ex, saveRes))
  })

  def saveReadCollection(read: Seq[CompCollType]): Future[Seq[Boolean]] = Future.sequence(read map tables.saveCompleteColl)

  def futureSaveExercise(exercise: CompExType): Future[Boolean] = tables.saveCompleteEx(exercise)

  protected def saveSolution(solution: SolType): Future[Boolean]

  // Update

  def updateExerciseState(collId: Int, exId: Int, newState: ExerciseState): Future[Boolean] = tables.updateExerciseState(collId, exId, newState)

  def updateCollectionState(collId: Int, newState: ExerciseState): Future[Boolean] = tables.updateCollectionState(collId, newState)

  // Deletion

  def futureDeleteCollection(collId: Int): Future[Boolean] = tables.futureDeleteCollection(collId)

  def futureDeleteExercise(collId: Int, exId: Int): Future[Boolean] = tables.futureDeleteExercise(collId, exId)

  // Correction

  def correctAbstract(user: User, collId: Int, id: Int, isLive: Boolean)(implicit request: Request[AnyContent], ec: ExecutionContext): Future[Try[Either[Html, JsValue]]] =
    readSolution(user, collId, id, isLive) match {
      case None => Future(Failure(SolutionTransferException))

      case Some(solution) =>
        (futureCollById(collId) zip futureCompleteExById(collId, id)) map {
          case (maybeColl, maybeEx) => (maybeColl zip maybeEx).headOption
        } flatMap {
          case None => Future(Failure(NoSuchExerciseException(id)))

          case Some((collection, exercise)) =>
            val futureResultTry: Future[Try[CompResult]] = correctEx(user, solution, collection, exercise)

            futureResultTry map {
              case Success(res)   =>
                if (isLive) Success(Right(onLiveCorrectionResult(res)))
                else Success(Left(onSubmitCorrectionResult(user, res)))
              case Failure(error) => Failure(error)
            }
        }
    }

  protected def correctEx(user: User, sol: SolType, coll: CollType, exercise: CompExType): Future[Try[CompResult]]

  // Reading from requests

  def readExerciseFromForm(collId: Int)(implicit request: Request[AnyContent]): Form[CompExType] = compExTypeForm(collId).bindFromRequest()

  private def readSolution(user: User, collId: Int, id: Int, isLive: Boolean)(implicit request: Request[AnyContent]) =
    if (isLive) readSolutionFromPutRequest(user, collId: Int, id)
    else readSolutionFromPostRequest(user, collId: Int, id)

  def readSolutionFromPostRequest(user: User, collId: Int, id: Int)(implicit request: Request[AnyContent]): Option[SolType]

  def readSolutionFromPutRequest(user: User, collId: Int, id: Int)(implicit request: Request[AnyContent]): Option[SolType]

  // Views

  def renderExercise(user: User, coll: CollType, exercise: CompExType, numOfExes: Int): Future[Html]

  def adminRenderEditRest(exercise: Option[CompCollType]): Html

  def renderCollectionEditForm(user: User, collection: CompCollType, isCreation: Boolean): Html =
    views.html.admin.collExes.collectionEditForm(user, this, collection.wrapped.asInstanceOf[CompleteCollectionWrapper], isCreation, new Html("") /*adminRenderEditRest(collection)*/)

  def renderExerciseEditForm(user: User, newEx: CompExType, isCreation: Boolean): Html =
    views.html.admin.exerciseEditForm(user, this, newEx, renderEditRest(newEx), isCreation = true)

  override def previewExercise(user: User, read: ReadAndSaveResult[CompCollType]): Html =
    views.html.admin.collExes.collPreview(user, read, this)

  // Result handlers

  def onSubmitCorrectionResult(user: User, result: CompResult): Html

  def onSubmitCorrectionError(user: User, error: Throwable): Html

  def onLiveCorrectionResult(result: CompResult): JsValue

  def onLiveCorrectionError(error: Throwable): JsValue

  // Helper methods for admin

  // TODO: scalarStyle = Folded if fixed...
  override def yamlString: Future[String] = futureCompleteColls map {
    exes => "%YAML 1.2\n---\n" + (exes map (yamlFormat.write(_).print(Auto /*, Folded*/)) mkString "---\n")
  }

  def instantiateCollection(id: Int, state: ExerciseState): CompCollType

  def instantiateExercise(collId: Int, id: Int, state: ExerciseState): CompExType

}
