package controllers.core

import java.nio.file.{Files, Path}

import io.ebean.Finder
import model.exercise.Exercise
import model.logging.{ExerciseCompletionEvent, ExerciseCorrectionEvent, ExerciseStartEvent}
import model.result.{CompleteResult, EvaluationResult}
import model.tools.IdPartExToolObject
import model.user.User
import play.data.{DynamicForm, FormFactory}
import play.libs.Json
import play.mvc.Results.{badRequest, ok, redirect}
import play.mvc.Security.Authenticated
import play.mvc.{Controller, Result}
import play.twirl.api.Html

import scala.util.{Failure, Success, Try}

@Authenticated(classOf[model.Secured])
abstract class IdPartExController[E <: Exercise, R <: EvaluationResult]
(f: FormFactory, val finder: Finder[Integer, E], val toolObject: IdPartExToolObject)
  extends BaseController(f) {

  val STEP = 10

  def correct(id: Int, part: String): Result = {
    val user = getUser
    correctEx(factory.form().bindFromRequest(), finder.byId(id), user) match {
      case Success(correctionResult) =>
        log(user, new ExerciseCompletionEvent(Controller.request, id, correctionResult))

        ok(renderCorrectionResult(user, correctionResult))
      case Failure(error) =>
        val content = new Html(s"<pre>${error.getMessage}:\n${error.getStackTrace.mkString("\n")}</pre>")
        badRequest(views.html.main.render("Fehler", user, new Html(""), content))
    }
  }

  def correctLive(id: Int, part: String): Result = {
    val user = getUser
    correctEx(factory.form().bindFromRequest(), finder.byId(id), user) match {
      case Success(correctionResult) =>
        log(user, new ExerciseCorrectionEvent(Controller.request, id, correctionResult))

        ok(renderResult(correctionResult))
      case Failure(error) =>
        badRequest(Json.toJson(error.getMessage))
    }
  }

  def exercise(id: Int, part: String): Result = Option(finder.byId(id)) match {
    case Some(exercise) =>
      val user = getUser
      log(user, new ExerciseStartEvent(Controller.request(), id))
      ok(renderExercise(user, exercise))
    case None => redirect(controllers.routes.Application.index())
  }


  def index(page: Int): Result = {
    val allExes = finder.all
    val exes = allExes.subList(Math.max(0, (page - 1) * STEP), Math.min(page * STEP, allExes.size()))
    ok(renderExes(getUser, exes, allExes.size()))
  }

  protected def renderExes(user: User, exes: java.util.List[_ <: E], allExesSize: Int): Html =
    views.html.exesList.render(user, exes, renderExesListRest, toolObject, allExesSize / STEP + 1)


  protected def checkAndCreateSolDir(username: String, exercise: E): Try[Path] =
    Try(Files.createDirectories(toolObject.getSolDirForExercise(username, exercise)))


  protected def renderCorrectionResult(user: User, correctionResult: CompleteResult[R]): Html =
    views.html.correction.render(toolObject.toolname.toUpperCase, correctionResult, renderResult(correctionResult),
      user, controllers.routes.Application.index())

  protected def correctEx(form: DynamicForm, exercise: E, user: User): Try[CompleteResult[R]]

  protected def renderExercise(user: User, exercise: E): Html = new Html("")

  protected def renderExesListRest: Html = new Html("")

  protected def renderResult(correctionResult: CompleteResult[R]): Html = new Html("")

}
