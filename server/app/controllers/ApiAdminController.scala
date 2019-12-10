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
  configuration: Configuration,
  ws: WSClient
)(implicit ec: ExecutionContext) extends AbstractApiExerciseController(cc, configuration) {

  private val logger = Logger(classOf[ApiController])

  // FIXME: port 5050 in prod mode!
  private val resourcesServerBaseUrl = "http://localhost:5050/tools"

  override protected val adminRightsRequired: Boolean = true

  private implicit val cf: Format[ExerciseCollection] = ToolJsonProtocol.collectionFormat
  private implicit val ef: Format[Exercise]           = ToolJsonProtocol.exerciseFormat


  def readCollections(toolId: String): Action[AnyContent] = apiWithToolMain(toolId) { (_, _, toolMain) =>
    ws.url(s"$resourcesServerBaseUrl/${toolMain.urlPart}/collections")
      .get()
      .map { request => (request.json \ "collections").validate[Seq[ExerciseCollection]](Reads.seq(cf)) }
      .map {
        case JsSuccess(collections, _) => Ok(Json.toJson(collections))
        case JsError(errors)           =>
          errors.foreach(e => logger.error(e.toString))
          BadRequest("Could not read collections...")
      }
  }

  def readExercises(toolId: String, collId: Int): Action[AnyContent] = apiWithToolMain(toolId) { (_, _, toolMain) =>
    ws.url(s"$resourcesServerBaseUrl/${toolMain.urlPart}/collections/$collId/exercises")
      .get()
      .map { request => (request.json \ "exercises").validate[Seq[Exercise]](Reads.seq(ef)) }
      .map {
        case JsError(errors) =>
          errors.foreach(e => logger.error(e.toString))
          BadRequest("Could not read exercises...")

        case JsSuccess(exercises, _) =>

          // FIXME: validate ExerciseContent !?!
          val validExercises = exercises.filter { exercise =>
            toolMain.readExerciseContent(exercise)
              .fold(
                errors => {
                  errors.foreach(e => logger.error(e.toString))
                  false
                },
                _ => true
              )
          }

          println(exercises.size + " :: " + validExercises.size)

          Ok(Json.toJson(validExercises))
      }
  }

  def upsertCollection(toolId: String, collectionId: Int): Action[ExerciseCollection] =
    apiWithToolMain(toolId, parse.json[ExerciseCollection]) { (request, _, _) =>
      tables
        .futureUpsertCollection(request.body.copy(toolId = toolId, id = collectionId))
        .map { inserted => Ok(JsBoolean(inserted)) }
    }

  def upsertExercise(toolId: String, collId: Int, exId: Int): Action[Exercise] =
    apiWithToolMain(toolId, parse.json[Exercise]) { (request, _, _) =>
      // FIXME: validate content!

      tables
        .futureUpsertExercise(request.body.copy(toolId = toolId, collectionId = collId, id = exId))
        .map { inserted => Ok(JsBoolean(inserted)) }
    }

}
