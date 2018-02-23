package controllers.exes.idPartExes

import java.nio.file.{Files, Path}

import controllers.Secured
import controllers.exes.BaseExerciseController
import model._
import model.core._
import model.core.tools.IdPartExToolObject
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.JsValue
import play.api.mvc._
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

abstract class AIdPartExController[PartType <: ExPart](cc: ControllerComponents, dbcp: DatabaseConfigProvider, to: IdPartExToolObject[PartType])(implicit ec: ExecutionContext)
  extends BaseExerciseController(cc, dbcp, to) with Secured with JsonFormat {

  protected def partTypeFromUrl(urlName: String): Option[PartType]

  override def readSolutionFromPutRequest(user: User, id: Int)(implicit request: Request[AnyContent]): Option[SolType] = request.body.asJson flatMap (_.asObj) match {
    case Some(jsObj) =>

      val partAndSolution: Option[(PartType, JsValue)] = for {
        part <- jsObj.stringField("part") flatMap partTypeFromUrl
        solution <- jsObj.field("solution")
      } yield (part, solution)

      partAndSolution flatMap {
        case (part, solution) => readSolutionForPartFromJson(user, id, solution, part)
      }

    case None => ???
  }

  protected def readSolutionForPartFromJson(user: User, id: Int, jsValue: JsValue, part: PartType): Option[SolType]


  def exercise(id: Int, partStr: String): EssentialAction = futureWithUser { user =>
    implicit request =>
      partTypeFromUrl(partStr) match {
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

  protected def checkAndCreateSolDir(username: String, exercise: CompExType): Try[Path] =
    Try(Files.createDirectories(toolObject.solutionDirForExercise(username, exercise.id)))

  // Views

  protected def renderExercise(user: User, exercise: CompExType, part: PartType): Future[Html]

}
