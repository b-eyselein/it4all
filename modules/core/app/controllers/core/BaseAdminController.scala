package controllers.core

import java.io.File
import java.nio.file.{Path, Paths}

import model.exercisereading._
import model.user.User
import model.{JsonReadable, StringConsts}
import play.api.Configuration
import play.data.FormFactory
import play.libs.Json
import play.mvc.Http.MultipartFormData
import play.mvc.{Controller, Result, Results}
import play.twirl.api.Html

import scala.util.{Failure, Success, Try}

abstract class BaseAdminController[E <: JsonReadable](c: Configuration, f: FormFactory, exerciseReader: JsonReader[E])
  extends BaseController(c, f) {

  val savingDir: Path = Paths.get(rootDir, StringConsts.ADMIN_FOLDER, exerciseReader.exerciseType)

  def adminIndex: Result = Results.ok(renderAdminIndex(getUser))

  def renderAdminIndex(user: User): Html

  def getJSONSchemaFile: Result = Results.ok(Json.prettyPrint(exerciseReader.jsonSchema))

  def processReadingResult(abstractResult: AbstractReadingResult, render: (java.util.List[E], Boolean) => Html): Result =
    abstractResult match {
      case result: ReadingResult[E] =>
        result.read.foreach(exerciseReader.save)
        val files: List[Try[Path]] = result.read.flatMap(exerciseReader.checkFiles)

        files.foreach(println(_))

        Results.ok(views.html.admin.preview.render(getUser, render(result.javaRead, false)))
      case error: ReadingError =>
        Results.badRequest(views.html.jsonReadingError.render(getUser, error))
      case failure: ReadingFailure => Results.badRequest("There has been an error...")
    }

  def uploadFile(render: (java.util.List[E], Boolean) => Html): Result = {
    val body: MultipartFormData[File] = Controller.request.body().asMultipartFormData()
    body.getFile(StringConsts.BODY_FILE_NAME) match {
      case n if n == null => Results.badRequest("Fehler!")
      case uploadedFile =>
        val pathToUploadedFile = uploadedFile.getFile.toPath

        val jsonFile = Paths.get(savingDir.toString, uploadedFile.getFilename)
        saveUploadedFile(savingDir, pathToUploadedFile, jsonFile) match {
          case Success(jsonTargetPath) => processReadingResult(exerciseReader.readFromJsonFile(jsonTargetPath), render(_, _))
          case Failure(error) => Results.badRequest("There has been an error uploading your file...")
        }
    }
  }

}