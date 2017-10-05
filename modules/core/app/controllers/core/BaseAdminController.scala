package controllers.core

import java.io.File
import java.nio.file.Paths

import scala.util.{ Failure, Success }

import model.{ JsonReadable, StringConsts }
import model.exercisereading.{ AbstractReadingResult, JsonReader, ReadingError, ReadingFailure, ReadingResult }
import play.data.FormFactory
import play.libs.Json
import play.mvc.{ Result, Results }
import play.mvc.Controller
import play.mvc.Http.MultipartFormData
import play.twirl.api.Html
import model.user.User

abstract class BaseAdminController[E <: JsonReadable](f: FormFactory, exerciseReader: JsonReader[E])
  extends BaseController(f) {

  val savingDir = Paths.get(BaseController.BASE_DATA_PATH, StringConsts.ADMIN_FOLDER, exerciseReader.exerciseType)

  def adminIndex = Results.ok(renderAdminIndex(BaseController.getUser))

  def renderAdminIndex(user: User): Html

  def getJSONSchemaFile = Results.ok(Json.prettyPrint(exerciseReader.jsonSchema))

  def processReadingResult(abstractResult: AbstractReadingResult, render: (java.util.List[E], Boolean) ⇒ Html) =
    abstractResult match {
      case result: ReadingResult[E] ⇒
        result.read.foreach(exerciseReader.save(_))
        Results.ok(views.html.admin.preview.render(BaseController.getUser, render(result.javaRead, false)))
      case error: ReadingError ⇒
        Results.badRequest(views.html.jsonReadingError.render(BaseController.getUser, error))
      case failure: ReadingFailure ⇒ Results.badRequest("There has been an error...")
    }

  def uploadFile(render: (java.util.List[E], Boolean) ⇒ Html): Result = {
    val body: MultipartFormData[File] = Controller.request.body().asMultipartFormData()
    body.getFile(StringConsts.BODY_FILE_NAME) match {
      case n if n == null ⇒ Results.badRequest("Fehler!")
      case uploadedFile ⇒
        val pathToUploadedFile = uploadedFile.getFile.toPath

        val jsonFile = Paths.get(savingDir.toString, uploadedFile.getFilename)
        BaseController.saveUploadedFile(savingDir, pathToUploadedFile, jsonFile) match {
          case Success(jsonTargetPath) ⇒ processReadingResult(exerciseReader.readFromJsonFile(jsonTargetPath), render(_, _))
          case Failure(error)          ⇒ Results.badRequest("There has been an error uploading your file...")
        }
    }
  }

}