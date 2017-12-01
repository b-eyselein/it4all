package controllers.exes.idPartExes

import java.nio.file.{Files, Path}

import controllers.exes.BaseExerciseController
import controllers.Secured
import model.core._
import model.core.tools.ExToolObject
import model.{Exercise, HasBaseValues, User}
import play.api.data.Form
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import play.api.mvc.{Call, ControllerComponents, EssentialAction}
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

abstract class AIdPartExController[E <: Exercise, R <: EvaluationResult]
(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository, to: IdPartExToolObject)(implicit ec: ExecutionContext)
  extends BaseExerciseController[E](cc, dbcp, r, to) with Secured {

  // Reading solution from Request

  type SolutionType <: Solution

  def solForm: Form[SolutionType]

  def correct(id: Int, part: String): EssentialAction = futureWithUser { user =>
    implicit request =>
      solForm.bindFromRequest.fold(_ => Future(BadRequest("There has been an error!")),
        solution => completeExById(id) map {
          case None => BadRequest("TODO!")

          case Some(ex) => correctEx(user, solution, ex, part) match {
            case Success(correctionResult) =>
              log(user, new ExerciseCompletionEvent[R](request, id, correctionResult))
              Ok(renderCorrectionResult(user, correctionResult))

            case Failure(error) =>
              BadRequest(views.html.main.render("Fehler", user, new Html(""), new Html(
                s"""<pre>${error.getMessage}:
                   |${error.getStackTrace mkString "\n"}</pre>""".stripMargin)))
          }
        })
  }

  def correctLive(id: Int, part: String): EssentialAction = futureWithUser { user =>
    implicit request =>
      solForm.bindFromRequest.fold(_ => Future(BadRequest("There has been an error!")),
        solution => completeExById(id) map {
          case None => BadRequest("TODO!")

          case Some(ex) => correctEx(user, solution, ex, part) match {
            case Success(correctionResult) =>
              log(user, new ExerciseCorrectionEvent[R](request, id, correctionResult))
              Ok(renderResult(correctionResult))

            case Failure(error) => BadRequest(Json.toJson(error.getMessage))
          }
        })
  }


  def exercise(id: Int, part: String): EssentialAction = futureWithUser { user =>
    implicit request =>
      completeExById(id) flatMap {
        case Some(exercise) =>
          log(user, ExerciseStartEvent(request, id))
          renderExercise(user, exercise, part) map (Ok(_))
        case None           => Future(Redirect(toolObject.indexCall))
      }
  }

  protected def checkAndCreateSolDir(username: String, exercise: CompEx): Try[Path] =
    Try(Files.createDirectories(toolObject.solutionDirForExercise(username, exercise.ex)))


  protected def renderCorrectionResult(user: User, correctionResult: CompleteResult[R]): Html =
    views.html.core.correction.render(correctionResult, renderResult(correctionResult), user, toolObject)

  protected def correctEx(user: User, sol: SolutionType, exercise: CompEx, part: String): Try[CompleteResult[R]] = ???

  protected def renderExercise(user: User, exercise: CompEx, part: String): Future[Html] = ???

  protected def renderResult(correctionResult: CompleteResult[R]): Html = ???

}
