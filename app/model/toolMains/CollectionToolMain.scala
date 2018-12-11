package model.toolMains

import model._
import model.core._
import model.core.result.CompleteResult
import model.persistence.ExerciseCollectionTableDefs
import net.jcazevedo.moultingyaml.Auto
import play.api.Logger
import play.api.data.Form
import play.api.i18n.MessagesProvider
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContent, Call, Request, RequestHeader}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

abstract class CollectionToolMain(tn: String, up: String)(implicit ec: ExecutionContext) extends FixedExToolMain(tn, up) {

  // Abstract types

  override type ExType <: ExInColl

  override type CompExType <: CompleteExInColl[ExType]

  type CollType <: ExerciseCollection[ExType, CompExType]

  type CompCollType <: CompleteCollection

  type SolType

  type DBSolType <: CollectionExSolution[SolType]

  type CompResult <: CompleteResult[R]

  override type ReadType = CompCollType

  override type Tables <: ExerciseCollectionTableDefs[ExType, CompExType, CollType, CompCollType, SolType, DBSolType]

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

  def futureMaybeOldSolution(user: User, collId: Int, exId: Int): Future[Option[DBSolType]] = tables.futureMaybeOldSolution(user.username, collId, exId)

  def futureSampleSolutions(collId: Int, exId: Int): Future[Seq[String]] = tables.futureSampleSolutions(collId, exId)

  // Saving

  override def futureSaveRead(exercises: Seq[CompCollType]): Future[Seq[(CompCollType, Boolean)]] = Future.sequence(exercises map {
    ex => tables.saveCompleteColl(ex) map (saveRes => (ex, saveRes))
  })

  def saveReadCollection(read: Seq[CompCollType]): Future[Seq[Boolean]] = Future.sequence(read map tables.saveCompleteColl)

  def futureSaveExercise(exercise: CompExType): Future[Boolean] = tables.futureSaveCompleteEx(exercise)

  protected def saveSolution(solution: DBSolType): Future[Boolean]

  // Update

  def updateExerciseState(collId: Int, exId: Int, newState: ExerciseState): Future[Boolean] = tables.updateExerciseState(collId, exId, newState)

  def updateCollectionState(collId: Int, newState: ExerciseState): Future[Boolean] = tables.updateCollectionState(collId, newState)

  // Deletion

  def futureDeleteCollection(collId: Int): Future[Boolean] = tables.futureDeleteCollection(collId)

  def futureDeleteExercise(collId: Int, exId: Int): Future[Boolean] = tables.futureDeleteExercise(collId, exId)

  // Correction

  def correctAbstract(user: User, collId: Int, id: Int, isLive: Boolean)(implicit request: Request[AnyContent], ec: ExecutionContext): Future[Try[JsValue]] =
    readSolution(user, collId, id) match {
      case None => Future(Failure(SolutionTransferException))

      case Some(solution) =>

        val collAndEx: Future[Option[(CollType, CompExType)]] = for {
          coll <- futureCollById(collId)
          ex <- futureCompleteExById(collId, id)
        } yield (coll zip ex).headOption

        collAndEx flatMap {
          case None => Future(Failure(NoSuchExerciseException(id)))

          case Some((collection, exercise)) =>
            val futureResultTry: Future[Try[CompResult]] = correctEx(user, solution, collection, exercise)

            futureResultTry flatMap {
              case Failure(error) => Future(Failure(error))
              case Success(res)   =>

                // FIXME: points != 0? maxPoints != 0?
                val dbSol = instantiateSolution(id = -1, user.username, collection, exercise, solution, res.points, res.maxPoints)
                tables.futureSaveSolution(dbSol) map { solSaved => Success(onLiveCorrectionResult(res, solSaved)) }
            }
        }
    }

  protected def correctEx(user: User, sol: SolType, coll: CollType, exercise: CompExType): Future[Try[CompResult]]

  // Reading from requests

  def readExerciseFromForm(collId: Int)(implicit request: Request[AnyContent]): Form[CompExType] = compExTypeForm(collId).bindFromRequest()

  protected def readSolution(user: User, collId: Int, id: Int)(implicit request: Request[AnyContent]): Option[SolType]

  // Views

  override def exercisesOverviewForIndex: Html = Html(
    s"""<div class="form-group">
       |  <a class="btn btn-primary btn-block" href="${controllers.routes.CollectionController.collectionList(up)}">Zu den Übungsaufgabensammlungen</a>
       |</div>""".stripMargin)

  override def adminIndexView(admin: User, toolList: ToolList): Future[Html] = statistics map {
    stats => views.html.admin.collExes.collectionAdminIndex(admin, stats, this, toolList)
  }

  def renderExercise(user: User, coll: CollType, exercise: CompExType, numOfExes: Int, maybeOldSolution: Option[DBSolType])
                    (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html

  def adminRenderEditRest(exercise: Option[CompCollType]): Html

  def renderCollectionEditForm(user: User, collection: CompCollType, isCreation: Boolean, toolList: ToolList): Html =
    views.html.admin.collExes.collectionEditForm(user, collection, isCreation, new Html(""), this, toolList /*adminRenderEditRest(collection)*/)

  def renderExerciseEditForm(user: User, newEx: CompExType, isCreation: Boolean, toolList: ToolList): Html =
    views.html.admin.exerciseEditForm(user, newEx, renderEditRest(newEx), isCreation = true, this, toolList)

  override def previewReadAndSaveResult(user: User, read: ReadAndSaveResult[CompCollType], toolList: ToolList): Html =
    views.html.admin.collExes.collPreview(user, read, this, toolList)

  // Result handlers

  def onLiveCorrectionResult(result: CompResult, solutionSaved: Boolean): JsValue = result.toJson(solutionSaved)

  def onLiveCorrectionError(error: Throwable): JsValue = {
    Logger.error("There has been a correction error", error)
    Json.obj("msg" -> "Es gab einen internen Fehler bei der Korrektur!")
  }

  // Helper methods for admin

  // TODO: scalarStyle = Folded if fixed...
  override def yamlString: Future[String] = futureCompleteColls map {
    exes => "%YAML 1.2\n---\n" + (exes map (yamlFormat.write(_).print(Auto /*, Folded*/)) mkString "---\n")
  }

  def instantiateCollection(id: Int, state: ExerciseState): CompCollType

  def instantiateExercise(collId: Int, id: Int, state: ExerciseState): CompExType

  def instantiateSolution(id: Int, username: String, collection: CollType, exercise: CompExType, solution: SolType,
                          points: Points, maxPoints: Points): DBSolType

  // Calls

  override def indexCall: Call = controllers.routes.MainExerciseController.index(this.up)

}
