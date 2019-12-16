package controllers

import javax.inject.{Inject, Singleton}
import model.ExerciseReview
import model.core.ToolForms
import model.persistence.ExerciseTableDefs
import model.tools.collectionTools.{Exercise, ExerciseCollection, ExerciseMetaData, ToolJsonProtocol}
import play.api.data.Form
import play.api.libs.json._
import play.api.mvc._
import play.api.{Configuration, Logger}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class ApiController @Inject()(
  cc: ControllerComponents,
  tables: ExerciseTableDefs,
  override protected val configuration: Configuration
)(implicit val ec: ExecutionContext) extends AbstractController(cc) with AbstractApiExerciseController {

  private val logger = Logger(classOf[ApiController])

  override protected val adminRightsRequired: Boolean = false

  private implicit val collectionFormat      : Format[ExerciseCollection] = ToolJsonProtocol.collectionFormat
  private implicit val exerciseMetaDataFormat: Format[ExerciseMetaData]   = ToolJsonProtocol.exerciseMetaDataFormat
  private implicit val exerciseFormat        : Format[Exercise]           = ToolJsonProtocol.exerciseFormat

  def apiAllCollections(toolType: String): Action[AnyContent] = JwtAuthenticatedToolMainAction(toolType).async { implicit request =>
    tables.futureAllCollections(request.toolMain.urlPart).map { collections =>
      Ok(Json.toJson(collections))
    }
  }

  def apiCollection(toolType: String, collId: Int): Action[AnyContent] = JwtAuthenticatedToolMainAction(toolType).async { implicit request =>
    tables.futureCollById(request.toolMain.urlPart, collId).map {
      case None             => NotFound(s"There is no such collection with id $collId for tool ${request.toolMain.toolName}")
      case Some(collection) => Ok(Json.toJson(collection))
    }
  }

  def apiExerciseMetaDataForTool(toolType: String): Action[AnyContent] = JwtAuthenticatedToolMainAction(toolType).async { implicit request =>
    tables.futureExerciseMetaDataForTool(request.toolMain.urlPart).map { exerciseMetaData =>
      Ok(Json.toJson(exerciseMetaData))
    }
  }

  def apiExerciseMetaDataForCollection(toolType: String, collId: Int): Action[AnyContent] = JwtAuthenticatedToolMainAction(toolType).async { implicit request =>
    tables.futureExerciseMetaDataForCollection(request.toolMain.urlPart, collId).map { exerciseMetaData =>
      Ok(Json.toJson(exerciseMetaData))
    }
  }

  def apiExercises(toolType: String, collId: Int): Action[AnyContent] = JwtAuthenticatedToolMainAction(toolType).async { implicit request =>
    // FIXME: send only id, title, ... ?
    tables.futureExercisesInColl(request.toolMain, collId).map { exercises: Seq[Exercise] =>
      Ok(Json.toJson(exercises))
    }
  }

  def apiExercise(toolType: String, collId: Int, exId: Int): Action[AnyContent] = JwtAuthenticatedToolMainAction(toolType).async { implicit request =>
    tables.futureExerciseById(request.toolMain.urlPart, collId, exId).map {
      case None           => NotFound(s"There is no such exercise with id $exId for collection $collId")
      case Some(exercise) => Ok(Json.toJson(exercise))
    }
  }

  def apiCorrect(toolType: String, collId: Int, exId: Int, partStr: String): Action[JsValue] = JwtAuthenticatedToolMainAction(toolType).async(parse.json) { implicit request =>

    tables.futureCollectionAndExercise(request.toolMain.urlPart, collId, exId).flatMap {
      case None                         => Future.successful(onNoSuchExercise(collId, exId))
      case Some((collection, exercise)) =>

        request.toolMain.partTypeFromUrl(partStr) match {
          case None         => Future.successful(onNoSuchExercisePart(exercise, partStr))
          case Some(exPart) =>

            request.toolMain.readSolution(request.body).fold(
              errors => {
                errors.foreach(e => logger.error(e.toString()))
                ???
              },
              solution => {

                tables.futureInsertSolution(request.request.user, exercise, exPart, request.body)
                  .flatMap { inserted =>

                    request.toolMain.correctAbstract(request.request.user, solution, collection, exercise, exPart, inserted).map {
                      case Failure(error) =>
                        logger.error("There has been an internal correction error:", error)
                        BadRequest(Json.obj("msg" -> "Es gab einen internen Fehler bei der Korrektur!"))

                      case Success(result) => Ok(request.toolMain.onLiveCorrectionResult(result))
                    }
                  }
              }
            )
        }
    }
  }

  // Exercise review process

  def reviewExercisePart(toolType: String, collId: Int, id: Int, partStr: String): EssentialAction = JwtAuthenticatedToolMainAction(toolType).async { request =>
    tables.futureExerciseById(request.toolMain.urlPart, collId, id).flatMap {
      case None           => Future.successful(onNoSuchExercise(collId, id))
      case Some(exercise) =>

        request.toolMain.partTypeFromUrl(partStr) match {
          case None       => Future.successful(onNoSuchExercisePart(exercise, partStr))
          case Some(part) =>

            val onFormError: Form[ExerciseReview] => Future[Result] = { formWithErrors =>
              ???
            }

            val onFormRead: ExerciseReview => Future[Result] = { currentReview =>
              tables.futureSaveReview(request.toolMain, request.user.username, collId, exercise.id, part, currentReview).map {
                case true  => ??? // Redirect(controllers.coll.routes.CollectionController.index(toolMain.urlPart))
                case false => ???
              }
            }
            ToolForms.exerciseReviewForm.bindFromRequest()(request).fold(onFormError, onFormRead)
        }
    }
  }

}
