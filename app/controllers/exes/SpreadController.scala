package controllers.exes

import java.nio.file.{Files, Path, StandardCopyOption}
import javax.inject._

import com.google.common.io.{Files => GFiles}
import controllers.core.AIdPartExController
import model.User
import model.core._
import model.core.result.{CompleteResult, EvaluationResult}
import model.spread.{SpreadCompleteEx, _}
import net.jcazevedo.moultingyaml.YamlFormat
import play.api.data.Form
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{ControllerComponents, EssentialAction}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.Try

object SpreadController {

  val BODY_SOL_FILE_NAME   : String = "solFile"
  val CORRECTION_ADD_STRING: String = "_Korrektur"

  val correctors = Map("ods" -> ODFCorrector, "xlsx" -> XLSXCorrector, "xlsm" -> XLSXCorrector)

  def saveSolutionForUser(uploadedSolution: Path, targetFilePath: Path): Try[Path] =
    Try(Files.createDirectories(targetFilePath.getParent))
      .map(_ => Files.move(uploadedSolution, targetFilePath, StandardCopyOption.REPLACE_EXISTING))

  def correctSpread(musterPath: Path, testPath: Path, conditionalFormating: Boolean, charts: Boolean): SpreadSheetCorrectionResult = {
    val fileExtension: String = GFiles.getFileExtension(testPath.toString)

    correctors.get(fileExtension) match {
      case None            => SpreadSheetCorrectionFailure(s"""The filetype "$fileExtension" is not supported. Could not start correction.""")
      case Some(corrector) => corrector.correct(musterPath, testPath, conditionalFormating, charts)
    }
  }

}

@Singleton
class SpreadController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)(implicit ec: ExecutionContext)
  extends AIdPartExController[SpreadExercise, EvaluationResult](cc, dbcp, r, SpreadToolObject) with Secured {

  override type SolutionType = StringSolution

  override def solForm: Form[StringSolution] = Solution.stringSolForm

  // Yaml

  override type CompEx = SpreadCompleteEx

  override implicit val yamlFormat: YamlFormat[SpreadCompleteEx] = null

  // db

  import profile.api._

  override type TQ = repo.SpreadExerciseTable

  override def tq = repo.spreadExercises

  override def completeExes: Future[Seq[SpreadCompleteEx]] = db.run(repo.spreadExercises.result).map(_.map(ex => SpreadCompleteEx(ex)))

  override def completeExById(id: Int): Future[Option[SpreadCompleteEx]] = db.run(repo.spreadExercises.findBy(_.id).apply(id).result.headOption.map {
    case Some(ex) => Some(SpreadCompleteEx(ex))
    case None     => None
  })

  override def saveRead(read: Seq[SpreadCompleteEx]): Future[Seq[Int]] = Future.sequence(read.map(completeEx =>
    db.run(repo.spreadExercises insertOrUpdate completeEx.ex)))

  // Views

  override protected def renderExercise(user: User, exercise: SpreadCompleteEx, part: String): Future[Html] = Future(views.html.spread.spreadExercise.render(user, exercise.ex))

  override protected def renderResult(correctionResult: CompleteResult[EvaluationResult]): Html = ??? // FIXME: implement...

  // Correction

  override protected def correctEx(user: User, sol: StringSolution, exercise: SpreadCompleteEx, part: String): Try[CompleteResult[EvaluationResult]]
  = ??? // FIXME: implement???

  // Other routes

  def download(id: Int, fileExtension: String): EssentialAction = withUser { user =>
    implicit request =>
      //      finder.byId(id) match {
      //        case None           => BadRequest("This exercise does not exist!")
      //        case Some(exercise) =>
      //          val fileToDownload = toolObject.getSolFileForExercise(user.name, exercise, exercise.templateFilename + CORRECTION_ADD_STRING, fileExtension)
      //
      //          if (fileToDownload.toFile.exists) Ok.sendFile(fileToDownload.toFile)
      //          else Redirect(routes.SpreadController.index(0))
      //      }
      Ok("TODO")
  }


  //  override def exercise(id: Int, fileExtension: String): EssentialAction = withUser { user =>
  //    implicit request =>
  //      finder.byId(id) match {
  //        case None           => BadRequest("This exercise does not exist!")
  //        case Some(exercise) =>
  //          val filePath = Paths.get(toolObject.sampleDir.toString, exercise.templateFilename + "." + fileExtension)
  //
  //          println(filePath.toAbsolutePath)
  //
  //          if (filePath.toFile.exists) Ok.sendFile(filePath.toFile)
  //          else BadRequest("This file does not exist!")
  //      }
  //      Ok("TODO")
  //  }

  def upload(id: Int): EssentialAction = withUser { user =>
    implicit request =>
      //    val data: MultipartFormData[File] = request.body.asMultipartFormData
      //    Option(data.getFile(BODY_SOL_FILE_NAME)) match {
      //      case None => InternalServerError(views.html.spreadcorrectionerror.render(user, "Datei konnte nicht hochgeladen werden!"))
      //      case Some(uploadedFile) =>
      //
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

}