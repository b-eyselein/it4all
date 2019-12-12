package controllers

import javax.inject.{Inject, Singleton}
import model.ExerciseReview
import model.core.ToolForms
import model.persistence.ExerciseTableDefs
import model.tools.collectionTools.{Exercise, ToolJsonProtocol}
import play.api.data.Form
import play.api.libs.json._
import play.api.mvc._
import play.api.{Configuration, Logger}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class ApiController @Inject()(cc: ControllerComponents, tables: ExerciseTableDefs, configuration: Configuration)(implicit ec: ExecutionContext)
  extends AbstractApiExerciseController(cc, configuration) {

  private val logger = Logger(classOf[ApiController])

  override protected val adminRightsRequired: Boolean = false


  def apiAllCollections(toolType: String): Action[AnyContent] = apiWithToolMain(toolType) { (_, _, toolMain) =>
    tables.futureAllCollections(toolMain.urlPart).map { collections =>
      Ok(Json.toJson(collections)(Writes.seq(ToolJsonProtocol.collectionFormat)))
    }
  }

  def apiCollection(toolType: String, collId: Int): Action[AnyContent] = apiWithToolMain(toolType) { (_, _, toolMain) =>
    tables.futureCollById(toolMain.urlPart, collId).map {
      case None             => NotFound(s"There is no such collection with id $collId for tool ${toolMain.toolName}")
      case Some(collection) => Ok(ToolJsonProtocol.collectionFormat.writes(collection))
    }
  }

  def apiExercises(toolType: String, collId: Int): Action[AnyContent] = apiWithToolMain(toolType) { (_, _, toolMain) =>
    // FIXME: send only id, title, ... ?
    tables.futureExercisesInColl(toolMain, collId).map { exercises: Seq[Exercise] =>
      Ok(Json.toJson(exercises)(Writes.seq(ToolJsonProtocol.exerciseFormat)))
    }
  }

  def apiExercise(toolType: String, collId: Int, exId: Int): Action[AnyContent] = apiWithToolMain(toolType) { (_, _, toolMain) =>
    tables.futureExerciseById(toolMain.urlPart, collId, exId).map {
      case None           => NotFound(s"There is no such exercise with id $exId for collection $collId")
      case Some(exercise) => Ok(Json.toJson(exercise)(ToolJsonProtocol.exerciseFormat))
    }
  }

  def apiCorrect(toolType: String, collId: Int, exId: Int, partStr: String): Action[JsValue] = apiWithToolMain(toolType, parse.json) { (request, user, toolMain) =>
    tables.futureCollectionAndExercise(toolMain.urlPart, collId, exId).flatMap {
      case None                         => Future.successful(onNoSuchExercise(collId, exId))
      case Some((collection, exercise)) =>

        toolMain.partTypeFromUrl(partStr) match {
          case None         => Future.successful(onNoSuchExercisePart(exercise, partStr))
          case Some(exPart) =>

            toolMain.readSolution(request.body).fold(
              errors => {
                errors.foreach(e => logger.error(e.toString()))
                ???
              },
              solution => {

                tables.futureInsertSolution(user, exercise, exPart, request.body)
                  .flatMap { inserted =>

                    toolMain.correctAbstract(user, solution, collection, exercise, exPart, inserted).map {
                      case Failure(error) =>
                        logger.error("There has been an internal correction error:", error)
                        BadRequest(Json.obj("msg" -> "Es gab einen internen Fehler bei der Korrektur!"))

                      case Success(result) => Ok(toolMain.onLiveCorrectionResult(result))
                    }
                  }
              }
            )
        }
    }
  }

  // Exercise review process

  def reviewExercisePart(toolType: String, collId: Int, id: Int, partStr: String): EssentialAction = apiWithToolMain(toolType) { (request, user, toolMain) =>
    tables.futureExerciseById(toolMain.urlPart, collId, id).flatMap {
      case None           => Future.successful(onNoSuchExercise(collId, id))
      case Some(exercise) =>

        toolMain.partTypeFromUrl(partStr) match {
          case None       => Future.successful(onNoSuchExercisePart(exercise, partStr))
          case Some(part) =>

            val onFormError: Form[ExerciseReview] => Future[Result] = { formWithErrors =>
              ???
            }

            val onFormRead: ExerciseReview => Future[Result] = { currentReview =>
              tables.futureSaveReview(toolMain, user.username, collId, exercise.id, part, currentReview).map {
                case true  => ??? // Redirect(controllers.coll.routes.CollectionController.index(toolMain.urlPart))
                case false => ???
              }
            }
            ToolForms.exerciseReviewForm.bindFromRequest()(request).fold(onFormError, onFormRead)
        }
    }
  }

}
