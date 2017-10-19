package controllers.spread

import java.io.File
import java.nio.file.{Files, Path, Paths, StandardCopyOption}
import javax.inject.Inject

import com.google.common.io.{Files => GFiles}
import controllers.core.{AExerciseAdminController, IdPartExController}
import controllers.spread.SpreadController._
import model._
import model.result.{CompleteResult, EvaluationResult}
import model.user.User
import play.data.{DynamicForm, FormFactory}
import play.mvc.Http.MultipartFormData
import play.mvc.Results._
import play.mvc.{Controller, Result}
import play.twirl.api.Html

import scala.util.{Failure, Success, Try}

class SpreadAdmin @Inject()(f: FormFactory) extends AExerciseAdminController[SpreadExercise](f, SpreadToolObject, SpreadExercise.finder, SpreadExerciseReader)

class SpreadController @Inject()(f: FormFactory) extends IdPartExController[SpreadExercise, EvaluationResult](f, SpreadExercise.finder, SpreadToolObject) {

  def download(id: Int, fileExtension: String): Result = Option(SpreadExercise.finder.byId(id)) match {
    case None => badRequest("This exercise does not exist!")
    case Some(exercise) =>
      val fileToDownload = toolObject.getSolFileForExercise(getUser.name, exercise, exercise.templateFilename + CORRECTION_ADD_STRING, fileExtension)

      if (fileToDownload.toFile.exists) ok(fileToDownload.toFile)
      else redirect(routes.SpreadController.index(0))
  }


  override def exercise(id: Int, fileExtension: String): Result = Option(SpreadExercise.finder.byId(id)) match {
    case None => badRequest("This exercise does not exist!")
    case Some(exercise) =>
      val filePath = Paths.get(toolObject.sampleDir.toString, exercise.templateFilename + "." + fileExtension)

      println(filePath.toAbsolutePath)

      if (filePath.toFile.exists) ok(filePath.toFile)
      else badRequest("This file does not exist!")
  }

  def upload(id: Int): Result = {
    val data: MultipartFormData[File] = Controller.request.body.asMultipartFormData()
    Option(data.getFile(BODY_SOL_FILE_NAME)) match {
      case None => internalServerError(views.html.spreadcorrectionerror.render(getUser, "Datei konnte nicht hochgeladen werden!"))
      case Some(uploadedFile) =>

        val user = getUser
        val exercise = SpreadExercise.finder.byId(id)

        val pathToUploadedFile = uploadedFile.getFile.toPath
        val fileExtension = com.google.common.io.Files.getFileExtension(uploadedFile.getFilename)

        val targetFilePath = toolObject.getSolFileForExercise(user.name, exercise, exercise.templateFilename, fileExtension)

        // Save solution
        saveSolutionForUser(pathToUploadedFile, targetFilePath) match {
          case Failure(e) => internalServerError(views.html.spreadcorrectionerror.render(user, "Die Datei konnte nicht gespeichert werden!"))
          case Success(_) =>
            // Get paths to sample document
            val sampleDocumentPath = Paths.get(toolObject.sampleDir.toString, exercise.sampleFilename + "." + fileExtension)
            if (sampleDocumentPath.toFile.exists) {
              try {
                val result: SpreadSheetCorrectionResult = correctSpread(sampleDocumentPath, targetFilePath, conditionalFormating = false, charts = false)

                if (result.success)
                  ok(views.html.excelcorrect.render(user, result, exercise.getId, fileExtension))
                else
                  internalServerError(views.html.spreadcorrectionerror.render(user, result.notices.head))
              } catch {
                case e: CorrectionException => internalServerError(views.html.spreadcorrectionerror.render(user, e.getMessage))
              }
            } else {
              internalServerError(views.html.spreadcorrectionerror.render(user, "Die Musterdatei konnte nicht gefunden werden!"))
            }
        }
    }
  }

  override def index(page: Int): Result = super.index(page)

  override protected def correctEx(form: DynamicForm, exercise: SpreadExercise, user: User): Try[CompleteResult[EvaluationResult]] = ??? // FIXME: implement???

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