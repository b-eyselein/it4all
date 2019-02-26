package model.toolMains

import better.files.File
import model.{YamlArr, _}
import model.core._
import model.core.overviewHelpers.SolvedState
import model.persistence.ExerciseCollectionTableDefs
import play.api.Logger
import net.jcazevedo.moultingyaml._
import play.api.data.Form
import play.api.i18n.MessagesProvider
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContent, Call, Request, RequestHeader}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

abstract class CollectionToolMain(tn: String, up: String)(implicit ec: ExecutionContext) extends FixedExToolMain(tn, up) {

  private val logger = Logger(classOf[CollectionToolMain])

  // TODO: remove...

  def theExParts: Seq[PartType] = exParts

  // Abstract types

  override type ExIdentifierType = CollectionExIdentifier

  override type DBSolType <: CollectionExSolution[SolType]

  override type ReadType = (CollType, Seq[ExType])

  override type Tables <: ExerciseCollectionTableDefs[ExType, PartType, CollType, SolType, DBSolType]


  type CollType <: ExerciseCollection

  // Other members

  protected val collectionYamlFormat: MyYamlFormat[CollType]

  protected def exerciseYamlFormat(collId: Int, collSemVer: SemanticVersion): MyYamlFormat[ExType]

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

  def futureAllCollections: Future[Seq[CollType]] = tables.futureAllCollections

  def futureExerciseById(collId: Int, id: Int): Future[Option[ExType]] = tables.futureExerciseById(collId, id)

  def futureExercisesInColl(collId: Int): Future[Seq[ExType]] = tables.futureExercisesInColl(collId)

  override def futureMaybeOldSolution(user: User, exIdentifier: CollectionExIdentifier, part: PartType): Future[Option[DBSolType]] =
    tables.futureMaybeOldSolution(user.username, exIdentifier.collId, exIdentifier.exId)

  def futureSampleSolutions(collId: Int, exId: Int): Future[Seq[String]] = tables.futureSampleSolutions(collId, exId)

  def futureSolveState(user: User, collId: Int, exId: Int): Future[Option[SolvedState]] = tables.futureSolveState(user, collId, exId)

  // Saving

  def futureInsertAndDeleteOldCollection(collection: CollType): Future[Boolean] =
    tables.futureInsertAndDeleteOldCollection(collection)

  def futureSaveExercise(exercise: ExType): Future[Boolean] = ???

  override def futureSaveRead(reads: Seq[ReadType]): Future[Seq[(ReadType, Boolean)]]

  //  = Future.sequence(reads map {
  //    ex => tables.saveCompleteColl(ex) map (saveRes => (ex, saveRes))
  //  })

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

  override def adminIndexView(admin: User, toolList: ToolList): Future[Html] = tables.futureAllCollections map {
    collections => views.html.admin.collExes.collectionAdminIndex(admin, collections, this, toolList)
  }

  def renderExercise(user: User, coll: CollType, exercise: ExType, numOfExes: Int, maybeOldSolution: Option[DBSolType], part: PartType)
                    (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html

  def renderCollectionEditForm(user: User, collection: CollType, isCreation: Boolean, toolList: ToolList)
                              (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html =
    views.html.admin.collExes.collectionEditForm(user, collection, isCreation, new Html(""), this, toolList /*adminRenderEditRest(collection)*/)

  def renderExerciseEditForm(user: User, newEx: ExType, isCreation: Boolean, toolList: ToolList): Html =
    views.html.admin.exerciseEditForm(user, newEx, renderEditRest(newEx), isCreation = true, this, toolList)

  def previewCollectionReadAndSaveResult(user: User, readCollections: ReadAndSaveResult[CollType], toolList: ToolList): Html =
    views.html.admin.collExes.readCollectionsPreview(user, readCollections, this, toolList)

  def previewExerciseReadsAndSaveResult(user: User, readExercises: ReadAndSaveResult[ExType], toolList: ToolList): Html =
    views.html.admin.collExes.readExercisesPreview(user, readExercises, this, toolList)

  override def previewReadAndSaveResult(user: User, read: ReadAndSaveResult[ReadType], toolList: ToolList): Html = {
    ???

    //  views.html.admin.collExes.collPreview(user, read, this, toolList)
  }

  // Result handlers

  private def onLiveCorrectionResult(result: CompResultType, solutionSaved: Boolean): JsValue =
    completeResultJsonProtocol.completeResultWrites(solutionSaved).writes(result)

  def onLiveCorrectionError(error: Throwable): JsValue = {
    logger.error("There has been a correction error", error)
    Json.obj("msg" -> "Es gab einen internen Fehler bei der Korrektur!")
  }

  // Helper methods for admin

  def readCollectionsFromYaml: Seq[Try[CollType]] = {
    val fileToRead: File = exerciseResourcesFolder / "collections.yaml"

    Try(fileToRead.contentAsString.parseYaml) match {
      case Failure(error)     => Seq(Failure(error))
      case Success(yamlValue) => yamlValue match {
        case YamlArray(yamlObjects) => yamlObjects.map(collectionYamlFormat.read)
        case _                      => ???
      }
    }
  }

  def readExercisesFromYaml(collection: CollType): Seq[Try[ExType]] = {

    val fileToRead: File = exerciseResourcesFolder / s"${collection.id}-${collection.shortName}.yaml"

    Try(fileToRead.contentAsString.parseYaml) match {
      case Failure(error)     => Seq(Failure(error))
      case Success(yamlValue) => yamlValue match {
        case YamlArray(yamlObjects) => yamlObjects.map(exerciseYamlFormat(collection.id, collection.semanticVersion).read)
        case _                      => ???
      }
    }
  }

  // TODO: scalarStyle = Folded if fixed...
  override def yamlString: Future[String] = ???

  //  futureCompleteColls map {
  //    exes => ??? // FIXME: "%YAML 1.2\n---\n" + (exes map (yamlFormat.write(_).print(Auto /*, Folded*/)) mkString "---\n")
  //  }

  def instantiateCollection(id: Int, author: String, state: ExerciseState): CollType

  def instantiateExercise(collId: Int, id: Int, author: String, state: ExerciseState): ExType

  protected def instantiateSolution(id: Int, username: String, collection: CollType, exercise: ExType, solution: SolType,
                                    points: Points, maxPoints: Points): DBSolType

  // Calls

  override def indexCall: Call = controllers.routes.MainExerciseController.index(this.up)

}
