package controllers.core

import java.nio.file.{Files, Path}

import model.core._
import model.core.result.{CompleteResult, EvaluationResult}
import model.core.tools.IdPartExToolObject
import model.{DbExercise, User}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import play.api.mvc.{ControllerComponents, EssentialAction}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

abstract class AIdPartExController[E <: DbExercise, R <: EvaluationResult]
(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository, to: IdPartExToolObject)(implicit ec: ExecutionContext)
  extends BaseExerciseController(cc, dbcp, r, to) with Secured {

  def correct(id: Int, part: String): EssentialAction = futureWithUser { user =>
    implicit request =>
      solForm.bindFromRequest.fold(_ => Future(BadRequest("There has been an error!")),
        solution => exById(id).map {
          case None     => BadRequest("TODO!")
          case Some(ex) => correctEx(user, solution, ex, part) match {
            case Success(correctionResult) =>
              log(user, new ExerciseCompletionEvent[R](request, id, correctionResult))

              Ok(renderCorrectionResult(user, correctionResult))
            case Failure(error)            =>
              BadRequest(views.html.main.render("Fehler", user, new Html(""), new Html(
                s"""<pre>${error.getMessage}:
                   |${error.getStackTrace.mkString("\n")}</pre>""".stripMargin)))
          }
        })
  }

  def correctLive(id: Int, part: String): EssentialAction = futureWithUser { user =>
    implicit request =>
      solForm.bindFromRequest.fold(_ => Future(BadRequest("There has been an error!")),
        solution => exById(id).map {
          case None     => BadRequest("TODO!")
          case Some(ex) => correctEx(user, solution, ex, part) match {
            case Success(correctionResult) =>
              log(user, new ExerciseCorrectionEvent[R](request, id, correctionResult))

              Ok(renderResult(correctionResult))
            case Failure(error)            => BadRequest(Json.toJson(error.getMessage))
          }
        })
  }


  def exercise(id: Int, part: String): EssentialAction = futureWithUser { user =>
    implicit request =>
      exById(id).map {
        case Some(exercise) =>
          log(user, ExerciseStartEvent(request, id))
          Ok(renderExercise(user, exercise))
        case None           => Redirect(toolObject.indexCall)
      }
  }

  protected def checkAndCreateSolDir(username: String, exercise: E): Try[Path] =
    Try(Files.createDirectories(toolObject.getSolDirForExercise(username, exercise)))


  protected def renderCorrectionResult(user: User, correctionResult: CompleteResult[R]): Html =
    views.html.core.correction.render(toolObject.toolname.toUpperCase, correctionResult, renderResult(correctionResult),
      user, controllers.routes.Application.index())

  protected def correctEx(user: User, sol: SolutionType, exercise: ExerciseType, part: String): Try[CompleteResult[R]]

  protected def renderExercise(user: User, exercise: ExerciseType): Html = new Html("")

  protected def renderResult(correctionResult: CompleteResult[R]): Html = new Html("")

}
