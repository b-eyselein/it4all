package controllers.core

import io.ebean.Finder
import model.exercise.{Exercise, ExerciseState}
import model.exercisereading.ExerciseReader
import model.tools.IdExToolObject
import model.user.User
import play.api.Configuration
import play.data.FormFactory
import play.libs.Json
import play.mvc.Security.Authenticated
import play.mvc.{Result, Results}
import play.twirl.api.Html

import scala.collection.JavaConverters.seqAsJavaListConverter

@Authenticated(classOf[model.AdminSecured])
abstract class AExerciseAdminController[E <: Exercise]
(c: Configuration, f: FormFactory, val toolObject: IdExToolObject, val finder: Finder[Integer, E], val exerciseReader: ExerciseReader[E])
  extends BaseAdminController[E](c, f, exerciseReader) {

  def changeExState(id: Int): Result = {
    val exercise = finder.byId(id)
    val newState = ExerciseState.valueOf(factory.form().bindFromRequest().get("state"))

    exercise.state = newState
    exercise.save()

    Results.ok(Json.parse(s"""{"id": "$id", "newState": "${exercise.state}"}"""))
  }

  def deleteExercise(id: Int): Result = Option(finder.byId(id)) match {
    case None =>
      Results.badRequest(Json.parse(
        s"""{"message": "Die Aufgabe mit ID $id existiert nicht und kann daher nicht geloescht werden!"""
      ))
    case Some(toDelete) =>
      if (toDelete.delete()) {
        Results.ok(Json.parse(s"""{"id": "$id"}"""))
      } else {
        Results.badRequest(
          Json.parse(s"""{"message": "Es gab einen internen Fehler beim Loeschen der Aufgabe mit der ID $id}""")
        )
      }
  }


  def editExercise(id: Int): Result = {
    val exercise = exerciseReader.initFromForm(id, factory.form().bindFromRequest())
    exerciseReader.save(exercise)
    Results.ok(views.html.admin.preview.render(getUser, renderExercises(List(exercise).asJava, false)))
  }

  def editExerciseForm(id: Int): Result = finder.byId(id) match {
    case exercise if exercise == null => Results.badRequest("")
    case exercise => Results.ok(renderExEditForm(getUser, exercise, false))
  }

  def exercises: Result =
    Results.ok(views.html.admin.exerciseList.render(getUser, renderExercises(finder.all, true)))

  def exportExercises: Result =
    Results.ok(views.html.admin.export.render(getUser, Json.prettyPrint(Json.toJson(finder.all))))

  def importExercises: Result = processReadingResult(exerciseReader.readFromJsonFile(), renderExercises)

  def newExerciseForm: Result = {
    val id = ExerciseReader.findMinimalNotUsedId(finder)

    val exercise = exerciseReader.getOrInstantiateExercise(id)
    exerciseReader.save(exercise)

    Results.ok(renderExEditForm(getUser, exercise, true))
  }

  def uploadFile: Result = uploadFile(renderExercises)

  def renderExEditForm(user: User, exercise: E, isCreation: Boolean): Html

  def renderExercises(exercises: java.util.List[E], changesAllowed: Boolean): Html =
    views.html.admin.exercisesTable.render(exercises, toolObject, changesAllowed)

}
