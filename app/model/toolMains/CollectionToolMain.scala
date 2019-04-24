package model.toolMains

import better.files.File
import model._
import model.core.CoreConsts.stdStep
import model.core._
import model.core.overviewHelpers.{SolvedStates, SolvedStatesForExerciseParts}
import model.core.result.{CompleteResult, CompleteResultJsonProtocol}
import model.persistence.ExerciseTableDefs
import model.points._
import net.jcazevedo.moultingyaml._
import play.api.Logger
import play.api.data.Form
import play.api.i18n.MessagesProvider
import play.api.libs.json.{Format, JsValue, Json}
import play.api.mvc.{AnyContent, Call, Request, RequestHeader}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

abstract class CollectionToolMain(tn: String, up: String)(implicit ec: ExecutionContext)
  extends AToolMain(tn, up) with CollectionToolMainDbQueries {

  private val logger = Logger(classOf[CollectionToolMain])

  // Abstract types

  type CollType <: ExerciseCollection

  type ExType <: Exercise

  type PartType <: ExPart

  type SolType

  type SampleSolType <: SampleSolution[SolType]

  type UserSolType <: UserSolution[PartType, SolType]

  type CompResultType <: CompleteResult[ResultType]

  type ReviewType <: ExerciseReview

  override type Tables <: ExerciseTableDefs[PartType, ExType, CollType, SolType, SampleSolType, UserSolType, ReviewType]

  // Values

  val usersCanCreateExes: Boolean = false

  val exParts: Seq[PartType]

  // Yaml, Html forms, Json

  protected val collectionYamlFormat: MyYamlFormat[CollType]
  protected val exerciseYamlFormat  : MyYamlFormat[ExType]

  val collectionForm    : Form[CollType]
  val exerciseForm      : Form[ExType]
  val exerciseReviewForm: Form[ReviewType]

  // TODO: scalarStyle = Folded if fixed...
  def yamlString: Future[String] = ???

  val sampleSolutionJsonFormat: Format[SampleSolType]

  protected val completeResultJsonProtocol: CompleteResultJsonProtocol[ResultType, CompResultType]

  // Other helper methods

  protected def exerciseHasPart(exercise: ExType, partType: PartType): Boolean

  def partTypeFromUrl(urlName: String): Option[PartType] = exParts.find(_.urlName == urlName)

  // Db

  private def takeSlice[T](collection: Seq[T], page: Int, step: Int = stdStep): Seq[T] = {
    val start = Math.max(0, (page - 1) * step)
    val end = Math.min(page * step, collection.size)

    collection slice(start, end)
  }

  def futureUserCanSolveExPart(username: String, collId: Int, exId: Int, part: PartType): Future[Boolean] = Future.successful(true)

  def futureExesAndSolvedStatesForParts(user: User, collection: CollType, page: Int, step: Int): Future[Seq[SolvedStatesForExerciseParts[PartType]]] =

    futureExercisesInColl(collection.id).flatMap { exercises =>

      val approvedExercises: Seq[ExType] = exercises.filter(_.state == ExerciseState.APPROVED)

      val exesToDisplay = takeSlice(approvedExercises, page, step)

      Future.sequence(exesToDisplay.map { ex: ExType =>

        val exPartsForExercise = exParts.filter(exerciseHasPart(ex, _))

        val futureSolvedStatesForExerciseParts = Future.sequence(exPartsForExercise.map { exPart =>
          futureUserCanSolveExPart(user.username, collection.id, ex.id, exPart).flatMap {
            case true  =>
              futureSolveStateForExercisePart(user, collection.id, ex.id, exPart).map {
                // FIXME: query solved state!
                maybeSolvedState => (exPart, maybeSolvedState getOrElse SolvedStates.NotStarted)
              }
            case false => Future.successful((exPart, SolvedStates.Locked))
          }
        }).map(_.toMap)

        futureSolvedStatesForExerciseParts.map {
          solvedStatesForExerciseParts => SolvedStatesForExerciseParts(ex, solvedStatesForExerciseParts)
        }

      })

    }

  // Correction

  def correctAbstract(user: User, collection: CollType, exercise: ExType, part: PartType)
                     (implicit request: Request[AnyContent], ec: ExecutionContext): Future[Try[(CompResultType, Boolean)]] =
    readSolution(request, part) match {
      case Left(errorMsg) =>
        logger.error(errorMsg)
        Future.successful(Failure(SolutionTransferException))

      case Right(solution) =>
        correctEx(user, solution, collection, exercise, part).flatMap {
          case Failure(error) => Future.successful(Failure(error))
          case Success(res)   =>

            // FIXME: points != 0? maxPoints != 0?
            val dbSol = instantiateSolution(id = -1, exercise, part, solution, res.points, res.maxPoints)
            tables.futureSaveUserSolution(exercise.id, exercise.semanticVersion, collection.id, user.username, dbSol).map {
              solSaved => Success((res, solSaved))
            }
        }
    }

  protected def correctEx(user: User, sol: SolType, coll: CollType, exercise: ExType, part: PartType): Future[Try[CompResultType]]

  // Reading from requests

  protected def readSolution(request: Request[AnyContent], part: PartType): Either[String, SolType]

  // Views

  def previewExerciseRest(ex: Exercise): Html = Html(ex.toString)

  override def exercisesOverviewForIndex: Html = ???

  override def adminIndexView(admin: User, toolList: ToolList): Future[Html] = tables.futureAllCollections.map {
    collections => views.html.admin.collExes.collectionAdminIndex(admin, collections, this, toolList)
  }

  def renderExercise(user: User, coll: CollType, exercise: ExType, part: PartType, maybeOldSolution: Option[UserSolType])
                    (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html

  def renderCollectionEditForm(user: User, collection: CollType, isCreation: Boolean, toolList: ToolList)
                              (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html =
    views.html.admin.collExes.collectionEditForm(user, collection, isCreation, new Html(""), this, toolList /*adminRenderEditRest(collection)*/)

  def renderExerciseEditForm(user: User, collId: Int, newEx: ExType, isCreation: Boolean, toolList: ToolList): Html =
    views.html.admin.exerciseEditForm(user, collId, newEx, renderEditRest(newEx), isCreation = true, this, toolList)

  def renderEditRest(exercise: ExType): Html = Html("")

  def renderExercisePreview(user: User, collId: Int, newExercise: ExType, saved: Boolean): Html = {
    println(newExercise)
    ???
  }

  // Result handlers

  def onLiveCorrectionResult(result: CompResultType, solutionSaved: Boolean): JsValue =
    completeResultJsonProtocol.completeResultWrites(solutionSaved).writes(result)

  def onLiveCorrectionError(error: Throwable): JsValue = Json.obj("msg" -> "Es gab einen internen Fehler bei der Korrektur!")

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
        case YamlArray(yamlObjects) => yamlObjects.map(exerciseYamlFormat.read)
        case _                      => ???
      }
    }
  }

  //  futureCompleteColls .map {
  //    exes => ??? // FIXME: "%YAML 1.2\n---\n" + (exes .map (yamlFormat.write(_).print(Auto /*, Folded*/)) mkString "---\n")
  //  }

  def instantiateCollection(id: Int, author: String, state: ExerciseState): CollType

  def instantiateExercise(id: Int, author: String, state: ExerciseState): ExType

  protected def instantiateSolution(id: Int, exercise: ExType, part: PartType, solution: SolType, points: Points, maxPoints: Points): UserSolType

  // Calls

  override def indexCall: Call = controllers.coll.routes.CollectionController.index(this.urlPart)

  // Files ?!? TODO!

  def futureFilesForExercise(user: User, collId: Int, exercise: ExType, part: PartType): Future[Seq[ExerciseFile]] =
    Future.successful(Seq.empty)

}
