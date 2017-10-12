package controllers.core

import java.nio.file.{Files, Path}

import io.ebean.Finder
import model.exercise.Exercise
import model.logging.{ExerciseCompletionEvent, ExerciseCorrectionEvent, ExerciseStartEvent}
import model.result.{CompleteResult, EvaluationResult}
import model.tools.IdExToolObject
import model.user.User
import play.data.{DynamicForm, FormFactory}
import play.libs.Json
import play.mvc.{Controller, Result, Results}
import play.twirl.api.Html

import scala.util.{Failure, Success, Try}

abstract class IdExController[E <: Exercise, R <: EvaluationResult]
(f: FormFactory, val finder: Finder[Integer, E], val toolObject: IdExToolObject)
  extends BaseController(f) {

  val STEP = 10

  def correct(id: Int): Result = {
    val user = getUser
    correctEx(factory.form().bindFromRequest(), finder.byId(id), user) match {
      case Success(correctionResult) =>
        log(user, new ExerciseCompletionEvent(Controller.request, id, correctionResult))

        Results.ok(renderCorrectionResult(user, correctionResult))
      case Failure(error) =>
        val content = new Html(s"<pre>${error.getMessage}:\n${error.getStackTrace.mkString("\n")}</pre>")
        Results.badRequest(views.html.main.render("Fehler", user, new Html(""), content))
    }
  }

  def correctLive(id: Int): Result = {
    val user = getUser
    correctEx(factory.form().bindFromRequest(), finder.byId(id), user) match {
      case Success(correctionResult) =>
        log(user, new ExerciseCorrectionEvent(Controller.request, id, correctionResult))

        Results.ok(renderResult(correctionResult))
      case Failure(error) =>
        Results.badRequest(Json.toJson(error.getMessage))
    }
  }

  def exercise(id: Int): Result = Option(finder.byId(id)) match {
    case Some(exercise) =>
      val user = getUser
      log(user, new ExerciseStartEvent(Controller.request(), id))
      Results.ok(renderExercise(user, exercise))
    case None => Results.redirect(controllers.routes.Application.index())
  }


  def index(page: Int): Result = {
    val allExes = finder.all
    val exes = allExes.subList(Math.max(0, (page - 1) * STEP), Math.min(page * STEP, allExes.size()))
    Results.ok(views.html.exesList.render(getUser, exes, renderExesListRest, toolObject, allExes.size() / STEP + 1))
  }

  protected def checkAndCreateSolDir(username: String, exercise: E): Try[Path] =
    Try(Files.createDirectories(toolObject.getSolDirForExercise(username, exercise)))


  protected def renderCorrectionResult(user: User, correctionResult: CompleteResult[R]): Html =
    views.html.correction.render(toolObject.toolname.toUpperCase, correctionResult, renderResult(correctionResult),
      user, controllers.routes.Application.index())

  protected def correctEx(form: DynamicForm, exercise: E, user: User): Try[CompleteResult[R]]

  protected def renderExercise(user: User, exercise: E): Html

  protected def renderExesListRest: Html

  protected def renderResult(correctionResult: CompleteResult[R]): Html

}
