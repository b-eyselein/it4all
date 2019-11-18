package controllers

import javax.inject.{Inject, Singleton}
import model.toolMains.ToolList
import model.{ExerciseCollection, JsonProtocol}
import play.api.libs.json._
import play.api.mvc._
import play.api.{Configuration, Logger}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class ApiAdminController @Inject()(cc: ControllerComponents, tl: ToolList, configuration: Configuration)(implicit ec: ExecutionContext)
  extends AbstractApiExerciseController(cc, tl, configuration) {

  private val logger = Logger(classOf[ApiController])


  override protected val adminRightsRequired: Boolean = true


  def readCollections(toolId: String): Action[AnyContent] = apiWithToolMain(toolId) { (_, _, toolMain) =>

    val successfulReadCollections: Seq[ExerciseCollection] = toolMain.readCollectionsFromYaml
      .tapEach {
        case Success(_)         => ()
        case Failure(exception) => logger.error("Error while reading yaml", exception)
      }
      .flatMap(_.toOption) // filter out Failures

    Future.successful(Ok(Json.toJson(successfulReadCollections)(Writes.seq(JsonProtocol.collectionFormat))))
  }

  def readExercises(toolId: String, collId: Int): Action[AnyContent] = apiWithToolMain(toolId) { (_, _, toolMain) =>

    toolMain.futureCollById(collId).map {
      case None             => NotFound(s"There is no such collection $collId")
      case Some(collection) =>

        val successfulReadExercises: Seq[toolMain.ExType] = toolMain.readExercisesFromYaml(collection)
          .tapEach {
            case Success(_)         => ()
            case Failure(exception) => logger.error("Error while reading yaml", exception)
          }
          .flatMap(_.toOption) // filter out Failures

        Ok(Json.toJson(successfulReadExercises)(Writes.seq(toolMain.exerciseJsonFormat)))
    }
  }

  def upsertCollection(toolId: String, collId: Int): Action[AnyContent] = apiWithToolMain(toolId) { (request, _, toolMain) =>

    request.body.asJson match {
      case None          => Future.successful(BadRequest("No json was provided in body!"))
      case Some(jsValue) =>

        JsonProtocol.collectionFormat.reads(jsValue) match {
          case JsError(errors)             =>
            errors.foreach(e => logger.error(e.toString()))
            Future.successful(BadRequest("Json in body could not be interpreted as ExerciseCollection!"))
          case JsSuccess(newCollection, _) =>

            // FIXME: check that toolId and collId fit!

            toolMain.futureDeleteOldAndInsertNewCollection(newCollection).map { inserted =>
              Ok(JsBoolean(inserted))
            }
        }
    }
  }

  def upsertExercise(toolId: String, collId: Int, exId: Int): Action[AnyContent] = apiWithToolMain(toolId) { (request, _, toolMain) =>

    request.body.asJson match {
      case None          => Future.successful(BadRequest("No json was provided in body!"))
      case Some(jsValue) =>

        toolMain.exerciseJsonFormat.reads(jsValue) match {
          case JsError(errors)           =>
            errors.foreach(e => logger.error(e.toString()))
            Future.successful(BadRequest("Json in body could not be interpreted as Exercise!"))
          case JsSuccess(newExercise, _) =>

            // FIXME: check that toolId, collId and exId fit!

            toolMain.futureDeleteOldAndInsertNewExercise(collId, newExercise).map { inserted =>
              Ok(JsBoolean(inserted))
            }
        }
    }
  }

}
