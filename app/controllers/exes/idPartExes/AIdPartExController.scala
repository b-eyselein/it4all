package controllers.exes.idPartExes

import java.nio.file.{Files, Path}

import controllers.Secured
import controllers.exes.BaseExerciseController
import model.core._
import model.core.tools.ExToolObject
import model._
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Json
import play.api.mvc._
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

trait IdPartExToolObject extends ExToolObject {

  def exParts: Map[String, String]

  def exerciseRoute(exercise: HasBaseValues, part: String): Call

  override def exerciseRoutes(exercise: CompEx): Map[Call, String] = exParts map (exPart => (exerciseRoute(exercise.ex, exPart._1), exPart._2))

  def correctLiveRoute(exercise: HasBaseValues, part: String): Call

  def correctRoute(exercise: HasBaseValues, part: String): Call

}


abstract class AIdPartExController[Ex <: Exercise, CompEx <: CompleteEx[Ex], R <: EvaluationResult, CompResult <: CompleteResult[R], Tables <: ExerciseTableDefs[Ex, CompEx]]
(cc: ControllerComponents, dbcp: DatabaseConfigProvider, t: Tables, to: IdPartExToolObject)(implicit ec: ExecutionContext)
  extends BaseExerciseController[Ex, CompEx, R, CompResult, Tables](cc, dbcp, t, to) with Secured {

  type PartType

  def partTypeFromString(str: String): Option[PartType]

  trait IdPartExIdentifier extends ExerciseIdentifier {

    val id: Int

    val part: PartType

  }

  override type ExIdentifier <: IdPartExIdentifier

  def identifier(id: Int, part: String): ExIdentifier

  def correct(id: Int, partStr: String): EssentialAction = futureWithUser { user =>
    implicit request =>
      correctAbstract(user, identifier(id, partStr), readSolutionFromPostRequest, res => Ok(renderCorrectionResult(user, res)),
        // FIXME: on error...
        error => BadRequest(views.html.main.render("Fehler", user, new Html(""), new Html(
          s"""<pre>${error.getMessage}: ${error.getStackTrace mkString "\n"}</pre>""".stripMargin))))
  }

  def correctLive(id: Int, partStr: String): EssentialAction = futureWithUser { user =>
    implicit request => correctAbstract(user, identifier(id, partStr), readSolutionFromPutRequest, res => Ok(renderResult(res)), error => BadRequest(Json.toJson(error.getMessage)))
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

  protected def renderExercise(user: User, exercise: CompEx, part: PartType): Future[Html]

  protected def renderResult(correctionResult: CompResult): Html

}
