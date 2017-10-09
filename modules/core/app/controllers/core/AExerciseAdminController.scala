package controllers.core

import java.io.File
import java.nio.file.Paths

import scala.collection.JavaConverters.seqAsJavaListConverter
import scala.util.{ Failure, Success }

import io.ebean.Finder
import model.StringConsts
import model.exercise.{ Exercise, ExerciseState }
import model.exercisereading.ExerciseReader
import model.tools.IdExToolObject
import model.user.User
import play.data.FormFactory
import play.libs.Json
import play.mvc.{ Result, Results }
import play.mvc.Controller
import play.mvc.Http.MultipartFormData
import play.mvc.Security.Authenticated
import play.twirl.api.Html

@Authenticated(classOf[model.AdminSecured])
abstract class AExerciseAdminController[E <: Exercise](
  f: FormFactory, val toolObject: IdExToolObject, val finder: Finder[Integer, E], val exerciseReader: ExerciseReader[E]
)
  extends BaseAdminController[E](f, exerciseReader) {

  def changeExState(id: Int) = {
    val exercise = finder.byId(id)
    val newState = ExerciseState.valueOf(factory.form().bindFromRequest().get("state"))

    exercise.state = newState
    exercise.save

    Results.ok(Json.parse(s"""{"id": "$id", "newState": "${exercise.state}"}"""))
  }

  def deleteExercise(id: Int) = {
    val toDelete = finder.byId(id)
    if (toDelete == null) {
      Results.badRequest(Json.parse(
        s"""{"message": "Die Aufgabe mit ID $id existiert nicht und kann daher nicht geloescht werden!"""
      ))
    } else {
      if (toDelete.delete()) {
        Results.ok(Json.parse(s"""{"id": "$id"}"""))
      } else {
        Results.badRequest(
          Json.parse(s"""{"message": "Es gab einen internen Fehler beim Loeschen der Aufgabe mit der ID $id}""")
        )
      }
    }
  }

  def editExercise(id: Int) = {
    val exercise = exerciseReader.initFromForm(id, factory.form().bindFromRequest())
    exerciseReader.save(exercise)
    Results.ok(views.html.admin.preview.render(BaseController.getUser, renderExercises(List(exercise).asJava, false)))
  }

  def editExerciseForm(id: Int) = finder.byId(id) match {
    case exercise if exercise == null => Results.badRequest("")
    case exercise                     => Results.ok(renderExEditForm(BaseController.getUser, exercise, false))
  }

  def exercises =
    Results.ok(views.html.admin.exerciseList.render(BaseController.getUser, renderExercises(finder.all, true)))

  def exportExercises =
    Results.ok(views.html.admin.export.render(BaseController.getUser, Json.prettyPrint(Json.toJson(finder.all))))

  def importExercises = processReadingResult(exerciseReader.readFromJsonFile(), renderExercises(_, _))

  def newExerciseForm = {
    val id = ExerciseReader.findMinimalNotUsedId(finder)

    val exercise = exerciseReader.getOrInstantiateExercise(id)
    exerciseReader.save(exercise)

    Results.ok(renderExEditForm(BaseController.getUser, exercise, true))
  }

  def uploadFile: Result = uploadFile(renderExercises(_, _))

  def getSampleDir = Paths.get(BaseController.BASE_DATA_PATH, BaseController.SAMPLE_SUB_DIRECTORY, exerciseReader.exerciseType)

  def renderExEditForm(user: User, exercise: E, isCreation: Boolean): Html

  def renderExercises(exercises: java.util.List[E], changesAllowed: Boolean) =
    views.html.admin.exercisesTable.render(exercises, toolObject, changesAllowed)

}
