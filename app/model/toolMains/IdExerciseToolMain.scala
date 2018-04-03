package model.toolMains

import java.nio.file.{Files, Path}

import model.core.CoreConsts.solutionName
import model.core._
import model.persistence.SingleExerciseTableDefs
import model.{JsonFormat, PartSolution, User}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContent, Request}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

abstract class IdExerciseToolMain(urlPart: String)(implicit ec: ExecutionContext) extends ASingleExerciseToolMain(urlPart) with JsonFormat {

  // Abstract types

  type CompResult <: CompleteResult[R]

  type SolType <: PartSolution

  override type Tables <: SingleExerciseTableDefs[ExType, CompExType, SolType, PartType]

  // Methods

  def checkAndCreateSolDir(username: String, exercise: CompExType): Try[Path] =
    Try(Files.createDirectories(solutionDirForExercise(username, exercise.ex.id)))

  def futureSaveSolution(sol: SolType): Future[Boolean] = tables.futureSaveSolution(sol)

  def futureOldSolution(user: User, exerciseId: Int, partString: String)(implicit ec: ExecutionContext): Future[Option[SolType]] = partTypeFromUrl(partString) match {
    case None       => Future(None)
    case Some(part) => tables.futureOldSolution(user.username, exerciseId, part)
  }

  // Result handling

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

  // FIXME: change return type of function!
  def correctAbstract(user: User, id: Int, partStr: String, isLive: Boolean)(implicit request: Request[AnyContent], ec: ExecutionContext): Future[Try[Either[Html, JsValue]]] =
    partTypeFromUrl(partStr) match {
      case None       => Future(Failure(NoSuchPartException(partStr)))
      case Some(part) => readSolution(user, id, part, isLive) match {
        case None           => Future(Failure(SolutionTransferException))
        case Some(solution) => onSolution(user, solution, id, isLive)
      }
    }


  private def onSolution(user: model.User, solution: SolType, id: Int, isLive: Boolean): Future[Try[Either[Html, JsValue]]] = futureCompleteExById(id) flatMap {
    case None => Future(Failure(NoSuchExerciseException(id)))

    case Some(exercise) => futureSaveSolution(solution) flatMap { solutionSaved =>
      correctEx(user, solution, exercise, solutionSaved) map {
        case Success(res)   =>
          // FIXME: change return type of function!
          if (isLive) Success(Right(onLiveCorrectionResult(res)))
          else Success(Left(onSubmitCorrectionResult(user, res)))
        case Failure(error) => Failure(error)
      }
    }
  }


  private def readSolution(user: User, id: Int, part: PartType, isLive: Boolean)(implicit request: Request[AnyContent]): Option[SolType] =
    if (isLive) readSolutionFromPutRequest(user, id, part) else readSolutionFromPostRequest(user, id, part)

  def readSolutionFromPostRequest(user: User, id: Int, part: PartType)(implicit request: Request[AnyContent]): Option[SolType]

  def readSolutionFromPutRequest(user: User, id: Int, part: PartType)(implicit request: Request[AnyContent]): Option[SolType] =
    request.body.asJson flatMap (_.asObj) flatMap {
      _.field(solutionName) flatMap (solution => readSolutionForPartFromJson(user, id, solution, part))
    }

  def readSolutionForPartFromJson(user: User, id: Int, jsValue: JsValue, part: PartType): Option[SolType]

  protected def correctEx(user: User, sol: SolType, exercise: CompExType, solutionSaved: Boolean): Future[Try[CompResult]]

  // Views

  def renderExercise(user: User, exercise: CompExType, part: PartType, oldSolution: Option[SolType]): Html

  override def previewExercise(user: User, read: ReadAndSaveResult[CompExType]): Html =
    views.html.admin.idExes.idExercisePreview(user, read, this)

  override def adminExerciseList(admin: User, exes: Seq[CompExType]): Html =
    views.html.admin.idExes.idExerciseAdminListView(admin, exes, this)

}
