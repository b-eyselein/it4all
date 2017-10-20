package controllers.excontrollers

import controllers.core.BaseController
import io.ebean.Finder
import model.Solution
import model.exercise.{Exercise, ExerciseCollection}
import model.logging.ExerciseCompletionEvent
import model.result.CompleteResult
import model.user.User
import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc.ControllerComponents
import play.mvc.Security.Authenticated
import play.twirl.api.Html

import scala.util.{Failure, Success, Try}

@Authenticated(classOf[model.Secured])
abstract class ExerciseCollectionController[E <: Exercise, C <: ExerciseCollection[E]]
(cc: ControllerComponents, exType: String, finder: Finder[Integer, C], exFinder: Finder[Integer, E])
  extends BaseController(cc) {

  def exercises(id: Int) = Action { implicit request => Ok("TODO!") }


  type SolType <: Solution

  def solForm: Form[SolType]

  def correct(id: Int, part: String) = Action { implicit request =>
    solForm.bindFromRequest.fold(
      formWithErrors => BadRequest("There has been an error!"),
      solution => {
        val user = getUser
        correctPart(solution, exFinder.byId(id), part, user) match {
          case Success(correctionResult) =>
            log(user, new ExerciseCompletionEvent(request, id, correctionResult))

            Ok(renderCorrectionResult(user, correctionResult))
          case Failure(error) =>
            val content = new Html(s"<pre>${error.getMessage}:\n${error.getStackTrace.mkString("\n")}</pre>")
            BadRequest(views.html.main.render("Fehler", user, new Html(""), content))
        }
      }
    )
  }

  def correctLive(id: Int, part: String) = Action { implicit request =>
    solForm.bindFromRequest.fold(
      formWithErrors => BadRequest("There has been an error!"),
      solution => {
        val user = getUser
        correctPart(solution, exFinder.byId(id), part, user) match {
          case Success(correctionResult) =>
            log(user, new ExerciseCompletionEvent(request, id, correctionResult))

            Ok(renderResult(correctionResult))
          case Failure(error) =>
            BadRequest(Json.obj("message" -> error.getMessage))
        }
      }
    )
  }

  def renderCorrectionResult(user: User, correctionResult: CompleteResult[_]): Html =
    views.html.correction.render(exType.toUpperCase, correctionResult, renderResult(correctionResult),
      user, controllers.routes.Application.index())

  def renderResult(correctionResult: CompleteResult[_]): Html

  def correctPart(form: SolType, exercise: E, part: String, user: User): Try[CompleteResult[_]]

}
