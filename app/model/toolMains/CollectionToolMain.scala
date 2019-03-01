package model.toolMains

import better.files.File
import model._
import model.core._
import model.core.result.{CompleteResult, CompleteResultJsonProtocol}
import model.persistence.ExerciseCollectionTableDefs
import net.jcazevedo.moultingyaml._
import play.api.data.Form
import play.api.i18n.MessagesProvider
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContent, Call, Request, RequestHeader}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

abstract class CollectionToolMain(tn: String, up: String)(implicit ec: ExecutionContext)
  extends AToolMain(tn, up) with CollectionToolMainDbQueries {

  // Abstract types

  type CollType <: ExerciseCollection

  type ExType <: Exercise

  type PartType <: ExPart

  type SolType

  type SampleSolType <: SampleSolution[SolType]

  type UserSolType <: UserSolution[PartType, SolType]

  type CompResultType <: CompleteResult[ResultType]

  type ReviewType <: ExerciseReview

  override type Tables <: ExerciseCollectionTableDefs[PartType, ExType, CollType, SolType, SampleSolType, UserSolType, ReviewType]

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

  protected val completeResultJsonProtocol: CompleteResultJsonProtocol[ResultType, CompResultType]

  // Other helper methods

  protected def exerciseHasPart(exercise: ExType, partType: PartType): Boolean

  def partTypeFromUrl(urlName: String): Option[PartType] = exParts.find(_.urlName == urlName)

  // Correction

  def correctAbstract(user: User, collection: CollType, exercise: ExType, part: PartType)
                     (implicit request: Request[AnyContent], ec: ExecutionContext): Future[Try[(CompResultType, Boolean)]] =
    readSolution(user, collection, exercise, part) match {
      case None => Future.successful(Failure(SolutionTransferException))

      case Some(solution) =>

        correctEx(user, solution, collection, exercise, part) flatMap {
          case Failure(error) => Future.successful(Failure(error))
          case Success(res)   =>

            // FIXME: points != 0? maxPoints != 0?
            val dbSol = instantiateSolution(id = -1, exercise, part, solution, res.points, res.maxPoints)
            tables.futureSaveUserSolution(exercise.id, exercise.semanticVersion, collection.id, user.username, dbSol) map {
              solSaved => Success((res, solSaved))
            }
        }
    }

  protected def correctEx(user: User, sol: SolType, coll: CollType, exercise: ExType, part: PartType): Future[Try[CompResultType]]

  // Reading from requests

  def readExerciseFromForm(implicit request: Request[AnyContent]): Form[ExType] = exerciseForm.bindFromRequest()

  protected def readSolution(user: User, collection: CollType, exercise: ExType, part: PartType)(implicit request: Request[AnyContent]): Option[SolType]

  // Views

  override def exercisesOverviewForIndex: Html = ???

  override def adminIndexView(admin: User, toolList: ToolList): Future[Html] = tables.futureAllCollections map {
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

  //  futureCompleteColls map {
  //    exes => ??? // FIXME: "%YAML 1.2\n---\n" + (exes map (yamlFormat.write(_).print(Auto /*, Folded*/)) mkString "---\n")
  //  }

  def instantiateCollection(id: Int, author: String, state: ExerciseState): CollType

  def instantiateExercise(id: Int, author: String, state: ExerciseState): ExType

  protected def instantiateSolution(id: Int, exercise: ExType, part: PartType, solution: SolType, points: Points, maxPoints: Points): UserSolType

  // Calls

  override def indexCall: Call = controllers.coll.routes.CollectionController.index(this.urlPart)

}
