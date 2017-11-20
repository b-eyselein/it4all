package controllers.core

import java.nio.file.{Files, Path}

import controllers.Secured
import model.core._
import model.core.tools.IdExToolObject
import model.{Exercise, User}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import play.api.mvc.{ControllerComponents, EssentialAction}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

abstract class AIdExController[E <: Exercise, R <: EvaluationResult]
(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository, to: IdExToolObject)(implicit ec: ExecutionContext)
  extends BaseExerciseController[E](cc, dbcp, r, to) with Secured {

  def correct(id: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      solForm.bindFromRequest.fold(_ => Future(BadRequest("There has been an error!")),
        solution => completeExById(id).map {
          case None           => BadRequest("TODO!")
          case Some(exercise) =>
            correctEx(solution, exercise, user) match {
              case Success(correctionResult) =>
                log(user, new ExerciseCompletionEvent[R](request, id, correctionResult))
                Ok(renderCorrectionResult(user, correctionResult))
              case Failure(error)            =>
                val content = new Html(s"<pre>${error.getMessage}:\n${error.getStackTrace mkString "\n"}</pre>")
                BadRequest(views.html.main.render("Fehler", user, new Html(""), content))
            }
        })
  }


  def correctLive(id: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      solForm.bindFromRequest.fold(_ => Future(BadRequest("There has been an error!")),
        solution => completeExById(id) map {
          case None           => BadRequest("TODO!")
          case Some(exercise) =>
            correctEx(solution, exercise, user) match {
              case Success(correctionResult) =>
                log(user, new ExerciseCompletionEvent[R](request, id, correctionResult))
                Ok(renderResult(correctionResult))
              case Failure(error)            => BadRequest(Json.toJson(error.getMessage))
            }
        })
  }

  def exercise(id: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      completeExById(id) map {
        case Some(exercise) =>
          log(user, ExerciseStartEvent(request, id))
          Ok(renderExercise(user, exercise))
        case None           => Redirect(controllers.routes.Application.index())
      }
  }

  protected def checkAndCreateSolDir(username: String, exercise: CompEx): Try[Path] =
    Try(Files.createDirectories(toolObject getSolDirForExercise(username, exercise.ex)))


  protected def renderCorrectionResult(user: User, correctionResult: CompleteResult[R]): Html =
    views.html.core.correction.render(correctionResult, renderResult(correctionResult), user, toolObject)

  protected def correctEx(sol: SolutionType, exercise: CompEx, user: User): Try[CompleteResult[R]]

  protected def renderExercise(user: User, exercise: CompEx): Html

  protected def renderResult(correctionResult: CompleteResult[R]): Html

}