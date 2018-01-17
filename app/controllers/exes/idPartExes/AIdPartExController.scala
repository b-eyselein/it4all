package controllers.exes.idPartExes

import java.nio.file.{Files, Path}

import controllers.Secured
import controllers.exes.BaseExerciseController
import model._
import model.core._
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import play.api.mvc._
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

abstract class AIdPartExController[Ex <: Exercise, CompEx <: CompleteEx[Ex], R <: EvaluationResult, CompResult <: CompleteResult[R], Tables <: ExerciseTableDefs[Ex, CompEx]]
(cc: ControllerComponents, dbcp: DatabaseConfigProvider, t: Tables, to: IdPartExToolObject)(implicit ec: ExecutionContext)
  extends BaseExerciseController[Ex, CompEx, R, CompResult, Tables](cc, dbcp, t, to) with Secured {

  type PartType <: ExPart

  protected def partTypeFromString(str: String): Option[PartType]

  trait ExPart {

    def urlName: String

    def partName: String

  }

  trait IdPartExIdentifier extends ExerciseIdentifier {

    val id: Int

    val part: PartType

  }

  override type ExIdentifier <: IdPartExIdentifier

  protected def identifier(id: Int, part: String): ExIdentifier

  def correct(id: Int, partStr: String): EssentialAction = futureWithUser { user =>
    implicit request =>
      correctAbstract(user, identifier(id, partStr), readSolutionFromPostRequest,
        onSubmitCorrectionResult(user, _), onSubmitCorrectionError(user, _))
  }

  def correctLive(id: Int, partStr: String): EssentialAction = futureWithUser { user =>
    implicit request => correctAbstract(user, identifier(id, partStr), readSolutionFromPutRequest, onLiveCorrectionResult, onLiveCorrectionError)
  }

  def exercise(id: Int, partStr: String): EssentialAction = futureWithUser { user =>
    implicit request =>
      partTypeFromString(partStr) match {
        case None       => Future(Redirect(toolObject.indexCall))
        case Some(part) =>
          futureCompleteExById(id) flatMap {
            case Some(exercise) =>
              log(user, ExerciseStartEvent(request, id))
              renderExercise(user, exercise, part) map (Ok(_))
            case None           => Future(Redirect(toolObject.indexCall))
          }
      }
  }

  protected def checkAndCreateSolDir(username: String, exercise: CompEx): Try[Path] =
    Try(Files.createDirectories(toolObject.solutionDirForExercise(username, exercise.ex)))

  // Views

  protected def renderExercise(user: User, exercise: CompEx, part: PartType): Future[Html]

  // Handlers for results

  protected def onSubmitCorrectionResult(user: User, result: CompResult): Result

  protected def onSubmitCorrectionError(user: User, error: Throwable): Result

  protected def onLiveCorrectionResult(result: CompResult): Result

  protected def onLiveCorrectionError(error: Throwable): Result

}
