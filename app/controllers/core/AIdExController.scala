package controllers.core

import java.nio.file.{Files, Path}

import model.core._
import model.core.result.{CompleteResult, EvaluationResult}
import model.core.tools.IdExToolObject
import model.{DbExercise, User}
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{ControllerComponents, EssentialAction}
import play.twirl.api.Html

import scala.concurrent.ExecutionContext
import scala.util.Try

abstract class AIdExController[E <: DbExercise, R <: EvaluationResult]
(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository, to: IdExToolObject)(implicit ec: ExecutionContext)
  extends BaseExerciseController[E](cc, dbcp, r, to) with Secured {

  def correct(id: Int): EssentialAction = withUser { _ =>
    implicit request =>
      solForm.bindFromRequest.fold(
        _ => BadRequest("There has been an error!"),
        _ => {
          //          correctEx(solution, finder.byId(id), user) match {
          //            case Success(correctionResult) =>
          //              log(user, new ExerciseCompletionEvent[R](request, id, correctionResult))
          //
          //              Ok(renderCorrectionResult(user, correctionResult))
          //            case Failure(error)            =>
          //              val content = new Html(s"<pre>${error.getMessage}:\n${error.getStackTrace.mkString("\n")}</pre>")
          //              BadRequest(views.html.main.render("Fehler", user, new Html(""), content))
          //          }
          Ok("TODO")
        }
      )
  }

  def correctLive(id: Int): EssentialAction = withUser { _ =>
    implicit request =>
      solForm.bindFromRequest.fold(
        _ => BadRequest("There has been an error!"),
        _ => {
          //          correctEx(solution, finder.byId(id), user) match {
          //            case Success(correctionResult) =>
          //              log(user, new ExerciseCompletionEvent[R](request, id, correctionResult))
          //
          //              Ok(renderResult(correctionResult))
          //            case Failure(error)            => BadRequest(Json.toJson(error.getMessage))
          //          }
          Ok("TODO")
        }
      )
  }

  def exercise(id: Int): EssentialAction = withUser { _ =>
    implicit request =>
      //      finder.byId(id) match {
      //        case Some(exercise) =>
      //          log(user, ExerciseStartEvent(request, id))
      //          Ok(renderExercise(user, exercise))
      //        case None           => Redirect(controllers.routes.Application.index())
      //      }
      Ok("TODO")
  }

  protected def checkAndCreateSolDir(username: String, exercise: E): Try[Path] =
    Try(Files.createDirectories(toolObject.getSolDirForExercise(username, exercise)))


  protected def renderCorrectionResult(user: User, correctionResult: CompleteResult[R]): Html =
    views.html.core.correction.render(toolObject.toolname.toUpperCase, correctionResult, renderResult(correctionResult),
      user, controllers.routes.Application.index())

  protected def correctEx(sol: SolutionType, exercise: Option[E], user: User): Try[CompleteResult[R]]

  protected def renderExercise(user: User, exercise: ExerciseType): Html = new Html("")

  protected def renderExesListRest: Html //= new Html("")

  protected def renderResult(correctionResult: CompleteResult[R]): Html = new Html("")

}