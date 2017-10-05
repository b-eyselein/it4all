package controllers.core

import scala.util.{Failure, Success, Try}

import io.ebean.Finder
import model.Secured
import model.exercise.Exercise
import model.logging.{ExerciseCompletionEvent, ExerciseCorrectionEvent, ExerciseStartEvent}
import model.result.{CompleteResult, EvaluationResult}
import model.tools.IdExToolObject
import model.user.User
import play.data.{DynamicForm, FormFactory}
import play.libs.Json
import play.mvc.{Controller, Results}
import play.mvc.Security.Authenticated
import play.twirl.api.Html
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import play.Logger

abstract class IdExController[E <: Exercise, R <: EvaluationResult](
  f: FormFactory, val exType: String, val finder: Finder[Integer, E], val toolObject: IdExToolObject)
  extends BaseController(f) {

  val STEP = 10

  def getExType = exType

  def correct(id: Int) = {
    val user = BaseController.getUser
    correctEx(factory.form().bindFromRequest(), finder.byId(id), user) match {
      case Success(correctionResult) ⇒
        BaseController.log(user, new ExerciseCompletionEvent(Controller.request, id, correctionResult))

        Results.ok(renderCorrectionResult(user, correctionResult))
      case Failure(error) ⇒
        Results.badRequest("TODO!")
    }
  }

  def correctLive(id: Int) = {
    val user = BaseController.getUser
    correctEx(factory.form().bindFromRequest(), finder.byId(id), user) match {
      case Success(correctionResult) ⇒
        BaseController.log(user, new ExerciseCorrectionEvent(Controller.request, id, correctionResult))

        Results.ok(renderResult(correctionResult))
      case Failure(error) ⇒
        Results.badRequest(Json.toJson(error.getMessage()))
    }
  }

  def exercise(id: Int) = {
    val user = BaseController.getUser
    finder.byId(id) match {
      case exercise if exercise != null ⇒
        BaseController.log(user, new ExerciseStartEvent(Controller.request(), id))

        Results.ok(renderExercise(user, exercise))
      case _ ⇒ Results.redirect(controllers.routes.Application.index())
    }
  }

  def index(page: Int) = {
    val allExes = finder.all()
    val exes = allExes.subList(Math.max(0, (page - 1) * STEP), Math.min(page * STEP, allExes.size()))
    Results.ok(
      views.html.exesList.render(BaseController.getUser, exes, renderExesListRest, toolObject, allExes.size() / STEP + 1))
  }

  def checkAndCreateSolDir(username: String, exercise: E): Path = {
    val dir = BaseController.getSolDirForExercise(username, exType, exercise)

    dir.toFile.exists match {
      case true ⇒ dir
      case false ⇒
        try {
          Files.createDirectories(dir)
        } catch {
          case e: IOException ⇒
            Logger.error(s"There was an error while creating the directory for an $exType  solution: $dir", e)
            null
        }
    }
  }

  def correctEx(form: DynamicForm, exercise: E, user: User): Try[CompleteResult[R]]

  def getSampleDir = BaseController.getSampleDir(toolObject.exType)

  def renderCorrectionResult(user: User, correctionResult: CompleteResult[R]) =
    views.html.correction.render(exType.toUpperCase, correctionResult, renderResult(correctionResult),
                                 user, controllers.routes.Application.index())

  def renderExercise(user: User, exercise: E): Html

  def renderExesListRest: Html

  def renderResult(correctionResult: CompleteResult[R]): Html

}
