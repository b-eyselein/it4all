package controllers.core.excontrollers

import controllers.core.BaseAdminController
import model.core._
import model.core.tools.ExToolObject
import model.{Exercise, User}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import play.api.mvc._
import play.twirl.api.Html

import scala.concurrent.ExecutionContext

abstract class AExerciseAdminController[E <: Exercise]
(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository, t: ExToolObject)(implicit ec: ExecutionContext)
  extends BaseAdminController[E](cc, dbcp, r, t) with Secured {

  def changeExState(id: Int): EssentialAction = withAdmin { _ =>
    implicit request =>
      //      finder.byId(id) match {
      //        case None => BadRequest(Json.obj("message" -> "No such file exists..."))
      //
      //        case Some(exercise) =>
      //          exercise.state = ExerciseStateHelper.byName(request.body.asFormUrlEncoded.get("state").mkString).getOrElse(RESERVED)
      //          //          exercise.save()
      //          Ok(Json.obj("id" -> id, "newState" -> exercise.state.toString))
      //      }
      Ok("TODO")
  }

  def deleteExercise(id: Int): EssentialAction = withAdmin { _ =>
    implicit request =>
      //      finder.byId(id) match {
      //        case None    =>
      //          BadRequest(Json.obj("message" -> s"Die Aufgabe mit ID $id existiert nicht und kann daher nicht geloescht werden!"))
      //        case Some(_) =>
      //          //          if (toDelete.delete()) {
      //          //            Ok(Json.obj("id" -> id))
      //          //          } else {
      //          BadRequest(Json.obj("message" -> s"Es gab einen internen Fehler beim Loeschen der Aufgabe mit der ID $id"))
      //        //          }
      //      }
      Ok("TODO")
  }

  def editExercise(id: Int): EssentialAction = withAdmin { _ =>
    implicit request =>
      //    exerciseReader.initFromForm(id, factory.form().bindFromRequest()) match {
      //      case error: ReadingError =>
      //        BadRequest(views.html.jsonReadingError.render(admin, error))
      //      case _: ReadingFailure => BadRequest("There has been an error...")
      //      case result: ReadingResult[E] =>
      //
      //        result.read.foreach(res => exerciseReader.save(res.read))
      //        Ok(views.html.admin.preview.render(admin, toolObject, result.read))
      //    }
      Ok("TODO!")
  }

  def editExerciseForm(id: Int): EssentialAction = futureWithAdmin { admin =>
    implicit request => exById(id).map(ex => Ok(views.html.admin.editExForm(admin, toolObject, ex, renderEditRest(ex))))
  }

  def exercises: EssentialAction = futureWithAdmin { admin =>
    implicit request =>
      allExes.map(exes => Ok(views.html.admin.exerciseList.render(admin, exes, toolObject)))
  }

  def exportExercises: EssentialAction = withAdmin { admin =>
    implicit request =>
      Ok(views.html.admin.export.render(admin, Json.prettyPrint(null /*Json.toJson(finder.all)*/)))
  }

  def newExerciseForm: EssentialAction = withAdmin { admin =>
    // FIXME
    implicit request =>
      // FIXME: ID of new exercise?
      Ok(views.html.admin.editExForm.render(admin, toolObject, None, renderEditRest(None)))
  }

  def newExercise: EssentialAction = withAdmin { admin =>
    implicit request =>
      Ok("TODO: Not yet used...")
  }

  // Helper methods

  def renderEditRest(exercise: Option[E]): Html = new Html("")

  def renderExercises(exercises: List[E]): Html = new Html("") //views.html.admin.exercisesTable.render(exercises, toolObject)

  def renderExesCreated(admin: User, exercises: List[E]): Html = views.html.admin.preview.render(admin, exercises, toolObject)

}
