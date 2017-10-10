package controllers.core

import java.io.IOException
import java.nio.file.{Files, Path}

import io.ebean.Finder
import model.exercise.Exercise
import model.logging.{ExerciseCompletionEvent, ExerciseCorrectionEvent, ExerciseStartEvent}
import model.result.{CompleteResult, EvaluationResult}
import model.tools.IdExToolObject
import model.user.User
import play.Logger
import play.api.Configuration
import play.mvc.Result
import play.data.{DynamicForm, FormFactory}
import play.libs.Json
import play.mvc.{Controller, Results}
import play.twirl.api.Html

import scala.util.{Failure, Success, Try}

abstract class IdExController[E <: Exercise, R <: EvaluationResult]
(c: Configuration, f: FormFactory, val finder: Finder[Integer, E], val toolObject: IdExToolObject)
  extends BaseController(c, f) {

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

  def exercise(id: Int): Result = {
    val user = getUser
    finder.byId(id) match {
      case exercise if exercise != null =>
        log(user, new ExerciseStartEvent(Controller.request(), id))

        Results.ok(renderExercise(user, exercise))
      case _ => Results.redirect(controllers.routes.Application.index())
    }
  }

  def index(page: Int): Result = {
    val allExes = finder.all()
    val exes = allExes.subList(Math.max(0, (page - 1) * STEP), Math.min(page * STEP, allExes.size()))
    Results.ok(
      views.html.exesList.render(getUser, exes, renderExesListRest, toolObject, allExes.size() / STEP + 1)
    )
  }

  def checkAndCreateSolDir(username: String, exercise: E): Path = {
    val dir = toolObject.getSolDirForExercise(username, exercise)

    if (dir.toFile.exists) dir
    else {
      try {
        Files.createDirectories(dir)
      } catch {
        case e: IOException =>
          Logger.error(s"There was an error while creating the directory for an ${toolObject.exType}  solution: $dir", e)
          null
      }
    }
  }

  def correctEx(form: DynamicForm, exercise: E, user: User): Try[CompleteResult[R]]

  def renderCorrectionResult(user: User, correctionResult: CompleteResult[R]): Html =
    views.html.correction.render(toolObject.toolname.toUpperCase, correctionResult, renderResult(correctionResult),
      user, controllers.routes.Application.index())

  def renderExercise(user: User, exercise: E): Html

  def renderExesListRest: Html

  def renderResult(correctionResult: CompleteResult[R]): Html

}
