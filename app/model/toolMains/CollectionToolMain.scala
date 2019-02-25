package model.toolMains

import model._
import model.core._
import model.core.overviewHelpers.SolvedState
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


  // TODO: remove...

  def theExParts: Seq[PartType] = exParts

  // Abstract types

  override type ExIdentifierType = CollectionExIdentifier

  override type DBSolType <: CollectionExSolution[SolType]

  override type ReadType = CompCollType

  override type Tables <: ExerciseCollectionTableDefs[ExType, CollType, CompCollType, SolType, DBSolType]


  type CollType <: ExerciseCollection[ExType]

  type CompCollType <: CompleteCollection

  // Other members

  val collectionSingularName: String

  val collectionPluralName: String

  protected def compExTypeForm(collId: Int): Form[ExType]

  // Database queries

  // Numbers

  def numOfExesInColl(id: Int): Future[Int] = tables.futureNumOfExesInColl(id)

  def futureHighestCollectionId: Future[Int] = tables.futureHighestCollectionId

  def futureHighestIdInCollection(collId: Int): Future[Int] = tables.futureHighestIdInCollection(collId)

  // Reading

  def futureCollById(id: Int): Future[Option[CollType]] = tables.futureCollById(id)

  def futureCompleteColls: Future[Seq[CompCollType]] = tables.futureCompleteColls

  def futureCompleteCollById(id: Int): Future[Option[CompCollType]] = tables.futureCompleteCollById(id)

  def futureExerciseById(collId: Int, id: Int): Future[Option[ExType]] = tables.futureExerciseById(collId, id)

  def futureExercisesInColl(collId: Int): Future[Seq[ExType]] = tables.futureExercisesInColl(collId)

  override def futureMaybeOldSolution(user: User, exIdentifier: CollectionExIdentifier, part: PartType): Future[Option[DBSolType]] =
    tables.futureMaybeOldSolution(user.username, exIdentifier.collId, exIdentifier.exId)

  def futureSampleSolutions(collId: Int, exId: Int): Future[Seq[String]] = tables.futureSampleSolutions(collId, exId)

  def futureSolveState(user: User, collId: Int, exId: Int): Future[Option[SolvedState]] = tables.futureSolveState(user, collId, exId)

  // Saving

  override def futureSaveRead(exercises: Seq[CompCollType]): Future[Seq[(CompCollType, Boolean)]] = Future.sequence(exercises map {
    ex => tables.saveCompleteColl(ex) map (saveRes => (ex, saveRes))
  })

  def saveReadCollection(read: Seq[CompCollType]): Future[Seq[Boolean]] = Future.sequence(read map tables.saveCompleteColl)

  protected def saveSolution(solution: DBSolType): Future[Boolean]

  // Update

  def updateExerciseState(collId: Int, exId: Int, newState: ExerciseState): Future[Boolean] = tables.updateExerciseState(collId, exId, newState)

  def updateCollectionState(collId: Int, newState: ExerciseState): Future[Boolean] = tables.updateCollectionState(collId, newState)

  // Deletion

  def futureDeleteCollection(collId: Int): Future[Boolean] = tables.futureDeleteCollection(collId)

  def futureDeleteExercise(collId: Int, exId: Int): Future[Boolean] = tables.futureDeleteExercise(collId, exId)

  // Correction

  def correctAbstract(user: User, collId: Int, id: Int, part: PartType)(implicit request: Request[AnyContent], ec: ExecutionContext): Future[Try[JsValue]] =
    readSolution(user, collId, id) match {
      case None => Future.successful(Failure(SolutionTransferException))

      case Some(solution) =>

        val collAndEx: Future[Option[(CollType, ExType)]] = for {
          coll <- futureCollById(collId)
          ex <- futureExerciseById(collId, id)
        } yield (coll zip ex).headOption

        collAndEx flatMap {
          case None => Future.successful(Failure(NoSuchExerciseException(id)))

          case Some((collection, exercise)) => correctEx(user, solution, collection, exercise, part) match {
            case Failure(error) => Future.successful(Failure(error))
            case Success(res)   =>

              // FIXME: points != 0? maxPoints != 0?
              val dbSol = instantiateSolution(id = -1, user.username, collection, exercise, solution, res.points, res.maxPoints)
              tables.futureSaveSolution(dbSol) map { solSaved => Success(onLiveCorrectionResult(res, solSaved)) }
          }
        }
    }

  protected def correctEx(user: User, sol: SolType, coll: CollType, exercise: ExType, part: PartType): Try[CompResultType]

  // Reading from requests

  def readExerciseFromForm(collId: Int)(implicit request: Request[AnyContent]): Form[ExType] = compExTypeForm(collId).bindFromRequest()

  protected def readSolution(user: User, collId: Int, id: Int)(implicit request: Request[AnyContent]): Option[SolType]

  // Views

  override def exercisesOverviewForIndex: Html = Html(
    s"""<div class="form-group">
       |  <a class="btn btn-primary btn-block" href="${controllers.coll.routes.CollectionController.collectionList(up)}">Zu den Übungsaufgabensammlungen</a>
       |</div>""".stripMargin)

  override def adminIndexView(admin: User, toolList: ToolList): Future[Html] = statistics map {
    stats => views.html.admin.collExes.collectionAdminIndex(admin, stats, this, toolList)
  }

  def renderExercise(user: User, coll: CollType, exercise: ExType, numOfExes: Int, maybeOldSolution: Option[DBSolType], part: PartType)
                    (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html

  def adminRenderEditRest(exercise: Option[CompCollType]): Html

  def renderCollectionEditForm(user: User, collection: CompCollType, isCreation: Boolean, toolList: ToolList): Html =
    views.html.admin.collExes.collectionEditForm(user, collection, isCreation, new Html(""), this, toolList /*adminRenderEditRest(collection)*/)

  def renderExerciseEditForm(user: User, newEx: ExType, isCreation: Boolean, toolList: ToolList): Html =
    views.html.admin.exerciseEditForm(user, newEx, renderEditRest(newEx), isCreation = true, this, toolList)

  override def previewReadAndSaveResult(user: User, read: ReadAndSaveResult[CompCollType], toolList: ToolList): Html =
    views.html.admin.collExes.collPreview(user, read, this, toolList)

  // Result handlers

  def onLiveCorrectionResult(result: CompResultType, solutionSaved: Boolean): JsValue =
    completeResultJsonProtocol.completeResultWrites(solutionSaved).writes(result)

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

  def instantiateExercise(collId: Int, id: Int, author: String, state: ExerciseState): ExType

  protected def instantiateSolution(id: Int, username: String, collection: CollType, exercise: ExType, solution: SolType,
                                    points: Points, maxPoints: Points): DBSolType

  // Calls

  override def indexCall: Call = controllers.routes.MainExerciseController.index(this.up)

}
