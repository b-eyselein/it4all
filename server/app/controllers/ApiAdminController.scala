package controllers

import javax.inject.{Inject, Singleton}
import model.persistence.ExerciseTableDefs
import model.tools.collectionTools.{Exercise, ExerciseCollection, ToolJsonProtocol}
import play.api.libs.json.{Writes, _}
import play.api.libs.ws.WSClient
import play.api.mvc._
import play.api.{Configuration, Logger}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ApiAdminController @Inject()(
  cc: ControllerComponents,
  tables: ExerciseTableDefs,
  configuration: Configuration,
  ws: WSClient
)(implicit ec: ExecutionContext) extends AbstractApiExerciseController(cc, configuration) {

  private val logger = Logger(classOf[ApiController])

  private val resourcesServerBaseUrl = "http://localhost:5050/tools"

  override protected val adminRightsRequired: Boolean = true


  def readCollections(toolId: String): Action[AnyContent] = apiWithToolMain(toolId) { (_, _, toolMain) =>
    implicit val exerciseCollectionFormat: Format[Seq[ExerciseCollection]] = Format(
      Reads.seq(ToolJsonProtocol.collectionFormat),
      Writes.seq(ToolJsonProtocol.collectionFormat)
    )

    ws.url(s"$resourcesServerBaseUrl/${toolMain.urlPart}/collections")
      .get()
      .map { request => (request.json \ "collections").validate[Seq[ExerciseCollection]] }
      .map {
        case JsSuccess(collections, _) => Ok(Json.toJson(collections))
        case JsError(errors)           =>
          errors.foreach(e => logger.error(e.toString))
          BadRequest("Could not read collections...")
      }
  }

  def readExercises(toolId: String, collId: Int): Action[AnyContent] = apiWithToolMain(toolId) { (_, _, toolMain) =>

    implicit val exerciseSeqFormat: Format[Seq[Exercise]] = Format(
      Reads.seq(ToolJsonProtocol.exerciseFormat),
      Writes.seq(ToolJsonProtocol.exerciseFormat)
    )

    tables.futureCollById(toolMain.urlPart, collId).flatMap {
      case None             => Future(NotFound(s"There is no such collection $collId"))
      case Some(collection) =>

        ws.url(s"$resourcesServerBaseUrl/${toolMain.urlPart}/collections/${collection.id}/exercises")
          .get()
          .map { request => (request.json \ "exercises").validate[Seq[Exercise]] }
          .map {
            case JsSuccess(exercises, _) =>

              exercises.foreach { exercise =>
                toolMain.readExerciseContent(exercise).fold(
                  errors => {
                    errors.foreach(e => logger.error(e.toString))
                    ???
                  },
                  _ => ()
                )
              }

              // FIXME: validate ExerciseContent !?!
              Ok(Json.toJson(exercises))
            case JsError(errors)         =>
              errors.foreach(e => logger.error(e.toString))
              BadRequest("Could not read exercises...")
          }
    }
  }

  def upsertCollection(toolId: String, collectionId: Int): Action[AnyContent] = apiWithToolMain(toolId) { (request, _, toolMain) =>

    val onError: JsErrorsType => Future[Result] = { errors =>
      errors.foreach(e => logger.error(e.toString()))
      Future.successful(BadRequest("Json in body could not be interpreted as ExerciseCollection!"))
    }

    val onRead: ExerciseCollection => Future[Result] = { newCollection =>
      val collectionToInsert: ExerciseCollection = newCollection.copy(toolId = toolMain.urlPart, id = collectionId)

      tables.futureUpsertCollection(collectionToInsert).map { inserted => Ok(JsBoolean(inserted)) }
    }


    request.body.asJson match {
      case None          => Future.successful(BadRequest("No json was provided in body!"))
      case Some(jsValue) => ToolJsonProtocol.collectionFormat.reads(jsValue).fold(onError, onRead)
    }
  }

  def upsertExercise(toolId: String, collId: Int, exId: Int): Action[AnyContent] = apiWithToolMain(toolId) { (request, _, toolMain) =>

    val onRead: Exercise => Future[Result] = { readExercise =>
      val toInsert: Exercise = readExercise.copy(toolId = toolMain.urlPart, collectionId = collId, id = exId)

      // FIXME: validate content!

      tables.futureUpsertExercise(toInsert).map { inserted => Ok(JsBoolean(inserted)) }
    }

    def onError: JsErrorsType => Future[Result] = { errors =>
      errors.foreach(e => logger.error(e.toString()))
      Future.successful(BadRequest("Json in body could not be interpreted as Exercise!"))
    }

    request.body.asJson match {
      case None          => Future.successful(BadRequest("No json was provided in body!"))
      case Some(jsValue) => ToolJsonProtocol.exerciseFormat.reads(jsValue).fold(errors => onError(errors), onRead)
    }
  }

}
