package controllers.core

import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.Arrays
import java.util.List

import scala.collection.JavaConverters.asScalaBufferConverter

import com.fasterxml.jackson.databind.JsonNode

import io.ebean.Finder
import javax.inject.Inject
import model.AdminSecured
import model.StringConsts
import model.exercise.Exercise
import model.exercisereading.ExerciseReader
import model.exercisereading.ReadingError
import model.exercisereading.ReadingResult
import model.user.User
import play.Logger
import play.api.libs.json.JsValue
import play.api.mvc.AbstractController
import play.api.mvc.ControllerComponents
import play.api.mvc.Result
import play.api.mvc.Results
import play.libs.Json
import play.mvc.Controller
import play.mvc.Http.MultipartFormData
import play.mvc.Http.MultipartFormData.FilePart
import play.twirl.api.Html
import play.mvc.Security.Authenticated
import model.exercisereading.ReadingFailure

@Authenticated(classOf[AdminSecured])
abstract class AScalaExAdminController[E <: Exercise] @Inject() (cc: ControllerComponents, routes: RoutesObject, finder: Finder[Integer, E], exerciseReader: ExerciseReader[E])
    extends AbstractController(cc) {

  implicit def JavaJson2ScalaJson(jsonNode: com.fasterxml.jackson.databind.JsonNode): JsValue =
    play.api.libs.json.Json.toJson(play.libs.Json.prettyPrint(jsonNode))

  case class DeletionResult(id: Int, message: String)

  def saveUploadedFile(savingDir: Path, pathToUploadedFile: Path, saveTo: Path): Path = {
    if (!savingDir.toFile.exists && !ExerciseReader.createDirectory(savingDir))
      // error occured...
      return null

    try {
      return Files.move(pathToUploadedFile, saveTo, StandardCopyOption.REPLACE_EXISTING)
    } catch {
      case e: IOException =>
        Logger.error("Error while saving uploaded sql file!", e)
        null
    }
  }

  def deleteExercise(id: Int) = Action { request =>
    finder.byId(id) match {
      case null =>
        val ret = Json.toJson(DeletionResult(id, s"Die Aufgabe mit ID $id existiert nicht und kann daher nicht gelöscht werden!"));
        BadRequest(JavaJson2ScalaJson(ret))
      case ex =>
        if (ex.delete) {
          val ret = Json.toJson(DeletionResult(id, s"Die Aufgabe mit der ID $id konnte erfolgreich gelöscht werden."))
          Ok(JavaJson2ScalaJson(ret))
        } else {
          val ret = Json.toJson(DeletionResult(id, s"Es gab einen internen Fehler beim Löschen der Aufgabe mit der ID $id"))
          BadRequest(JavaJson2ScalaJson(ret))
        }
    }
  }

  def editExercise(id: Int) = Action { request =>
    val exercise = exerciseReader.initFromForm(id, null /* request.body.asFormUrlEncoded*/ )
    exerciseReader.saveExercise(exercise)
    Ok(views.html.admin.preview.render(BaseController.getUser, renderExercises(Arrays.asList(exercise), false)))
  }

  def editExerciseForm(id: Int) = finder.byId(id) match {
    case null => BadRequest(s"There has been an error while searching for exercise $id of type ...")
    case ex => Ok(renderExEditForm(BaseController.getUser, ex, false))
  }

  def exercises = Action { Ok(views.html.admin.exerciseList.render(BaseController.getUser, renderExercises(finder.all, true))) }

  def exportExercises = Action { Ok(views.html.admin.export.render(BaseController.getUser, Json.prettyPrint(Json.toJson(finder.all)))) }
  def getJSONSchemaFile = Action { Ok(Json.prettyPrint(exerciseReader.getJsonSchema)) }

  def importExercises = Action { request =>
    exerciseReader.readFromStandardFile match {
      case err: ReadingError => BadRequest(views.html.jsonReadingError.render(BaseController.getUser, err))
      case fail: ReadingFailure => BadRequest(views.html.jsonReadingError.render(BaseController.getUser, null))
      case res: ReadingResult[E] =>
        res.read.asScala.foreach(exerciseReader.saveExercise(_))
        Ok(views.html.admin.preview.render(BaseController.getUser, renderExercises(res.read, false)))
    }
  }

  def adminIndex = Action { Ok(renderAdminIndex(BaseController.getUser)) }

  def newExerciseForm = Action {
    val id = ExerciseReader.findMinimalNotUsedId(finder)

    val exercise = exerciseReader.getOrInstantiateExercise(id)
    exerciseReader.saveExercise(exercise)

    Ok(renderExEditForm(BaseController.getUser, exercise, true))
  }

  def uploadFile = Action {
    val body: MultipartFormData[File] = Controller.request().body().asMultipartFormData()
    val uploadedFile: FilePart[File] = body.getFile(StringConsts.BODY_FILE_NAME)

    if (uploadedFile == null)
      BadRequest("Fehler!")

    val pathToUploadedFile = uploadedFile.getFile.toPath
    val savingDir = Paths.get(BaseController.BASE_DATA_PATH, StringConsts.ADMIN_FOLDER, exerciseReader.getExerciseType)

    val jsonFile = Paths.get(savingDir.toString, uploadedFile.getFilename)
    val jsonTargetPath = saveUploadedFile(savingDir, pathToUploadedFile, jsonFile)

    exerciseReader.readAllFromFile(jsonTargetPath) match {
      case err: ReadingError => BadRequest(views.html.jsonReadingError.render(BaseController.getUser, err))
      case fail: ReadingFailure => BadRequest("TODO!")
      case res: ReadingResult[E] =>
        res.read.asScala.foreach(exerciseReader.saveExercise(_))
        Ok(views.html.admin.preview.render(BaseController.getUser, renderExercises(res.read, false)))
    }
  }

  def getSampleDir =
    Paths.get(BaseController.BASE_DATA_PATH, BaseController.SAMPLE_SUB_DIRECTORY, exerciseReader.getExerciseType)

  def renderAdminIndex(user: User): Html

  def renderExEditForm(user: User, exercise: E, isCreation: Boolean): Html

  def renderExercises(exercises: List[E], changesAllowed: Boolean) =
    views.html.admin.exercisesTable.render(exercises, routes, changesAllowed)

}
