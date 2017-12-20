package controllers.exes.idExes

import java.nio.file.{Files, Path}

import controllers.Secured
import controllers.exes.BaseExerciseController
import model.core._
import model.core.tools.ExToolObject
import model.{Exercise, HasBaseValues, User}
import play.api.db.slick.DatabaseConfigProvider
import play.api.http.Writeable
import play.api.libs.json.Json
import play.api.mvc._
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

trait IdExToolObject extends ExToolObject {

  def exerciseRoute(exercise: HasBaseValues): Call

  override def exerciseRoutes(exercise: CompEx): Map[Call, String] = Map(exerciseRoute(exercise.ex) -> "Aufgabe bearbeiten")

  def correctLiveRoute(exercise: HasBaseValues): Call

  def correctRoute(exercise: HasBaseValues): Call

}

abstract class AIdExController[E <: Exercise, R <: EvaluationResult, CompResult <: CompleteResult[R]]
(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository, to: IdExToolObject)(implicit ec: ExecutionContext)
  extends BaseExerciseController[E](cc, dbcp, r, to) with Secured {

  def correct(id: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      correctAbstract(user, id, readSolutionFromPostRequest, renderCorrectionResult(user, _),
        error => views.html.main.render("Fehler", user, new Html(""), new Html(s"<pre>${error.getMessage}:\n${error.getStackTrace mkString "\n"}</pre>")))
  }

  def correctLive(id: Int): EssentialAction = futureWithUser { user =>
    implicit request => correctAbstract(user, id, readSolutionFromPutRequest, renderResult, error => Json.obj("message" -> error.getMessage))
  }

  private def correctAbstract[S, Err](user: User, id: Int, maybeSolution: Option[SolType],
                                      onCorrectionSuccess: CompResult => S, onCorrectionError: Throwable => Err)
                                     (implicit successWriteable: Writeable[S], errorWriteable: Writeable[Err], request: Request[AnyContent]): Future[Result] =
    maybeSolution match {
      case None           => Future(BadRequest("No solution!"))
      case Some(solution) => futureCompleteExById(id) map {
        case None           => NotFound("No such exercise!")
        case Some(exercise) => correctEx(user, solution, exercise) match {
          case Success(result) => Ok(onCorrectionSuccess(result))
          case Failure(error)  => BadRequest(onCorrectionError(error))
        }
      }
    }

  def exercise(id: Int): EssentialAction = futureWithUser { user =>
    implicit request =>
      futureCompleteExById(id) flatMap {
        case Some(exercise) =>
          log(user, ExerciseStartEvent(request, id))
          renderExercise(user, exercise) map (rendered => Ok(rendered))
        case None           => Future(Redirect(controllers.routes.Application.index()))
      }
  }

  protected def checkAndCreateSolDir(username: String, exercise: CompEx): Try[Path] =
    Try(Files.createDirectories(toolObject solutionDirForExercise(username, exercise.ex)))


  protected def renderCorrectionResult(user: User, correctionResult: CompResult): Html =
    views.html.core.correction.render(correctionResult, renderResult(correctionResult), user, toolObject)

  protected def correctEx(user: User, sol: SolType, exercise: CompEx): Try[CompResult]

  protected def renderExercise(user: User, exercise: CompEx): Future[Html]

  protected def renderResult(correctionResult: CompResult): Html

}