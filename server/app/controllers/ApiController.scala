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

  def apiExerciseBasics(toolType: String, collId: Int): Action[AnyContent] = apiWithToolMain(toolType) { (_, _, toolMain) =>
    tables.futureExerciseBasicsInColl(toolMain, collId).map { exerciseBasics =>
      Ok(Json.toJson(exerciseBasics)(Writes.seq(ToolJsonProtocol.exerciseBasicsFormat)))
    }
  }

  def apiExercises(toolType: String, collId: Int): Action[AnyContent] = apiWithToolMain(toolType) { (_, _, toolMain) =>
    // FIXME: send only id, title, ...
    tables.futureExercisesInColl(toolMain, collId).map { exercises: Seq[Exercise] =>
      Ok(Json.toJson(exercises)(Writes.seq(toolMain.exerciseFormat)))
    }
  }

  def apiExercise(toolType: String, collId: Int, exId: Int): Action[AnyContent] = apiWithToolMain(toolType) { (_, _, toolMain) =>
    tables.futureExerciseById(toolMain.urlPart, collId, exId).map {
      case None           => NotFound(s"There is no such exercise with id $exId for collection $collId")
      case Some(exercise) => Ok(Json.toJson(exercise)(toolMain.exerciseFormat))
    }
  }

  def apiCorrect(toolType: String, collId: Int, exId: Int, partStr: String): Action[AnyContent] = apiWithToolMain(toolType) { (request, user, toolMain) =>
    tables.futureCollById(toolMain.urlPart, collId) flatMap {
      case None             => Future.successful(onNoSuchCollection(toolMain.urlPart, collId))
      case Some(collection) =>

        tables.futureExerciseById(toolMain.urlPart, collection.id, exId) flatMap {
          case None           => Future.successful(onNoSuchExercise(collection, exId))
          case Some(exercise) =>

            toolMain.partTypeFromUrl(partStr) match {
              case None         => Future.successful(onNoSuchExercisePart(exercise, partStr))
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


  // Exercise review process

  def reviewExercisePart(toolType: String, collId: Int, id: Int, partStr: String): EssentialAction = apiWithToolMain(toolType) { (request, user, toolMain) =>
    tables.futureCollById(toolMain.urlPart, collId) flatMap {
      case None             => Future.successful(onNoSuchCollection(toolMain.urlPart, collId))
      case Some(collection) =>

        tables.futureExerciseById(toolMain.urlPart, collection.id, id) flatMap {
          case None           => Future.successful(onNoSuchExercise(collection, id))
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

}
