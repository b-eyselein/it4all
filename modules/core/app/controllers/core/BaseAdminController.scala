package controllers.core

import java.io.File
import java.nio.file.{Path, Paths}

import io.ebean.Finder
import model.exercisereading._
import model.tools.IdExToolObject
import model.{JsonReadable, StringConsts}
import play.data.FormFactory
import play.libs.Json
import play.mvc.Http.MultipartFormData
import play.mvc.{Controller, Result, Results}
import play.twirl.api.Html

import scala.util.{Failure, Success, Try}

abstract class BaseAdminController[E <: JsonReadable]
(f: FormFactory, val toolObject: IdExToolObject, val finder: Finder[Integer, E], exerciseReader: JsonReader[E])
  extends BaseController(f) {

  protected def statistics = new Html(s"<li>Es existieren insgesamt ${finder.query.findCount} Aufgaben</li>")

  protected val savingDir: Path = Paths.get(toolObject.rootDir, StringConsts.ADMIN_FOLDER, exerciseReader.exerciseType)

  def adminIndex: Result = Results.ok(views.html.admin.exerciseAdminMain.render(getUser, statistics, toolObject, new Html("")))

  def getJSONSchemaFile: Result = Results.ok(Json.prettyPrint(exerciseReader.jsonSchema))

  protected def processReadingResult(abstractResult: AbstractReadingResult, render: (List[E], Boolean) => Html): Result =
    abstractResult match {
      case error: ReadingError =>
        Results.badRequest(views.html.jsonReadingError.render(getUser, error))

      case failure: ReadingFailure => Results.badRequest("There has been an error...")

      case result: ReadingResult[E] =>
        result.read.foreach(exerciseReader.save)

        // TODO: display result of file creation...
        val files: List[Try[Path]] = result.read.flatMap(exerciseReader.checkFiles)

        Results.ok(views.html.admin.preview.render(getUser, render(result.read, false)))
    }

  protected def uploadFile(render: (List[E], Boolean) => Html): Result = {
    val data: MultipartFormData[File] = Controller.request.body.asMultipartFormData()
    Option(data.getFile(StringConsts.BODY_FILE_NAME)) match {
      case None => Results.badRequest("Fehler!")
      case Some(uploadedFile) =>
        val jsonFile = Paths.get(savingDir.toString, uploadedFile.getFilename)
        saveUploadedFile(savingDir, uploadedFile.getFile.toPath, jsonFile) match {
          case Success(jsonTargetPath) => processReadingResult(exerciseReader.readFromJsonFile(jsonTargetPath), render(_, _))
          case Failure(error) => Results.badRequest("There has been an error uploading your file...")
        }
    }
  }

}