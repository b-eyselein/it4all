package controllers.exes.fileExes

import java.nio.file.Path

import controllers.exes.{AToolMain, BaseExerciseController}
import model.core.{EvaluationResult, FileUtils, NoSuchExerciseException}
import model.{Exercise, FileCompleteEx, User}
import play.api.libs.Files.TemporaryFile
import play.api.mvc.MultipartFormData.FilePart
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Try}

trait AFileExToolMain[ExType <: Exercise, CompExType <: FileCompleteEx[ExType]] extends AToolMain[ExType, CompExType] with FileUtils {

  val fileTypes: Map[String, String]

  type R <: EvaluationResult

  BaseExerciseController.FileToolMains += (urlPart -> this)

  def renderExercise(user: User, exercise: CompExType, fileEnding: String): Html

  def renderExerciseById(user: User, id: Int, fileEnding: String)(implicit ec: ExecutionContext): Future[Option[Html]] = futureCompleteExById(id) map {
    case Some(exercise) => Some(renderExercise(user, exercise, fileEnding))
    case None           => None
  }

  def renderResult(user: User, correctionResult: R, exercise: CompExType, fileExtension: String): Html

  def correctAndRender(user: User, id: Int, file: FilePart[TemporaryFile], fileExtension: String)(implicit ec: ExecutionContext): Future[Try[Html]] =
    futureCompleteExById(id) map {
      case None         => Failure(NoSuchExerciseException(id))
      case Some(compEx) =>
        val learnerFileTargetPath: Path = solutionDirForExercise(user.username, compEx.id) / s"${compEx.templateFilename}.$fileExtension"

        val sampleFilename = s"${compEx.sampleFilename}.$fileExtension"
        val musterFileSourcePath = sampleDirForExercise(compEx.id) / sampleFilename
        val musterFileTargetPath = solutionDirForExercise(user.username, compEx.id) / sampleFilename

        val fileCopyTries = for {
          learnerTry <- move(file.ref.path, learnerFileTargetPath)
          musterTry <- copy(musterFileSourcePath, musterFileTargetPath)
        } yield (learnerTry, musterTry)

        fileCopyTries map {
          case (learnerFilePath, musterFilePath) =>
            val correction = correctEx(learnerFilePath, musterFilePath, fileExtension)
            renderResult(user, correction, compEx, fileExtension)
        }
    }


  protected def correctEx(learnerFilePath: Path, sampleFilePath: Path, fileExtension: String): R

}
