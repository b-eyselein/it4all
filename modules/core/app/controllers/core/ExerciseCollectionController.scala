package controllers.core

import io.ebean.Finder
import model.exercise.{Exercise, ExerciseCollection}
import model.logging.{ExerciseCompletionEvent, ExerciseCorrectionEvent}
import model.result.CompleteResult
import model.user.User
import play.api.Configuration
import play.data.{DynamicForm, FormFactory}
import play.libs.Json
import play.mvc.Security.Authenticated
import play.mvc.{Result, Results}
import play.twirl.api.Html

import scala.util.{Failure, Success, Try}

@Authenticated(classOf[model.Secured])
abstract class ExerciseCollectionController[E <: Exercise, C <: ExerciseCollection[E]]
(c: Configuration, f: FormFactory, exType: String, finder: Finder[Integer, C], exFinder: Finder[Integer, E])
  extends BaseController(c, f) {

  def exercises(id: Int): Result = Results.ok("TODO!")

  def correct(id: Int, part: String): Result = {
    val user = getUser

    correctPart(factory.form().bindFromRequest(), exFinder.byId(id), part, user) match {
      case Success(result) =>
        log(user, new ExerciseCompletionEvent(null, id, result))
        Results.ok(renderCorrectionResult(user, result))

      case Failure(error) => Results.badRequest("TODO!")
    }
  }

  def correctLive(id: Int, part: String): Result = {
    val user = getUser

    correctPart(factory.form().bindFromRequest(), exFinder.byId(id), part, user) match {
      case Success(correctionResult) =>
        log(user, new ExerciseCorrectionEvent(null, id, correctionResult))
        Results.ok(renderResult(correctionResult))

      case Failure(error) => Results.badRequest(Json.toJson(error.getMessage))
    }
  }

  def renderCorrectionResult(user: User, correctionResult: CompleteResult[_]): Html =
    views.html.correction.render(exType.toUpperCase, correctionResult, renderResult(correctionResult),
      user, controllers.routes.Application.index())

  def renderResult(correctionResult: CompleteResult[_]): Html

  def correctPart(form: DynamicForm, exercise: E, part: String, user: User): Try[CompleteResult[_]]

}
