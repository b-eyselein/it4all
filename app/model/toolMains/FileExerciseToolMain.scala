package model.toolMains

import better.files.File
import model.core.CoreConsts._
import model.core.{NoSuchExerciseException, ReadAndSaveResult}
import model.{FileCompleteEx, User}
import play.api.libs.Files.TemporaryFile
import play.api.mvc.Call
import play.api.mvc.MultipartFormData.FilePart
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

abstract class FileExerciseToolMain(tn: String, up: String)(implicit ec: ExecutionContext)
  extends ASingleExerciseToolMain(tn, up) {

  // Abstract types

  override type CompExType <: FileCompleteEx[ExType, PartType]

  // DB

  override def futureSaveRead(exercises: Seq[ReadType]): Future[Seq[(ReadType, Boolean)]] = Future.sequence(exercises map {
    ex =>
      // FIXME: check files...
      checkFiles(ex)
      tables.futureSaveCompleteEx(ex) map (saveRes => (ex, saveRes))
  })

  protected def checkFiles(ex: ReadType): Seq[File]

  // Other members

  val fileTypes: Map[String, String]

  // Paths

  def sampleDirForExercise(id: Int): File = exerciseRootDir / sampleSubDir / String.valueOf(id)

  def templateDirForExercise(id: Int): File = exerciseRootDir / templateSubDir / String.valueOf(id)

  // Views

  override def exercisesOverviewForIndex: Html = Html(
    s"""<div class="form-group">
       |  <a class="btn btn-primary btn-block" href="${controllers.routes.FileExerciseController.exerciseList(up)}">Zu den Übungsaufgaben</a>
       |</div>""".stripMargin)

  override def adminIndexView(admin: User, toolList: ToolList): Future[Html] = statistics map {
    stats => views.html.admin.fileExes.fileExerciseAdminMain(admin, stats, this, toolList)
  }

  override def previewReadAndSaveResult(user: User, read: ReadAndSaveResult[CompExType], toolList: ToolList): Html =
    views.html.admin.fileExes.fileExercisePreview(user, read, this, toolList)

  override def adminExerciseList(admin: User, exes: Seq[CompExType], toolList: ToolList): Html =
    views.html.admin.fileExes.fileExerciseAdminListView(admin, exes, this, toolList)

  def renderExercise(user: User, exercise: CompExType, fileEnding: String): Html

  def renderResult(user: User, correctionResult: R, exercise: CompExType, fileExtension: String): Html

  def correctAndRender(user: User, id: Int, file: FilePart[TemporaryFile], fileExtension: String): Future[Try[Html]] =
    futureCompleteExById(id) map {
      case None         => Failure(NoSuchExerciseException(id))
      case Some(compEx) =>
        val learnerFileTargetPath: File = solutionDirForExercise(user.username, compEx.ex.id) / s"${compEx.templateFilename}.$fileExtension"

        val sampleFilename: String = s"${compEx.sampleFilename}.$fileExtension"
        val musterFileSourcePath: File = sampleDirForExercise(compEx.ex.id) / sampleFilename
        val musterFileTargetPath: File = solutionDirForExercise(user.username, compEx.ex.id) / sampleFilename

        File(file.ref.path) moveTo learnerFileTargetPath
        musterFileSourcePath copyTo musterFileTargetPath

        val correction = correctEx(learnerFileTargetPath, musterFileTargetPath, fileExtension)
        Success(renderResult(user, correction, compEx, fileExtension))
    }

  // Correction

  protected def correctEx(learnerFilePath: File, sampleFilePath: File, fileExtension: String): R

  // Calls

  override def indexCall: Call = controllers.routes.MainExerciseController.index(this.up)

  override def exerciseRoutesForUser(user: User, exercise: CompExType): Future[Seq[CallForExPart]] = Future {
    exParts flatMap {
      exPart: PartType =>
        if (exercise.hasPart(exPart)) Some(CallForExPart(exPart, controllers.routes.FileExerciseController.exercise(up, exercise.ex.id, exPart.urlName), enabled = true))
        else None
    }
  }


}
