package controllers.core.excontrollers

import controllers.core.BaseController
import model.core._
import model.core.result.{CompleteResult, EvaluationResult}
import model.{Exercise, User}
import play.api.data.Form
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.{ControllerComponents, EssentialAction}
import play.twirl.api.Html

import scala.concurrent.ExecutionContext
import scala.util.Try

abstract class ExerciseCollectionController[E <: Exercise, C <: ExerciseCollection[E], R <: EvaluationResult]
(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository, exType: String)
(implicit ec: ExecutionContext)
  extends BaseController(cc, dbcp, r) with Secured {

  def exercises(id: Int): EssentialAction = withUser { _ => implicit request => Ok("TODO!") }

  type SolType <: Solution

  def solForm: Form[SolType]

  def correct(id: Int, part: String): EssentialAction = withUser { user =>
    implicit request =>
      solForm.bindFromRequest.fold(
        _ => BadRequest("There has been an error!"),
        solution => {
          //          correctPart(solution, exerciseFinder.byId(id), part, user) match {
          //            case Success(correctionResult) =>
          //              log(user, new ExerciseCompletionEvent[R](request, id, correctionResult))
          //              Ok(renderCorrectionResult(user, correctionResult))
          //            case Failure(error)            =>
          //              val content = new Html(s"<pre>${error.getMessage}:\n${error.getStackTrace.mkString("\n")}</pre>")
          //              BadRequest(views.html.main.render("Fehler", user, new Html(""), content))
          //          }
          Ok("TODO")
        }
      )
  }

  def correctLive(id: Int, part: String): EssentialAction = withUser { user =>
    implicit request =>
      solForm.bindFromRequest.fold(
        _ => BadRequest("There has been an error!"),
        solution => {
          //          correctPart(solution, exerciseFinder.byId(id), part, user) match {
          //            case Success(correctionResult) =>
          //              log(user, new ExerciseCompletionEvent[R](request, id, correctionResult))
          //              Ok(renderResult(correctionResult))
          //            case Failure(error)            => BadRequest(Json.obj("message" -> error.getMessage))
          //          }
          Ok("TODO")
        }
      )
  }

  private def renderCorrectionResult(user: User, correctionResult: CompleteResult[R]): Html =
    views.html.core.correction.render(exType.toUpperCase, correctionResult, renderResult(correctionResult),
      user, controllers.routes.Application.index())

  def renderResult(correctionResult: CompleteResult[R]): Html

  def correctPart(form: SolType, exercise: Option[E], part: String, user: User): Try[CompleteResult[R]]

}
