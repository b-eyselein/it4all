package controllers.core

import java.io.File
import java.nio.file.{Path, Paths}

import io.ebean.Finder
import model.exercisereading._
import model.tools.ExToolObject
import model.{JsonReadable, StringConsts}
import play.data.FormFactory
import play.libs.Json
import play.mvc.Http.MultipartFormData
import play.mvc.Security.Authenticated
import play.mvc.{Controller, Result}
import play.twirl.api.Html
import play.mvc.Results._

import scala.util.{Failure, Success}

@Authenticated(classOf[model.AdminSecured])
abstract class BaseAdminController[E <: JsonReadable]
(f: FormFactory, val toolObject: ExToolObject, val finder: Finder[Integer, E], exerciseReader: JsonReader[E])
  extends BaseController(f) {

  protected def statistics = new Html(s"<li>Es existieren insgesamt ${finder.query.findCount} Aufgaben</li>")

  protected val savingDir: Path = Paths.get(toolObject.rootDir, StringConsts.ADMIN_FOLDER, exerciseReader.exerciseType)

  def adminIndex: Result = ok(views.html.admin.exerciseAdminMain.render(getUser, statistics, toolObject, new Html("")))

  def getJSONSchemaFile: Result = ok(Json.prettyPrint(exerciseReader.jsonSchema))

  //  protected def processReadingResult(abstractResult: AbstractReadingResult, render: List[SingleReadingResult[E]] => Html): Result =
  //    abstractResult match {
  //      case error: ReadingError =>
  //        Results.badRequest(views.html.jsonReadingError.render(getUser, error))
  //
  //      case _: ReadingFailure => Results.badRequest("There has been an error...")
  //
  //      case result: ReadingResult[E] =>
  //        result.read.foreach(read => {
  //          exerciseReader.save(read.read)
  //          read.fileResults = exerciseReader.checkFiles(read.read)
  //        })
  //        Results.ok(views.html.admin.preview.render(getUser, toolObject, result.read))
  //    }

  def uploadFile(render: List[SingleReadingResult[E]] => Html): Result = {
    //    val data: MultipartFormData[File] = Controller.request.body.asMultipartFormData()
    //    Option(data.getFile(StringConsts.BODY_FILE_NAME)) match {
    //      case None => Results.badRequest("Fehler!")
    //      case Some(uploadedFile) =>
    //        val jsonFile = Paths.get(savingDir.toString, uploadedFile.getFilename)
    //        saveUploadedFile(savingDir, uploadedFile.getFile.toPath, jsonFile) match {
    //          case Success(jsonTargetPath) => processReadingResult(exerciseReader.readFromJsonFile(jsonTargetPath), render)
    //          case Failure(error) => Results.badRequest("There has been an error uploading your file...")
    //        }
    //    }
    ok("TODO!")
  }

}