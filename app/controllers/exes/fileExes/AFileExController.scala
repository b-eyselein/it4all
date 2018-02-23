package controllers.exes.fileExes

import java.nio.file.Path

import controllers.Secured
import controllers.exes.BaseExerciseController
import model._
import model.core.CommonUtils.RicherTry
import model.core._
import model.core.tools.FileExToolObject
import model.spread.SpreadConsts
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{ControllerComponents, EssentialAction}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

abstract class AFileExController[Ex <: Exercise, CompEx <: FileCompleteEx[Ex], Tables <: ExerciseTableDefs[Ex, CompEx]]
(cc: ControllerComponents, dbcp: DatabaseConfigProvider, t: Tables, to: FileExToolObject)(implicit ec: ExecutionContext)
  extends BaseExerciseController[Ex, CompEx, Tables](cc, dbcp, t, to) with Secured with FileUtils {

  // Routes

  def exercise(id: Int, fileExtension: String): EssentialAction = futureWithUser { user =>
    implicit request =>
      futureCompleteExById(id) map {
        case Some(exercise) =>
          log(user, ExerciseStartEvent(request, id))
          Ok(renderExercise(user, exercise, fileExtension))
        case None           => Redirect(toolObject.indexCall)
      }
  }

  def uploadSolution(id: Int, fileExtension: String): EssentialAction = futureWithUser(parse.multipartFormData) { user =>
    implicit request =>
      request.body.file(SpreadConsts.FILE_NAME) match {
        case None       => Future(BadRequest("There has been an error uploading your file!"))
        case Some(file) =>
          futureCompleteExById(id) map {
            case None         => BadRequest("There is no such exercise!")
            case Some(compEx) =>
              val learnerFileTargetPath = toolObject.solutionDirForExercise(user.username, compEx.id) / s"${compEx.templateFilename}.$fileExtension"
              val learnerTry = move(file.ref.path, learnerFileTargetPath)

              val sampleFilename = s"${compEx.sampleFilename}.$fileExtension"
              val musterFileSourcePath = toolObject.sampleDirForExercise(compEx.id) / sampleFilename
              val musterFileTargetPath = toolObject.solutionDirForExercise(user.username, compEx.id) / sampleFilename
              val musterTry = copy(musterFileSourcePath, musterFileTargetPath)

              learnerTry zip musterTry match {
                case Failure(_)                                 => BadRequest("There has been an error saving your file!")
                case Success((learnerFilePath, musterFilePath)) => Ok(renderResult(user, correctEx(learnerFilePath, musterFilePath, fileExtension), compEx, fileExtension))
              }
          }
      }
  }

  def downloadTemplate(id: Int, fileExtension: String): EssentialAction = futureWithUser { _ =>
    implicit request =>
      futureCompleteExById(id) map {
        case Some(exercise) => Ok.sendFile(exercise.templateFilePath(fileExtension).toFile)
        case None           => Redirect(toolObject.indexCall)
      }
  }

  def downloadCorrected(id: Int, fileExtension: String): EssentialAction = futureWithUser { user =>
    implicit request =>
      futureCompleteExById(id) map {
        case Some(exercise) =>
          val correctedFilePath = toolObject.solutionDirForExercise(user.username, exercise.id) / (exercise.templateFilename + SpreadConsts.CORRECTION_ADD_STRING + "." + fileExtension)
          Ok.sendFile(correctedFilePath.toFile)
        case None           => BadRequest("There is no such exercise!")
      }
  }

  // View helpers

  protected def renderExercise(user: User, exercise: CompEx, part: String): Html

  protected def renderResult(user: User, correctionResult: R, exercise: CompEx, fileExtension: String): Html

  // Correction

  protected def correctEx(learnerFilePath: Path, sampleFilePath: Path, fileExtension: String): R

  // Creation of exercises

  protected def saveReadToDb(read: CompEx): Future[Int]

  protected def checkFiles(ex: CompEx): Seq[Try[Path]]

}
