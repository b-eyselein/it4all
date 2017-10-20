package controllers.core

import java.nio.file.{Path, Paths}

import io.ebean.Finder
import model.exercisereading._
import model.tools.ExToolObject
import model.{JsonReadable, StringConsts}
import play.api.mvc.ControllerComponents
import play.mvc.Security.Authenticated
import play.twirl.api.Html

@Authenticated(classOf[model.AdminSecured])
abstract class BaseAdminController[E <: JsonReadable]
(cc: ControllerComponents, val toolObject: ExToolObject, val finder: Finder[Integer, E], exerciseReader: JsonReader[E])
  extends BaseController(cc) {

  protected def statistics = new Html(s"<li>Es existieren insgesamt ${finder.query.findCount} Aufgaben</li>")

  protected val savingDir: Path = Paths.get(toolObject.rootDir, StringConsts.ADMIN_FOLDER, exerciseReader.exerciseType)

  def adminIndex = Action { implicit request =>
    Ok(views.html.admin.exerciseAdminMain.render(getUser, statistics, toolObject, new Html("")))
  }

  def getJSONSchemaFile = Action { implicit request => Ok("" /*Json.prettyPrint(exerciseReader.jsonSchema)*/)
  }

  //  protected def processReadingResult(abstractResult: AbstractReadingResult, render: List[SingleReadingResult[E]] => Html): Result =
  //    abstractResult match {
  //      case error: ReadingError =>
  //        BadRequest(views.html.jsonReadingError.render(getUser, error))
  //
  //      case _: ReadingFailure => BadRequest("There has been an error...")
  //
  //      case result: ReadingResult[E] =>
  //        result.read.foreach(read => {
  //          exerciseReader.save(read.read)
  //          read.fileResults = exerciseReader.checkFiles(read.read)
  //        })
  //        Ok(views.html.admin.preview.render(getUser, toolObject, result.read))
  //    }

  def uploadFile(render: List[SingleReadingResult[E]] => Html) = Action { implicit request =>
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
    Ok("TODO!")
  }

}