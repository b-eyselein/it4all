package controllers.exes.idPartExes

import java.nio.file.{Files, Path}

import controllers.exes.{AToolMain, BaseExerciseController}
import model.core._
import model.{CompleteEx, Exercise, JsonFormat, User}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContent, Request}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

trait AIdPartExToolMain[PartType <: ExPart, SolType, ExType <: Exercise, CompEx <: CompleteEx[ExType]]
  extends AToolMain[ExType, CompEx] with JsonFormat {

  type R <: EvaluationResult

  type CompResult <: CompleteResult[R]

  BaseExerciseController.IdPartExToolMains += (this.urlPart -> this)

  def partTypeFromUrl(urlName: String): Option[PartType]

  def checkAndCreateSolDir(username: String, exercise: CompEx): Try[Path] =
    Try(Files.createDirectories(solutionDirForExercise(username, exercise.id)))

  def saveSolution(sol: SolType): Future[Boolean]

  def readOldSolution(user: User, exerciseId: Int, part: PartType): Future[Option[SolType]]

  // Reading solution from request

  def readSolutionFromPostRequest(user: User, id: Int)(implicit request: Request[AnyContent]): Option[SolType]

  def readSolutionFromPutRequest(user: User, id: Int)(implicit request: Request[AnyContent]): Option[SolType] = request.body.asJson flatMap (_.asObj) match {
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

  def readSolutionForPartFromJson(user: User, id: Int, jsValue: JsValue, part: PartType): Option[SolType]


  def onSubmitCorrectionResult(user: User, result: CompResult): Html

  def onSubmitCorrectionError(user: User, error: Throwable): Html

  def onLiveCorrectionResult(result: CompResult): JsValue

  def onLiveCorrectionError(error: Throwable): JsValue = {
    val msg: String = error match {
      case NoSuchExerciseException(notExistingId) => s"Es gibt keine Aufgabe mit der ID '$notExistingId'!"
      case SolutionTransferException              => "Es gab einen Fehler bei der Übertragung ihrer Lösung!"
      case other                                  => "Es gab einen anderen Fehler bei der Korrektur ihrer Lösung:\n" + other.getMessage
    }

    Json.obj("msg" -> msg)
  }

  // Correction

  def correctAbstract(user: User, id: Int, isLive: Boolean)(implicit request: Request[AnyContent], ec: ExecutionContext): Future[Try[Either[Html, JsValue]]] =
    readSolution(user, id, isLive) match {
      case None => Future(Failure(SolutionTransferException))

      case Some(solution) =>
        futureCompleteExById(id) flatMap {
          case None => Future(Failure(NoSuchExerciseException(id)))

          case Some(exercise) =>
            val futureResultTry: Future[Try[CompResult]] = correctEx(user, solution, exercise)

            futureResultTry map {
              case Success(res) =>
                if (isLive) Success(Right(onLiveCorrectionResult(res)))
                else Success(Left(onSubmitCorrectionResult(user, res)))
            }
        }
    }


  private def readSolution(user: User, id: Int, isLive: Boolean)(implicit request: Request[AnyContent]) =
    if (isLive) readSolutionFromPutRequest(user, id)
    else readSolutionFromPostRequest(user, id)

  protected def correctEx(user: User, sol: SolType, exercise: CompEx): Future[Try[CompResult]]

  // Views

  def renderExerciseById(user: User, id: Int, partStr: String)(implicit ec: ExecutionContext): Future[Option[Html]] = futureCompleteExById(id) flatMap {
    case Some(exercise) =>
      val part = partTypeFromUrl(partStr)
      renderExercise(user, exercise, part) map Some.apply
    case None           => Future(None)
  }

  def renderExercise(user: User, exercise: CompEx, maybePart: Option[PartType]): Future[Html]

}
