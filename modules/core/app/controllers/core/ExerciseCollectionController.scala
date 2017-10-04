package controllers.core

import scala.util.{Failure, Success, Try}

import io.ebean.Finder
import model.exercise.{Exercise, ExerciseCollection}
import model.logging.{ExerciseCompletionEvent, ExerciseCorrectionEvent}
import model.result.CompleteResult
import model.user.User
import play.data.{DynamicForm, FormFactory}
import play.libs.Json
import play.mvc.{Result, Results}
import play.mvc.Security.Authenticated
import play.twirl.api.Html
import model.tools.DoubleIdExToolObject

@Authenticated(classOf[model.Secured])
abstract class ExerciseCollectionController[E <: Exercise, C <: ExerciseCollection[E]](
  f: FormFactory, exType: String, finder: Finder[Integer, C], exFinder: Finder[Integer, E])
  extends BaseController(f) {

  def exercises(id: Int): Result = Results.ok("TODO!")

  def correct(id: Int, part: String) = {
    val user = BaseController.getUser

    correctPart(factory.form().bindFromRequest(), exFinder.byId(id), part, user) match {
      case Success(result) ⇒
        BaseController.log(user, new ExerciseCompletionEvent(null, id, result))
        Results.ok(renderCorrectionResult(user, result))

      case Failure(error) ⇒ Results.badRequest("TODO!")
    }
  }

  def correctLive(id: Int, part: String) = {
    val user = BaseController.getUser

    correctPart(factory.form().bindFromRequest(), exFinder.byId(id), part, user) match {
      case Success(correctionResult) ⇒
        BaseController.log(user, new ExerciseCorrectionEvent(null, id, correctionResult))
        Results.ok(renderResult(correctionResult))

      case Failure(error) ⇒ Results.badRequest(Json.toJson(error.getMessage()))
    }
  }

  def renderCorrectionResult(user: User, correctionResult: CompleteResult[_]) =
    views.html.correction.render(exType.toUpperCase, correctionResult, renderResult(correctionResult),
                                 user, controllers.routes.Application.index());

  def renderResult(correctionResult: CompleteResult[_]): Html

  def correctPart(form: DynamicForm, exercise: E, part: String, user: User): Try[CompleteResult[_]]

}
