package controllers

import javax.inject.{Inject, Singleton}
import model.persistence.ExerciseTableDefs
import model.tools.collectionTools.{Exercise, ExerciseCollection, ToolJsonProtocol}
import play.api.libs.json._
import play.api.libs.ws.WSClient
import play.api.mvc._
import play.api.{Configuration, Logger}

import scala.concurrent.ExecutionContext

@Singleton
class ApiAdminController @Inject()(
  cc: ControllerComponents,
  tables: ExerciseTableDefs,
  override protected val configuration: Configuration,
  ws: WSClient
)(implicit val ec: ExecutionContext) extends AbstractController(cc) with AbstractApiExerciseController {

  private val logger = Logger(classOf[ApiController])

  // FIXME: port 5050 in prod mode!
  private val resourcesServerBaseUrl = "http://localhost:5050/tools"

  override protected val adminRightsRequired: Boolean = true

  private implicit val cf: Format[ExerciseCollection] = ToolJsonProtocol.collectionFormat
  private implicit val ef: Format[Exercise]           = ToolJsonProtocol.exerciseFormat


  def readCollections(toolId: String): Action[AnyContent] = JwtAuthenticatedToolMainAction(toolId).async { implicit request =>
    ws.url(s"$resourcesServerBaseUrl/${request.toolMain.urlPart}/collections")
      .get()
      .map { request => (request.json \ "collections").validate[Seq[ExerciseCollection]](Reads.seq(cf)) }
      .map {
        case JsSuccess(collections, _) => Ok(Json.toJson(collections))
        case JsError(errors)           =>
          errors.foreach(e => logger.error(e.toString))
          BadRequest("Could not read collections...")
      }
  }

  def readExercises(toolId: String, collId: Int): Action[AnyContent] = JwtAuthenticatedToolMainAction(toolId).async { request =>
    ws.url(s"$resourcesServerBaseUrl/${request.toolMain.urlPart}/collections/${String.valueOf(collId)}/exercises")
      .get()
      .map { request => (request.json \ "exercises").validate[Seq[Exercise]](Reads.seq(ef)) }
      .map {
        case JsError(errors) =>
          errors.foreach(e => logger.error(e.toString))
          BadRequest("Could not read exercises...")

        case JsSuccess(exercises, _) =>

          // FIXME: validate ExerciseContent !?!
          val validExercises = exercises.filter { exercise =>
            request.toolMain.readExerciseContent(exercise)
              .fold(
                errors => {
                  errors.foreach(e => logger.error(e.toString))
                  false
                },
                _ => true
              )
          }

          Ok(Json.toJson(validExercises))
      }
  }

  def upsertCollection(toolId: String, collectionId: Int): Action[ExerciseCollection] =
    JwtAuthenticatedToolMainAction(toolId)(parse.json[ExerciseCollection]).async { implicit request =>
      tables
        .futureUpsertCollection(request.body.copy(toolId = toolId, id = collectionId))
        .map { inserted => Ok(JsBoolean(inserted)) }
    }

  def upsertExercise(toolId: String, collId: Int, exId: Int): Action[Exercise] =
    JwtAuthenticatedToolMainAction(toolId)(parse.json[Exercise]).async { implicit request =>
      // FIXME: validate content!
      tables
        .futureUpsertExercise(request.body.copy(toolId = toolId, collectionId = collId, id = exId))
        .map { inserted => Ok(JsBoolean(inserted)) }
    }

}
