package controllers.core

import io.ebean.Finder
import model.exercise.{Exercise, ExerciseState}
import model.exercisereading._
import model.tools.ExToolObject
import model.user.User
import play.data.FormFactory
import play.libs.Json
import play.mvc.Result
import play.mvc.Results._
import play.mvc.Security.Authenticated
import play.twirl.api.Html

import scala.collection.JavaConverters._

@Authenticated(classOf[model.AdminSecured])
abstract class AExerciseAdminController[E <: Exercise]
(f: FormFactory, t: ExToolObject, fi: Finder[Integer, E], val exerciseReader: ExerciseReader[E])
  extends BaseAdminController[E](f, t, fi, exerciseReader) {

  def changeExState(id: Int): Result = Option(finder.byId(id)) match {
    case None => badRequest(Json.parse("{No such file exists...}"))
    case Some(exercise) =>
      exercise.state = ExerciseState.valueOf(factory.form().bindFromRequest().get("state"))
      exercise.save()

      ok(Json.parse(s"""{"id": "$id", "newState": "${exercise.state}"}"""))
  }

  def deleteExercise(id: Int): Result = Option(finder.byId(id)) match {
    case None => badRequest(Json.parse(
      s"""{"message": "Die Aufgabe mit ID $id existiert nicht und kann daher nicht geloescht werden!"""
    ))
    case Some(toDelete) =>
      if (toDelete.delete()) {
        ok(Json.parse(s"""{"id": "$id"}"""))
      } else {
        badRequest(
          Json.parse(s"""{"message": "Es gab einen internen Fehler beim Loeschen der Aufgabe mit der ID $id}""")
        )
      }
  }

  def editExercise(id: Int): Result = exerciseReader.initFromForm(id, factory.form().bindFromRequest()) match {
    case error: ReadingError =>
      badRequest(views.html.jsonReadingError.render(getUser, error))
    case _: ReadingFailure => badRequest("There has been an error...")
    case result: ReadingResult[E] =>

      exerciseReader.save(exercise)
      ok(views.html.admin.preview.render(getUser, toolObject, List(exercise)))
  }

  def editExerciseForm(id: Int): Result = Option(finder.byId(id)) match {
    case None => badRequest("")
    case Some(exercise) => ok(renderExEditForm(getUser, exercise, isCreation = false))
  }

  def exercises: Result =
    ok(views.html.admin.exerciseList.render(getUser, finder.all.asScala.toList, toolObject))

  def exportExercises: Result =
    ok(views.html.admin.export.render(getUser, Json.prettyPrint(Json.toJson(finder.all))))

  def importExercises: Result = exerciseReader.readFromJsonFile() match {
    case error: ReadingError =>
      badRequest(views.html.jsonReadingError.render(getUser, error))

    case _: ReadingFailure => badRequest("There has been an error...")

    case result: ReadingResult[E] =>
      result.read.foreach(read => {
        exerciseReader.save(read.read)
        read.fileResults = exerciseReader.checkFiles(read.read)
      })
      ok(views.html.admin.preview.render(getUser, toolObject, result.read))
  }

  def newExerciseForm: Result = {
    val exercise = exerciseReader.getOrInstantiateExercise(ExerciseReader.findMinimalNotUsedId(finder))
    exerciseReader.save(exercise)
    ok(renderExEditForm(getUser, exercise, isCreation = true))
  }

  def uploadFile: Result = uploadFile(renderExesCreated)

  def renderExEditForm(user: User, exercise: E, isCreation: Boolean): Html =
    views.html.admin.editExForm.render(user, toolObject, exercise, isCreation)

  def renderExercises(exercises: List[E]): Html = views.html.admin.exercisesTable.render(exercises, toolObject)

  def renderExesCreated(exercises: List[SingleReadingResult[E]]): Html = views.html.admin.exercisesCreated.render(exercises, toolObject)

}
