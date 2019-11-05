package controllers.coll

import controllers.Secured
import javax.inject.{Inject, Singleton}
import model.ExerciseState
import model.core.CoreConsts._
import model.core._
import model.toolMains.{CollectionToolMain, ToolList}
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms.single
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json
import play.api.mvc._
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CollectionAdminController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, tl: ToolList, val repository: Repository)(implicit ec: ExecutionContext)
  extends AToolAdminController(cc, dbcp, tl) with HasDatabaseConfigProvider[JdbcProfile] with Secured with play.api.i18n.I18nSupport {

  private val logger = Logger(classOf[CollectionAdminController])

  override protected type ToolMainType = CollectionToolMain

  override protected def getToolMain(toolType: String): Option[CollectionToolMain] = toolList.getExCollToolMainOption(toolType)

  // Helpers

  private val stateForm: Form[ExerciseState] = Form(single("state" -> ExerciseState.formField))

  // Routes

  def adminChangeCollectionState(tool: String, id: Int): EssentialAction = futureWithUserWithToolMain(tool) { (_, toolMain) =>
    implicit request =>

      val onFormError: Form[ExerciseState] => Future[Result] = { formWithErrors =>
        for (formError <- formWithErrors.errors)
          logger.error(s"Form error while changinge state of exercise $id: ${formError.message}")
        Future(BadRequest("There has been an error!"))
      }

      def onFormRead(toolMain: CollectionToolMain): ExerciseState => Future[Result] = { newState =>
        toolMain.updateCollectionState(id, newState) map {
          case true  => Ok(Json.obj(idName -> id, "newState" -> newState.entryName))
          case false => BadRequest(Json.obj(messageName -> "Could not update exercise!"))
        }
      }

      stateForm.bindFromRequest().fold(onFormError, onFormRead(toolMain))
  }

  // Creating, updating and removing collections

  def adminEditCollection(tool: String, id: Int): EssentialAction = futureWithUserWithToolMain(tool) { (_, _) =>
    implicit request =>
      // FIXME: implement: editing of collection!
      Future(Ok("TODO: Editing collection...!"))
  }

  def adminCreateCollection(toolType: String): EssentialAction = futureWithUserWithToolMain(toolType) { (_, _) =>
    implicit request =>
      // FIXME: implement: creation of collection!
      Future(Ok("TODO: Creating new collection...!"))
  }

  def adminDeleteCollection(tool: String, id: Int): EssentialAction = futureWithUserWithToolMain(tool) { (_, toolMain) =>
    implicit request =>
      toolMain.futureDeleteCollection(id) map {
        case true  => Ok(Json.obj(idName -> id))
        case false => NotFound(Json.obj(messageName -> s"Die Sammlung mit ID $id existiert nicht und kann daher nicht geloescht werden!"))
      }
  }

  // Administrate exercises in collection

  def adminChangeExerciseState(tool: String, collId: Int, exId: Int): EssentialAction = futureWithUserWithToolMain(tool) { (_, toolMain) =>
    implicit request =>

      val onFormError: Form[ExerciseState] => Future[Result] = { formWithErrors =>
        for (formError <- formWithErrors.errors)
          logger.error(s"Form error while changinge state of exercise $exId: ${formError.message}")
        Future(BadRequest("There has been an error!"))
      }

      def onFormRead(toolMain: CollectionToolMain): ExerciseState => Future[Result] = { newState =>
        toolMain.updateExerciseState(collId, exId, newState) map {
          case true  => Ok(Json.obj(idName -> exId, "newState" -> newState.entryName))
          case false => BadRequest(Json.obj(messageName -> "Could not update exercise!"))
        }
      }

      stateForm.bindFromRequest().fold(onFormError, onFormRead(toolMain))
  }

  // Creating, updating and removing exercises

  def adminEditExercise(toolType: String, collId: Int, id: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (admin, toolMain) =>
    implicit request =>

      //      def onFormError: Form[toolMain.ExType] => Future[Result] = { formWithError =>
      //
      //        for (formError <- formWithError.errors)
      //          Logger.error(formError.key + " :: " + formError.message)
      //
      //        Future(BadRequest("Your form has has errors!"))
      //      }

      //      def onFormSuccess: toolMain.ExType => Future[Result] = { compEx =>
      // //        FIXME: save ex
      //        toolMain.futureUpdateExercise(compEx) map { _ =>
      //          Ok(views.html.admin.singleExercisePreview(admin, compEx, toolMain))
      //        }
      //      }

      //      toolMain.compExForm.bindFromRequest().fold(onFormError, onFormSuccess)
      ???
  }

  def adminCreateExercise(toolType: String, collId: Int): EssentialAction = ???


  def adminDeleteExercise(tool: String, collId: Int, exId: Int): EssentialAction = futureWithUserWithToolMain(tool) { (_, toolMain) =>
    implicit request =>
      toolMain.futureDeleteExercise(collId, exId) map {
        case true  => Ok(Json.obj(idName -> exId))
        case false => NotFound(Json.obj(messageName -> s"Die Aufgabe mit ID $exId existiert nicht und kann daher nicht geloescht werden!"))
      }
  }

  // Reviews

  def showReviews(toolType: String, collId: Int, id: Int): EssentialAction = futureWithUserWithToolMain(toolType) { (admin, toolMain) =>
    implicit request =>
      //      toolMain.futureExerciseById(collId, id) flatMap {
      //        case None    => Future(onNoSuchExercise(id))
      //        case Some(_) => toolMain.futureReviewsForExercise(id) map {
      //          reviews => Ok(views.html.admin.idExes.idExerciseReviewListExercise(admin, reviews, toolList, toolMain))
      //        }
      //      }
      ???
  }

}
