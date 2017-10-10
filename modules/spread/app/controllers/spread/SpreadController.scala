package controllers.spread

import controllers.core.IdExController
import model.CorrectionException
import model.SpreadExercise
import model.SpreadSheetCorrectionResult
import model.SpreadSheetCorrector
import model.result.CompleteResult
import model.result.EvaluationResult
import model.user.User
import play.Logger
import play.api.Configuration
import play.data.DynamicForm
import play.data.FormFactory
import play.mvc.Http.MultipartFormData
import play.mvc.Http.MultipartFormData.FilePart
import play.mvc.{Controller, Result, Results}
import play.twirl.api.Html
import javax.inject.Inject
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

import scala.util.Try

class SpreadController @Inject()(c: Configuration, f: FormFactory)
  extends IdExController[SpreadExercise, EvaluationResult](c, f, SpreadExercise.finder, new SpreadToolObject(c)) {

  val BODY_SOL_FILE_NAME: String = "solFile"
  val CORRECTION_ADD_STRING: String = "_Korrektur"


  override def correctEx(form: DynamicForm, exercise: SpreadExercise,
                         user: User): Try[CompleteResult[EvaluationResult]] = ???

  def download(id: Int, extension: String): Result = Option(SpreadExercise.finder.byId(id)) match {
    case None => Results.badRequest("This exercise does not exist!")
    case Some(exercise) =>
      val fileToDownload = toolObject.getSolFileForExercise(getUser.name, exercise, exercise.templateFilename + CORRECTION_ADD_STRING, extension)

      if (fileToDownload.toFile.exists)
        Results.ok(fileToDownload.toFile)
      else
        Results.redirect(routes.SpreadController.index(0))
  }


  def downloadTemplate(id: Int, fileType: String): Result = Option(SpreadExercise.finder.byId(id)) match {
    case None => Results.badRequest("This exercise does not exist!")
    case Some(exercise) =>
      val filePath = Paths.get(toolObject.sampleDir.toString, exercise.templateFilename + "." + fileType)

      if (filePath.toFile.exists)
        Results.ok(filePath.toFile)
      else
        Results.badRequest("This file does not exist!")

  }

  override def renderExercise(user: User, exercise: SpreadExercise): Html = views.html.spreadExercise.render(user, exercise)

  override def renderExesListRest: Html = ??? // FIXME: implement...

  override def renderResult(correctionResult: CompleteResult[EvaluationResult]): Html = ??? // FIXME: implement...

  def upload(id: Int): Result = {
    val user = getUser
    val exercise = SpreadExercise.finder.byId(id)

    // Extract solution from request
    val body: MultipartFormData[File] = Controller.request.body().asMultipartFormData()
    val uploadedFile: FilePart[File] = body.getFile(BODY_SOL_FILE_NAME)
    if (uploadedFile == null)
      return Results.internalServerError(
        views.html.spreadcorrectionerror.render(user, "Datei konnte nicht hochgeladen werden!"))
    val pathToUploadedFile = uploadedFile.getFile.toPath
    val fileExtension = com.google.common.io.Files.getFileExtension(uploadedFile.getFilename)

    // Save solution
    val targetFilePath = toolObject.getSolFileForExercise(user.name, exercise, exercise.templateFilename,
      fileExtension)
    val fileSuccessfullySaved = saveSolutionForUser(pathToUploadedFile, targetFilePath)
    if (!fileSuccessfullySaved)
      return Results.internalServerError(
        views.html.spreadcorrectionerror.render(user, "Die Datei konnte nicht gespeichert werden!"))

    // Get paths to sample document
    val sampleDocumentPath = Paths
      .get(toolObject.sampleDir.toString, exercise.sampleFilename + "." + fileExtension)
    if (!sampleDocumentPath.toFile.exists())
      return Results.internalServerError(
        views.html.spreadcorrectionerror.render(user, "Die Musterdatei konnte nicht gefunden werden!"))
    try {
      val result: SpreadSheetCorrectionResult = SpreadSheetCorrector.correct(sampleDocumentPath, targetFilePath, false, false)

      if (result.isSuccess)
        Results.ok(views.html.excelcorrect.render(user, result, exercise.getId, fileExtension))
      else
        Results.internalServerError(views.html.spreadcorrectionerror.render(user, result.getNotices.get(0)))
    } catch {
      case e: CorrectionException => Results.internalServerError(views.html.spreadcorrectionerror.render(user, e.getMessage))
    }
  }

  def saveSolutionForUser(uploadedSolution: Path, targetFilePath: Path): Boolean = {
    try {
      val solDirForExercise = targetFilePath.getParent
      if (!solDirForExercise.toFile.exists && !solDirForExercise.toFile.isDirectory)
        Files.createDirectories(solDirForExercise)

      Files.move(uploadedSolution, targetFilePath, StandardCopyOption.REPLACE_EXISTING)
      return true
    } catch {
      case e: Exception =>
        Logger.error("Fehler beim Speichern der Loesung!", e)
        return false
    }
  }

}
