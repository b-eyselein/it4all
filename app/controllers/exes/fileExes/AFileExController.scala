package controllers.exes.fileExes

import java.nio.file.Path
import java.sql.SQLSyntaxErrorException

import controllers.Secured
import controllers.exes.{BaseExerciseController, IntExIdentifier}
import model._
import model.core.CommonUtils.RicherTry
import model.core._
import model.core.tools.ExToolObject
import model.spread.SpreadConsts
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{Call, ControllerComponents, EssentialAction, Result}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

trait FileExToolObject extends ExToolObject {

  override type CompEx <: FileCompleteEx[_ <: Exercise]

  val fileTypes: Map[String, String]

  def exerciseRoute(exercise: HasBaseValues, fileExtension: String): Call

  override def exerciseRoutes(exercise: CompEx): Map[Call, String] =
    fileTypes filter (ft => exercise.available(ft._1)) map (ft => (exerciseRoute(exercise.ex, ft._1), s"Mit ${ft._2} bearbeiten"))

  def uploadSolutionRoute(exercise: HasBaseValues, fileExtension: String): Call

  def downloadCorrectedRoute(exercise: HasBaseValues, fileExtension: String): Call

}

abstract class AFileExController[E <: Exercise, R <: EvaluationResult, CompResult <: CompleteResult[R]]
(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository, to: FileExToolObject)(implicit ec: ExecutionContext)
  extends BaseExerciseController[E, R, CompResult](cc, dbcp, r, to) with Secured with FileUtils {

  override type ExIdentifier = IntExIdentifier

  override type CompEx <: FileCompleteEx[E]

  override def saveAndPreviewExercises(admin: User, read: Seq[CompEx]): Future[Result] =
    saveRead(read) map (_ => Ok(previewExercises(admin, read))) recover {
      // FIXME: Failures!
      case sqlError: SQLSyntaxErrorException =>
        sqlError.printStackTrace()
        BadRequest(sqlError.getMessage)
      case throwable                         =>
        throwable.printStackTrace()
        BadRequest(throwable.getMessage)
    }

  override protected def saveRead(read: Seq[CompEx]): Future[Seq[Int]] = Future.sequence(read map { ex =>
    // FIXME: use pathTries ==> display with exercises?
    val pathTries = checkFiles(ex)
    saveReadToDb(ex)
  })

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
              val learnerFileTargetPath = toolObject.solutionDirForExercise(user.username, compEx.ex) / s"${compEx.templateFilename}.$fileExtension"
              val learnerTry = move(file.ref.path, learnerFileTargetPath)

              val sampleFilename = s"${compEx.sampleFilename}.$fileExtension"
              val musterFileSourcePath = toolObject.sampleDirForExercise(compEx.ex) / sampleFilename
              val musterFileTargetPath = toolObject.solutionDirForExercise(user.username, compEx.ex) / sampleFilename
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
          val correctedFilePath = toolObject.solutionDirForExercise(user.username, exercise.ex) / (exercise.templateFilename + SpreadConsts.CORRECTION_ADD_STRING + "." + fileExtension)
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

  protected def checkFiles(ex: CompEx): List[Try[Path]]

}
