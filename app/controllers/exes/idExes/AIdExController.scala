package controllers.exes.idExes

import java.nio.file.{Files, Path}

import controllers.Secured
import controllers.exes.{BaseExerciseController, IntExIdentifier}
import model._
import model.core._
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import play.api.mvc._
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

abstract class AIdExController[Ex <: Exercise, CompEx <: CompleteEx[Ex], R <: EvaluationResult, CompResult <: CompleteResult[R], Tables <: ExerciseTableDefs[Ex, CompEx]]
(cc: ControllerComponents, dbcp: DatabaseConfigProvider, t: Tables, to: IdExToolObject)(implicit ec: ExecutionContext)
  extends BaseExerciseController[Ex, CompEx, R, CompResult, Tables](cc, dbcp, t, to) with Secured {

  override type ExIdentifier = IntExIdentifier

  def correct(id: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      correctAbstract(user, IntExIdentifier(id), readSolutionFromPostRequest, res => Ok(renderCorrectionResult(user, res)),
        // FIXME: site...
        error => BadRequest(views.html.main.render("Fehler", user, new Html(""), new Html(""), new Html(s"<pre>${error.getMessage}:\n${error.getStackTrace mkString "\n"}</pre>"))))
  }

  def correctLive(id: Int): EssentialAction = futureWithUser { user =>
    implicit request => correctAbstract(user, IntExIdentifier(id), readSolutionFromPutRequest, onLiveCorrectionSuccess, error => BadRequest(Json.obj("message" -> error.getMessage)))
  }

  def exercise(id: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      futureCompleteExById(id) flatMap {
        case Some(exercise) =>
          log(user, ExerciseStartEvent(request, id))
          renderExercise(user, exercise) map (rendered => Ok(rendered))
        case None           => Future(Redirect(controllers.routes.Application.index()))
      }
  }

  protected def checkAndCreateSolDir(username: String, exercise: CompEx): Try[Path] =
    Try(Files.createDirectories(toolObject solutionDirForExercise(username, exercise.ex)))


  protected def renderCorrectionResult(user: User, correctionResult: CompResult): Html =
    views.html.core.correction.render(correctionResult, renderResult(correctionResult), user, toolObject)

  protected def renderExercise(user: User, exercise: CompEx): Future[Html]

  protected def renderResult(correctionResult: CompResult): Html

  protected def onLiveCorrectionSuccess(correctionResult: CompResult): Result

}