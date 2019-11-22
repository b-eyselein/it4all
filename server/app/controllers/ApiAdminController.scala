package controllers

import javax.inject.{Inject, Singleton}
import model.persistence.ExerciseTableDefs
import model.tools.collectionTools.{Exercise, ExerciseCollection, ToolJsonProtocol}
import play.api.libs.json._
import play.api.mvc._
import play.api.{Configuration, Logger}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class ApiAdminController @Inject()(cc: ControllerComponents, tables: ExerciseTableDefs, configuration: Configuration)(implicit ec: ExecutionContext)
  extends AbstractApiExerciseController(cc, configuration) {

  private val logger = Logger(classOf[ApiController])


  private type JsErrorsType = scala.collection.Seq[(JsPath, scala.collection.Seq[JsonValidationError])]


  override protected val adminRightsRequired: Boolean = true


  def readCollections(toolId: String): Action[AnyContent] = apiWithToolMain(toolId) { (_, _, toolMain) =>

    val successfulReadCollections: Seq[ExerciseCollection] = toolMain.readCollectionsFromYaml
      .tapEach {
        case Success(_)         => ()
        case Failure(exception) => logger.error("Error while reading yaml", exception)
      }
      .flatMap(_.toOption) // filter out Failures

    Future.successful(Ok(Json.toJson(successfulReadCollections)(Writes.seq(ToolJsonProtocol.collectionFormat))))
  }

  def readExercises(toolId: String, collId: Int): Action[AnyContent] = apiWithToolMain(toolId) { (_, _, toolMain) =>

    tables.futureCollById(toolMain.urlPart, collId).map {
      case None             => NotFound(s"There is no such collection $collId")
      case Some(collection) =>

        val successfulReadExercises: Seq[Exercise] = toolMain.readExercisesFromYaml(collection)
          .tapEach {
            case Success(_)         => ()
            case Failure(exception) => logger.error("Error while reading yaml", exception)
          }
          .flatMap(_.toOption) // filter out Failures

        Ok(Json.toJson(successfulReadExercises)(Writes.seq(toolMain.exerciseFormat)))
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

      tables.futureUpsertExercise(toInsert).map { inserted => Ok(JsBoolean(inserted)) }
    }

    def onError: JsErrorsType => Future[Result] = { errors =>
      errors.foreach(e => logger.error(e.toString()))
      Future.successful(BadRequest("Json in body could not be interpreted as Exercise!"))
    }

    request.body.asJson match {
      case None          => Future.successful(BadRequest("No json was provided in body!"))
      case Some(jsValue) => toolMain.exerciseFormat.reads(jsValue).fold(errors => onError(errors), onRead)
    }
  }

}
