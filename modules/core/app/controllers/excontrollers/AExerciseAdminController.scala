package controllers.excontrollers

import controllers.core.BaseAdminController
import io.ebean.Finder
import model.exercise.{Exercise, ExerciseState}
import model.exercisereading._
import model.tools.ExToolObject
import model.user.User
import play.api.libs.json.Json
import play.api.mvc.ControllerComponents
import play.mvc.Security.Authenticated
import play.twirl.api.Html

import scala.collection.JavaConverters._

@Authenticated(classOf[model.AdminSecured])
abstract class AExerciseAdminController[E <: Exercise]
(cc: ControllerComponents, t: ExToolObject, fi: Finder[Integer, E], val exerciseReader: ExerciseReader[E])
  extends BaseAdminController[E](cc, t, fi, exerciseReader) {

  def changeExState(id: Int) = Action { implicit request =>
    Option(finder.byId(id)) match {
      case None => BadRequest(Json.obj("message" -> "No such file exists..."))

      case Some(exercise) =>
        exercise.state = ExerciseState.valueOf(request.body.asFormUrlEncoded.get("state").mkString)
        exercise.save()
        Ok(Json.obj("id" -> id, "newState" -> exercise.state.toString))
    }
  }

  def deleteExercise(id: Int) = Action { implicit request =>
    Option(finder.byId(id)) match {
      case None =>
        BadRequest(Json.obj("message" -> s"Die Aufgabe mit ID $id existiert nicht und kann daher nicht geloescht werden!"))
      case Some(toDelete) =>
        if (toDelete.delete()) {
          Ok(Json.obj("id" -> id))
        } else {
          BadRequest(Json.obj("message" -> s"Es gab einen internen Fehler beim Loeschen der Aufgabe mit der ID $id"))
        }
    }
  }

  def editExercise(id: Int) = Action { implicit request =>
    //    exerciseReader.initFromForm(id, factory.form().bindFromRequest()) match {
    //      case error: ReadingError =>
    //        BadRequest(views.html.jsonReadingError.render(getUser, error))
    //      case _: ReadingFailure => BadRequest("There has been an error...")
    //      case result: ReadingResult[E] =>
    //
    //        result.read.foreach(res => exerciseReader.save(res.read))
    //        Ok(views.html.admin.preview.render(getUser, toolObject, result.read))
    //    }
    Ok("TODO!")
  }

  def editExerciseForm(id: Int) = Action { implicit request =>
    Option(finder.byId(id)) match {
      case None => BadRequest("")
      case Some(exercise) => Ok(renderExEditForm(getUser, exercise, isCreation = false))
    }
  }

  def exercises = Action { implicit request =>
    Ok(views.html.admin.exerciseList.render(getUser, finder.all.asScala.toList, toolObject))
  }

  def exportExercises = Action { implicit request =>
    Ok(views.html.admin.export.render(getUser, Json.prettyPrint(null /*Json.toJson(finder.all)*/)))
  }

  def importExercises = Action { implicit request =>
    exerciseReader.readFromJsonFile() match {
      case error: ReadingError =>
        BadRequest(views.html.jsonReadingError.render(getUser, error))

      case _: ReadingFailure => BadRequest("There has been an error...")

      case result: ReadingResult[E] =>
        result.read.foreach(read => {
          exerciseReader.save(read.read)
          read.fileResults = exerciseReader.checkFiles(read.read)
        })
        Ok(views.html.admin.preview.render(getUser, toolObject, result.read))
    }
  }

  def newExerciseForm = Action { implicit request =>
    val exercise = exerciseReader.getOrInstantiateExercise(ExerciseReader.findMinimalNotUsedId(finder))
    exerciseReader.save(exercise)
    Ok(renderExEditForm(getUser, exercise, isCreation = true))
  }

  def uploadFile = super.uploadFile(_ => new Html("") /*renderExesCreated*/)


  def renderExEditForm(user: User, exercise: E, isCreation: Boolean): Html =
    views.html.admin.editExForm.render(user, toolObject, exercise, isCreation)

  def renderExercises(exercises: List[E]): Html = views.html.admin.exercisesTable.render(exercises, toolObject)

  def renderExesCreated(user: User, exercises: List[SingleReadingResult[E]]): Html = views.html.admin.preview.render(user, toolObject, exercises)

}
