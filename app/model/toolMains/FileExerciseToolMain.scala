package model.toolMains

import java.nio.file.Path

import model.core.{FileUtils, NoSuchExerciseException, ReadAndSaveResult}
import model.{FileCompleteEx, User}
import play.api.libs.Files.TemporaryFile
import play.api.mvc.MultipartFormData.FilePart
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Try}

abstract class FileExerciseToolMain(urlPart: String)(implicit ec: ExecutionContext) extends ASingleExerciseToolMain(urlPart) with FileUtils {

  // Abstract types

  override type CompExType <: FileCompleteEx[ExType, PartType]

  // DB

  override def futureSaveRead(exercises: Seq[ReadType]): Future[Seq[(ReadType, Boolean)]] = Future.sequence(exercises map {
    ex =>
      // FIXME: check files...
      checkFiles(ex)
      tables.saveCompleteEx(ex) map (saveRes => (ex, saveRes))
  })

  protected def checkFiles(ex: ReadType): Seq[Try[Path]]

  // Other members

  val fileTypes: Map[String, String]

  def renderExercise(user: User, exercise: CompExType, fileEnding: String): Html

  def renderResult(user: User, correctionResult: R, exercise: CompExType, fileExtension: String): Html

  def correctAndRender(user: User, id: Int, file: FilePart[TemporaryFile], fileExtension: String): Future[Try[Html]] =
    futureCompleteExById(id) map {
      case None         => Failure(NoSuchExerciseException(id))
      case Some(compEx) =>
        val learnerFileTargetPath: Path = solutionDirForExercise(user.username, compEx.ex.id) / s"${compEx.templateFilename}.$fileExtension"

        val sampleFilename = s"${compEx.sampleFilename}.$fileExtension"
        val musterFileSourcePath = sampleDirForExercise(compEx.ex.id) / sampleFilename
        val musterFileTargetPath = solutionDirForExercise(user.username, compEx.ex.id) / sampleFilename

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

  // Views

  override def previewExercise(user: User, read: ReadAndSaveResult[CompExType]): Html =
    views.html.admin.fileExes.fileExercisePreview(user, read, this)

  override def adminExerciseList(admin: User, exes: Seq[CompExType]): Html =
    views.html.admin.fileExes.fileExerciseAdminListView(admin, exes, this)

}
