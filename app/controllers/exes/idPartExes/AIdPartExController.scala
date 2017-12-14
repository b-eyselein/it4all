package controllers.exes.idPartExes

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

trait IdPartExToolObject extends ExToolObject {

  def exParts: Map[String, String]

  def exerciseRoute(exercise: HasBaseValues, part: String): Call

  override def exerciseRoutes(exercise: CompEx): Map[Call, String] = exParts map (exPart => (exerciseRoute(exercise.ex, exPart._1), exPart._2))

  def correctLiveRoute(exercise: HasBaseValues, part: String): Call

  def correctRoute(exercise: HasBaseValues, part: String): Call

}

abstract class AIdPartExController[E <: Exercise, R <: EvaluationResult, CompResult <: CompleteResult[R]]
(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository, to: IdPartExToolObject)(implicit ec: ExecutionContext)
  extends BaseExerciseController[E](cc, dbcp, r, to) with Secured {

  type PartType

  def partTypeFromString(str: String): Option[PartType]

  def correct(id: Int, partStr: String): EssentialAction = futureWithUser { user =>
    implicit request =>
      correctAbstract(user, partStr, id, readSolutionFromPostRequest, renderCorrectionResult(user, _),
        error => views.html.main.render("Fehler", user, new Html(""), new Html(
          s"""<pre>${error.getMessage}:
             |${error.getStackTrace mkString "\n"}</pre>""".stripMargin)))
  }

  def correctLive(id: Int, partStr: String): EssentialAction = futureWithUser { user =>
    implicit request => correctAbstract(user, partStr, id, readSolutionFromPutRequest, renderResult, error => Json.toJson(error.getMessage))
  }

  private def correctAbstract[S, Err](user: User, partStr: String, id: Int, maybeSolution: Option[SolType],
                                      onCorrectionSuccess: CompResult => S, onCorrectionError: Throwable => Err)
                                     (implicit successWriteable: Writeable[S], errorWriteable: Writeable[Err], request: Request[AnyContent]): Future[Result] =
    (partTypeFromString(partStr) zip maybeSolution).headOption match {
      case None                   => Future(BadRequest("No solution!"))
      case Some((part, solution)) => futureCompleteExById(id) map {
        case None           => NotFound("No such exercise!")
        case Some(exercise) => correctEx(user, solution, exercise, part) match {
          case Success(result) => Ok(onCorrectionSuccess(result))
          case Failure(error)  => BadRequest(onCorrectionError(error))
        }
      }
    }


  def exercise(id: Int, partStr: String): EssentialAction = futureWithUser { user =>
    implicit request =>
      partTypeFromString(partStr) match {
        case None       => Future(Redirect(toolObject.indexCall))
        case Some(part) =>
          futureCompleteExById(id) flatMap {
            case Some(exercise) =>
              log(user, ExerciseStartEvent(request, id))
              renderExercise(user, exercise, part) map (Ok(_))
            case None           => Future(Redirect(toolObject.indexCall))
          }
      }
  }

  protected def checkAndCreateSolDir(username: String, exercise: CompEx): Try[Path] =
    Try(Files.createDirectories(toolObject.solutionDirForExercise(username, exercise.ex)))


  protected def renderCorrectionResult(user: User, correctionResult: CompResult): Html =
    views.html.core.correction.render(correctionResult, renderResult(correctionResult), user, toolObject)

  protected def correctEx(user: User, sol: SolType, exercise: CompEx, part: PartType): Try[CompResult] = ???

  protected def renderExercise(user: User, exercise: CompEx, part: PartType): Future[Html] = ???

  protected def renderResult(correctionResult: CompResult): Html = ???

}
