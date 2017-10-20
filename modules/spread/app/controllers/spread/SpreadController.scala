package controllers.spread

import java.nio.file.{Files, Path, Paths, StandardCopyOption}
import javax.inject.Inject

import scala.concurrent.ExecutionContext.Implicits.global

import com.google.common.io.{Files => GFiles}
import controllers.excontrollers.{AExerciseAdminController, IdPartExController}
import controllers.spread.SpreadController._
import model._
import model.result.{CompleteResult, EvaluationResult}
import model.user.User
import play.api.data.Form
import play.api.mvc.ControllerComponents
import play.twirl.api.Html

import scala.concurrent.ExecutionContext
import scala.util.Try

class SpreadAdmin @Inject()(cc: ControllerComponents)(implicit ec: ExecutionContext)
  extends AExerciseAdminController[SpreadExercise](cc, SpreadToolObject, SpreadExercise.finder, SpreadExerciseReader)

class SpreadController @Inject()(cc: ControllerComponents) extends IdPartExController[SpreadExercise, EvaluationResult](cc, SpreadExercise.finder, SpreadToolObject) {

  override type SolType = StringSolution

  override def solForm: Form[StringSolution] = ???

  def download(id: Int, fileExtension: String) = Action { implicit request =>
    Option(SpreadExercise.finder.byId(id)) match {
      case None => BadRequest("This exercise does not exist!")
      case Some(exercise) =>
        val fileToDownload = toolObject.getSolFileForExercise(getUser.name, exercise, exercise.templateFilename + CORRECTION_ADD_STRING, fileExtension)

        if (fileToDownload.toFile.exists) Ok.sendFile(fileToDownload.toFile)
        else Redirect(routes.SpreadController.index(0))
    }
  }


  override def exercise(id: Int, fileExtension: String) = Action { implicit request =>
    Option(SpreadExercise.finder.byId(id)) match {
      case None => BadRequest("This exercise does not exist!")
      case Some(exercise) =>
        val filePath = Paths.get(toolObject.sampleDir.toString, exercise.templateFilename + "." + fileExtension)

        println(filePath.toAbsolutePath)

        if (filePath.toFile.exists) Ok.sendFile(filePath.toFile)
        else BadRequest("This file does not exist!")
    }
  }

  def upload(id: Int) = Action { implicit request =>
    //    val data: MultipartFormData[File] = request.body.asMultipartFormData
    //    Option(data.getFile(BODY_SOL_FILE_NAME)) match {
    //      case None => InternalServerError(views.html.spreadcorrectionerror.render(getUser, "Datei konnte nicht hochgeladen werden!"))
    //      case Some(uploadedFile) =>
    //
    //        val user = getUser
    //        val exercise = SpreadExercise.finder.byId(id)
    //
    //        val pathToUploadedFile = uploadedFile.getFile.toPath
    //        val fileExtension = com.google.common.io.Files.getFileExtension(uploadedFile.getFilename)
    //
    //        val targetFilePath = toolObject.getSolFileForExercise(user.name, exercise, exercise.templateFilename, fileExtension)
    //
    //        // Save solution
    //        saveSolutionForUser(pathToUploadedFile, targetFilePath) match {
    //          case Failure(e) => InternalServerError(views.html.spreadcorrectionerror.render(user, "Die Datei konnte nicht gespeichert werden!"))
    //          case Success(_) =>
    //            // Get paths to sample document
    //            val sampleDocumentPath = Paths.get(toolObject.sampleDir.toString, exercise.sampleFilename + "." + fileExtension)
    //            if (sampleDocumentPath.toFile.exists) {
    //              try {
    //                val result: SpreadSheetCorrectionResult = correctSpread(sampleDocumentPath, targetFilePath, conditionalFormating = false, charts = false)
    //
    //                if (result.success)
    //                  Ok(views.html.excelcorrect.render(user, result, exercise.getId, fileExtension))
    //                else
    //                  InternalServerError(views.html.spreadcorrectionerror.render(user, result.notices.head))
    //              } catch {
    //                case e: CorrectionException => InternalServerError(views.html.spreadcorrectionerror.render(user, e.getMessage))
    //              }
    //            } else {
    //              InternalServerError(views.html.spreadcorrectionerror.render(user, "Die Musterdatei konnte nicht gefunden werden!"))
    //            }
    //        }
    //    }
    Ok("TODO!")

  }

  override def index(page: Int) = super.index(page)

  override protected def correctEx(sol: StringSolution, exercise: SpreadExercise, user: User): Try[CompleteResult[EvaluationResult]]
  = ??? // FIXME: implement???

  override protected def renderExercise(user: User, exercise: SpreadExercise): Html = views.html.spreadExercise.render(user, exercise)

  override protected def renderResult(correctionResult: CompleteResult[EvaluationResult]): Html = ??? // FIXME: implement...

}

object SpreadController {

  val BODY_SOL_FILE_NAME: String = "solFile"
  val CORRECTION_ADD_STRING: String = "_Korrektur"

  val correctors = Map("ods" -> ODFCorrector, "xlsx" -> XLSXCorrector, "xlsm" -> XLSXCorrector)

  def saveSolutionForUser(uploadedSolution: Path, targetFilePath: Path): Try[Path] =
    Try(Files.createDirectories(targetFilePath.getParent))
      .map(_ => Files.move(uploadedSolution, targetFilePath, StandardCopyOption.REPLACE_EXISTING))

  def correctSpread(musterPath: Path, testPath: Path, conditionalFormating: Boolean, charts: Boolean): SpreadSheetCorrectionResult = {
    val fileExtension: String = GFiles.getFileExtension(testPath.toString)

    correctors.get(fileExtension) match {
      case None => SpreadSheetCorrectionFailure(s"""The filetype "$fileExtension" is not supported. Could not start correction.""")
      case Some(corrector) => corrector.correct(musterPath, testPath, conditionalFormating, charts)
    }
  }

}