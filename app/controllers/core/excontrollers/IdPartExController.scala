package controllers.core.excontrollers

import java.nio.file.{Files, Path}

import controllers.core.BaseController
import controllers.core.excontrollers.IdPartExController._
import model.core._
import model.core.result.{CompleteResult, EvaluationResult}
import model.core.tools.IdPartExToolObject
import model.{Exercise, User}
import play.api.data.Form
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import play.api.mvc.{ControllerComponents, EssentialAction}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object IdPartExController {
  val STEP = 10
}

abstract class IdPartExController[E <: Exercise, R <: EvaluationResult]
(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository, val toolObject: IdPartExToolObject)(implicit ec: ExecutionContext)
  extends BaseController(cc, dbcp, r) with Secured {

  type SolType <: Solution

  def solForm: Form[SolType]

  // Table queries

  import profile.api._

  type TQ <: repo.HasBaseValuesTable[E]

  def tq: TableQuery[TQ]

  def numOfExes: Future[Int] = db.run(tq.size.result)

  def allExes: Future[Seq[E]] = db.run(tq.result)

  def exById(id: Int): Future[Option[E]] = db.run(tq.findBy(_.id).apply(id).result.headOption)

  def correct(id: Int, part: String): EssentialAction = withUser { user =>
    //TODO
    implicit request =>
      solForm.bindFromRequest.fold(
        _ => BadRequest("There has been an error!"),
        solution => {
          correctEx(solution, null /* finder.byId(id)*/ , user) match {
            case Success(correctionResult) =>
              log(user, new ExerciseCompletionEvent[R](request, id, correctionResult))

              Ok(renderCorrectionResult(user, correctionResult))
            case Failure(error)            =>
              val content = new Html(s"<pre>${error.getMessage}:\n${error.getStackTrace.mkString("\n")}</pre>")
              BadRequest(views.html.main.render("Fehler", user, new Html(""), content))
          }
        }
      )
  }

  def correctLive(id: Int, part: String): EssentialAction = withUser { user =>
    //TODO
    implicit request =>
      solForm.bindFromRequest.fold(
        _ => BadRequest("There has been an error!"),
        solution => {
          correctEx(solution, null /* finder.byId(id)*/ , user) match {
            case Success(correctionResult) =>
              log(user, new ExerciseCompletionEvent[R](request, id, correctionResult))

              Ok(renderResult(correctionResult))
            case Failure(error)            => BadRequest(Json.toJson(error.getMessage))
          }
        }
      )
  }

  def exercise(id: Int, part: String): EssentialAction = futureWithUser { user =>
    implicit request =>
      exById(id).map {
        case Some(exercise) =>
          log(user, ExerciseStartEvent(request, id))
          Ok(renderExercise(user, exercise))
        case None           => Redirect(toolObject.indexCall)
      }
  }

  def index(page: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      allExes.map(allExes => {
        val exes = allExes.slice(Math.max(0, (page - 1) * STEP), Math.min(page * STEP, allExes.size))
        Ok(renderExes(user, exes, allExes.size))
      })
  }

  protected def renderExes(user: User, exes: Seq[E], allExesSize: Int): Html =
    views.html.core.exesList.render(user, exes, renderExesListRest, toolObject, allExesSize / STEP + 1)


  protected def checkAndCreateSolDir(username: String, exercise: E): Try[Path] =
    Try(Files.createDirectories(toolObject.getSolDirForExercise(username, exercise)))


  protected def renderCorrectionResult(user: User, correctionResult: CompleteResult[R]): Html =
    views.html.core.correction.render(toolObject.toolname.toUpperCase, correctionResult, renderResult(correctionResult),
      user, controllers.routes.Application.index())

  protected def correctEx(sol: SolType, exercise: Option[E], user: User): Try[CompleteResult[R]]

  protected def renderExercise(user: User, exercise: E): Html = new Html("")

  protected def renderExesListRest: Html = new Html("")

  protected def renderResult(correctionResult: CompleteResult[R]): Html = new Html("")

}
