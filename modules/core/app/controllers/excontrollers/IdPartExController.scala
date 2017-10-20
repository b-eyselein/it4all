package controllers.excontrollers

import java.nio.file.{Files, Path}

import controllers.core.BaseController
import controllers.excontrollers.IdPartExController._
import io.ebean.Finder
import model.Solution
import model.exercise.Exercise
import model.logging.{ExerciseCompletionEvent, ExerciseStartEvent}
import model.result.{CompleteResult, EvaluationResult}
import model.tools.IdPartExToolObject
import model.user.User
import play.api.data.Form
import play.api.mvc.ControllerComponents
import play.api.libs.json.Json
import play.mvc.Security.Authenticated
import play.twirl.api.Html

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

object IdPartExController {
  val STEP = 10
}

@Authenticated(classOf[model.Secured])
abstract class IdPartExController[E <: Exercise, R <: EvaluationResult]
(cc: ControllerComponents, val finder: Finder[Integer, E], val toolObject: IdPartExToolObject)
  extends BaseController(cc) {

  type SolType <: Solution

  def solForm: Form[SolType]

  def correct(id: Int, part: String) = Action { implicit request =>
    solForm.bindFromRequest.fold(
      formWithErrors => BadRequest("There has been an error!"),
      solution => {
        val user = getUser
        correctEx(solution, finder.byId(id), user) match {
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
        correctEx(solution, finder.byId(id), user) match {
          case Success(correctionResult) =>
            log(user, new ExerciseCompletionEvent(request, id, correctionResult))

            Ok(renderResult(correctionResult))
          case Failure(error) =>
            val content = new Html(s"<pre>${error.getMessage}:\n${error.getStackTrace.mkString("\n")}</pre>")
            BadRequest(Json.toJson(error.getMessage))
        }
      }
    )
  }

  def exercise(id: Int, part: String) = Action { implicit request =>
    Option(finder.byId(id)) match {
      case Some(exercise) =>
        val user = getUser
        log(user, new ExerciseStartEvent(request, id))
        Ok(renderExercise(user, exercise))
      case None => Redirect(controllers.routes.Application.index())
    }
  }


  def index(page: Int) = Action { implicit request =>
    val allExes = finder.all
    val exes = allExes.subList(Math.max(0, (page - 1) * STEP), Math.min(page * STEP, allExes.size()))
    Ok(renderExes(getUser, exes.asScala.toList, allExes.size()))
  }

  protected def renderExes(user: User, exes: List[_ <: E], allExesSize: Int): Html =
    views.html.exesList.render(user, exes, renderExesListRest, toolObject, allExesSize / STEP + 1)


  protected def checkAndCreateSolDir(username: String, exercise: E): Try[Path] =
    Try(Files.createDirectories(toolObject.getSolDirForExercise(username, exercise)))


  protected def renderCorrectionResult(user: User, correctionResult: CompleteResult[R]): Html =
    views.html.correction.render(toolObject.toolname.toUpperCase, correctionResult, renderResult(correctionResult),
      user, controllers.routes.Application.index())

  protected def correctEx(sol: SolType, exercise: E, user: User): Try[CompleteResult[R]]

  protected def renderExercise(user: User, exercise: E): Html = new Html("")

  protected def renderExesListRest: Html = new Html("")

  protected def renderResult(correctionResult: CompleteResult[R]): Html = new Html("")

}
