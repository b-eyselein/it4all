package controllers

import javax.inject.{Inject, Singleton}
import model._
import model.toolMains.ToolList
import play.api.libs.json._
import play.api.mvc._
import play.api.{Configuration, Logger}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class ApiController @Inject()(cc: ControllerComponents, tl: ToolList, configuration: Configuration)(implicit ec: ExecutionContext)
  extends ApiControllerBasics(cc, tl, configuration) {

  private val logger = Logger(classOf[ApiController])


  override protected val adminRightsRequired: Boolean = false


  def apiAllCollections(toolType: String): Action[AnyContent] = apiWithToolMain(toolType) { (_, _, toolMain) =>
    toolMain.futureAllCollections.map { collections =>
      Ok(Json.toJson(collections)(Writes.seq(JsonProtocol.collectionFormat)))
    }
  }

  def apiCollection(toolType: String, collId: Int): Action[AnyContent] = apiWithToolMain(toolType) { (_, _, toolMain) =>
    toolMain.futureCollById(collId).map {
      case None             => NotFound(s"There is no such collection with id $collId for tool ${toolMain.toolname}")
      case Some(collection) => Ok(Json.toJson(collection)(JsonProtocol.collectionFormat))
    }
  }

  def apiExercises(toolType: String, collId: Int): Action[AnyContent] = apiWithToolMain(toolType) { (_, _, toolMain) =>
    // FIXME: send only id, title, ...
    toolMain.futureExercisesInColl(collId).map { exercises =>
      Ok(Json.toJson(exercises)(Writes.seq(toolMain.exerciseJsonFormat)))
    }
  }

  def apiExercise(toolType: String, collId: Int, exId: Int): Action[AnyContent] = apiWithToolMain(toolType) { (_, _, toolMain) =>
    toolMain.futureExerciseById(collId, exId).map {
      case None           => NotFound(s"There is no such exercise with id $exId for collection $collId")
      case Some(exercise) => Ok(Json.toJson(exercise)(toolMain.exerciseJsonFormat))
    }
  }

  def apiCorrect(toolType: String, collId: Int, exId: Int, partStr: String): Action[AnyContent] = apiWithToolMain(toolType) { (request, user, toolMain) =>
    toolMain.futureCollById(collId) flatMap {
      case None             => Future.successful(onNoSuchCollection(toolMain, collId))
      case Some(collection) =>

        toolMain.futureExerciseById(collection.id, exId) flatMap {
          case None           => Future.successful(onNoSuchExercise(toolMain, collection, exId))
          case Some(exercise) =>

            toolMain.partTypeFromUrl(partStr) match {
              case None         => Future.successful(onNoSuchExercisePart(toolMain, exercise, partStr))
              case Some(exPart) =>

                toolMain.correctAbstract(user, collection, exercise, exPart)(request, ec).map {
                  case Success(result) => Ok(toolMain.onLiveCorrectionResult(result))
                  case Failure(error)  =>
                    logger.error("There has been an internal correction error:", error)
                    BadRequest(toolMain.onLiveCorrectionError(error))
                }
            }
        }
    }
  }

}
