package controllers.spread

import java.io.File
import java.nio.file.{Files, Path, Paths, StandardCopyOption}
import javax.inject.Inject

import controllers.core.IdExController
import model.result.{CompleteResult, EvaluationResult}
import model.user.User
import model.{CorrectionException, SpreadExercise, SpreadSheetCorrectionResult, SpreadSheetCorrector}
import play.data.{DynamicForm, FormFactory}
import play.mvc.{Controller, Result, Results}
import play.twirl.api.Html
import SpreadController._
import play.mvc.Http.MultipartFormData

import scala.util.{Failure, Success, Try}

class SpreadController @Inject()(f: FormFactory)
  extends IdExController[SpreadExercise, EvaluationResult](f, SpreadExercise.finder, SpreadToolObject) {

  def download(id: Int, extension: String): Result = Option(SpreadExercise.finder.byId(id)) match {
    case None => Results.badRequest("This exercise does not exist!")
    case Some(exercise) =>
      val fileToDownload = toolObject.getSolFileForExercise(getUser.name, exercise, exercise.templateFilename + CORRECTION_ADD_STRING, extension)

      if (fileToDownload.toFile.exists) Results.ok(fileToDownload.toFile)
      else Results.redirect(routes.SpreadController.index(0))
  }


  def downloadTemplate(id: Int, fileType: String): Result = Option(SpreadExercise.finder.byId(id)) match {
    case None => Results.badRequest("This exercise does not exist!")
    case Some(exercise) =>
      val filePath = Paths.get(toolObject.sampleDir.toString, exercise.templateFilename + "." + fileType)

      if (filePath.toFile.exists) Results.ok(filePath.toFile)
      else Results.badRequest("This file does not exist!")

  }

  def upload(id: Int): Result = {
    val data: MultipartFormData[File] = Controller.request.body.asMultipartFormData()
    Option(data.getFile(BODY_SOL_FILE_NAME)) match {
      case None => Results.internalServerError(views.html.spreadcorrectionerror.render(getUser, "Datei konnte nicht hochgeladen werden!"))
      case Some(uploadedFile) =>

        val user = getUser
        val exercise = SpreadExercise.finder.byId(id)

        val pathToUploadedFile = uploadedFile.getFile.toPath
        val fileExtension = com.google.common.io.Files.getFileExtension(uploadedFile.getFilename)

        val targetFilePath = toolObject.getSolFileForExercise(user.name, exercise, exercise.templateFilename, fileExtension)

        // Save solution
        saveSolutionForUser(pathToUploadedFile, targetFilePath) match {
          case Failure(e) => Results.internalServerError(views.html.spreadcorrectionerror.render(user, "Die Datei konnte nicht gespeichert werden!"))
          case Success(_) =>
            // Get paths to sample document
            val sampleDocumentPath = Paths.get(toolObject.sampleDir.toString, exercise.sampleFilename + "." + fileExtension)
            if (sampleDocumentPath.toFile.exists) {
              try {
                val result: SpreadSheetCorrectionResult = SpreadSheetCorrector.correct(sampleDocumentPath, targetFilePath, conditionalFormating = false, charts = false)

                if (result.success)
                  Results.ok(views.html.excelcorrect.render(user, result, exercise.getId, fileExtension))
                else
                  Results.internalServerError(views.html.spreadcorrectionerror.render(user, result.notices.head))
              } catch {
                case e: CorrectionException => Results.internalServerError(views.html.spreadcorrectionerror.render(user, e.getMessage))
              }
            } else {
              Results.internalServerError(views.html.spreadcorrectionerror.render(user, "Die Musterdatei konnte nicht gefunden werden!"))
            }
        }
    }
  }

  override protected def correctEx(form: DynamicForm, exercise: SpreadExercise, user: User): Try[CompleteResult[EvaluationResult]] = ??? // FIXME: implement???

  override protected def renderExercise(user: User, exercise: SpreadExercise): Html = views.html.spreadExercise.render(user, exercise)

  override protected def renderExesListRest: Html = ??? // FIXME: implement...

  override protected def renderResult(correctionResult: CompleteResult[EvaluationResult]): Html = ??? // FIXME: implement...

}

object SpreadController {

  val BODY_SOL_FILE_NAME: String = "solFile"
  val CORRECTION_ADD_STRING: String = "_Korrektur"

  def saveSolutionForUser(uploadedSolution: Path, targetFilePath: Path): Try[Path] =
    Try(Files.createDirectories(targetFilePath.getParent))
      .map(_ => Files.move(uploadedSolution, targetFilePath, StandardCopyOption.REPLACE_EXISTING))

}