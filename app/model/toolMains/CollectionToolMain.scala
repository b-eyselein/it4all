package model.toolMains

import model._
import model.core._
import model.persistence.ExerciseCollectionTableDefs
import net.jcazevedo.moultingyaml.Auto
import play.api.libs.json.JsValue
import play.api.mvc.{AnyContent, Request}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

abstract class CollectionToolMain(urlPart: String) extends FixedExToolMain(urlPart) {

  // Abstract types

  override type ExType <: ExInColl

  override type CompExType <: CompleteExInColl[ExType]

  type CollType <: ExerciseCollection[ExType, CompExType]

  type CompCollType <: CompleteCollection

  type SolType <: CollectionExSolution

  type CompResult <: CompleteResult[R]

  override type ReadType = CompCollType

  // Other members

  val collectionSingularName: String

  val collectionPluralName: String

  // Database queries

  override type Tables <: ExerciseCollectionTableDefs[ExType, CompExType, CollType, CompCollType, SolType]

  def numOfExesInColl(id: Int): Future[Int] = tables.futureNumOfExesInColl(id)

  def futureCollById(id: Int): Future[Option[CollType]] = tables.futureCollById(id)

  def futureCompleteColls(implicit ec: ExecutionContext): Future[Seq[CompCollType]] = tables.futureCompleteColls

  def futureCompleteCollById(id: Int)(implicit ec: ExecutionContext): Future[Option[CompCollType]] = tables.futureCompleteCollById(id)

  def futureCompleteExById(collId: Int, id: Int)(implicit ec: ExecutionContext): Future[Option[CompExType]] = tables.futureCompleteExById(collId, id)

  def futureMaybeOldSolution(username: String, collId: Int, id: Int): Future[Option[SolType]] = tables.futureMaybeOldSolution(username, collId, id)

  override def futureSaveRead(exercises: Seq[CompCollType])(implicit ec: ExecutionContext): Future[Seq[(CompCollType, Boolean)]] = Future.sequence(exercises map {
    ex => tables.saveCompleteColl(ex) map (saveRes => (ex, saveRes))
  })

  def saveReadCollection(read: Seq[CompCollType])(implicit ec: ExecutionContext): Future[Seq[Boolean]] = Future.sequence(read map tables.saveCompleteColl)

  protected def saveSolution(solution: SolType)(implicit ec: ExecutionContext): Future[Boolean]

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

  // Reading solution from Request

  private def readSolution(user: User, collId: Int, id: Int, isLive: Boolean)(implicit request: Request[AnyContent]) =
    if (isLive) readSolutionFromPutRequest(user, collId: Int, id)
    else readSolutionFromPostRequest(user, collId: Int, id)

  def readSolutionFromPostRequest(user: User, collId: Int, id: Int)(implicit request: Request[AnyContent]): Option[SolType]

  def readSolutionFromPutRequest(user: User, collId: Int, id: Int)(implicit request: Request[AnyContent]): Option[SolType]

  // Views

  def renderExercise(user: User, coll: CollType, exercise: CompExType, numOfExes: Int): Future[Html]

  def onSubmitCorrectionResult(user: User, result: CompResult): Html

  def onSubmitCorrectionError(user: User, error: Throwable): Html

  def onLiveCorrectionResult(result: CompResult): JsValue

  def onLiveCorrectionError(error: Throwable): JsValue

  def adminRenderEditRest(exercise: Option[CompCollType]): Html

  //FIXME: ugly hack because of type params...
  override def renderEditForm(id: Int, admin: User)(implicit ec: ExecutionContext): Future[Html] = ???

  //    futureCompleteCollById(id) map {
  //    ex => views.html.admin.exerciseEditForm(admin, this, ex, renderEditRest(ex))
  //  }


  // Helper methods for admin

  // TODO: scalarStyle = Folded if fixed...
  override def yamlString(implicit ec: ExecutionContext): Future[String] = futureCompleteColls map {
    exes => "%YAML 1.2\n---\n" + (exes map (yamlFormat.write(_).print(Auto /*, Folded*/)) mkString "---\n")
  }

}
