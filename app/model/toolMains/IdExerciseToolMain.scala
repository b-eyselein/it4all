package model.toolMains

import java.nio.file.{Files, Path}

import model.core.CoreConsts.solutionName
import model.core._
import model.core.result.CompleteResult
import model.persistence.SingleExerciseTableDefs
import model.{JsonFormat, PartSolution, User}
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContent, Call, Request}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

abstract class IdExerciseToolMain(urlPart: String)(implicit ec: ExecutionContext) extends ASingleExerciseToolMain(urlPart) with JsonFormat {

  // Abstract types

  type CompResult <: CompleteResult[R]

  type SolType <: PartSolution[PartType]

  override type Tables <: SingleExerciseTableDefs[ExType, CompExType, SolType, PartType]

  // Methods

  def checkAndCreateSolDir(username: String, exercise: CompExType): Try[Path] =
    Try(Files.createDirectories(solutionDirForExercise(username, exercise.ex.id)))

  def futureSaveSolution(sol: SolType): Future[Boolean] = tables.futureSaveSolution(sol)

  def futureOldOrDefaultSolution(user: User, exerciseId: Int, part: PartType)(implicit ec: ExecutionContext): Future[Option[SolType]] =
    tables.futureOldSolution(user.username, exerciseId, part)


  // Result handling

  def onSubmitCorrectionResult(user: User, pointsSaved: Boolean, result: CompResult): Html = ???

  def onSubmitCorrectionError(user: User, error: Throwable): Html = error match {
    case NoSuchExerciseException(_) => Html(error.getMessage)
    case NoSuchPartException(_)     => Html(error.getMessage)
    case SolutionTransferException  => Html(error.getMessage)
    case err                        =>
      Logger.error("Error while submit correction: " + err)
      err.printStackTrace()
      views.html.core.correctionError(user, OtherCorrectionException(err))
  }

  def onLiveCorrectionResult(pointsSaved: Boolean, result: CompResult): JsValue

  def onLiveCorrectionError(error: Throwable): JsValue = {
    Logger.error("There has been an correction error: ", error)
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

  def futureSampleSolutionForExerciseAndPart(id: Int, part: PartType): Future[String]

  private def onSolution(user: model.User, solution: SolType, id: Int, isLive: Boolean): Future[Try[Either[Html, JsValue]]] = futureCompleteExById(id) flatMap {
    case None => Future(Failure(NoSuchExerciseException(id)))
    // FIXME: change return type of function!

    case Some(exercise) => futureSaveSolution(solution) flatMap { solutionSaved =>
      correctEx(user, solution, exercise, solutionSaved) flatMap {
        case Success(res)   =>
          // FIXME: save result...
          tables.futureSaveResult(user.username, exercise.id, solution.part, res.points, res.maxPoints) map { pointsSaved =>
            if (isLive) Success(Right(onLiveCorrectionResult(pointsSaved, res)))
            else Success(Left(onSubmitCorrectionResult(user, pointsSaved, res)))
          }
        case Failure(error) => Future(Failure(error))
      }
    }
  }

  private def readSolution(user: User, id: Int, part: PartType, isLive: Boolean)(implicit request: Request[AnyContent]): Option[SolType] =
    if (isLive) readSolutionFromPutRequest(user, id, part) else readSolutionFromPostRequest(user, id, part)

  protected def readSolutionFromPostRequest(user: User, id: Int, part: PartType)(implicit request: Request[AnyContent]): Option[SolType]

  protected def readSolutionFromPutRequest(user: User, id: Int, part: PartType)(implicit request: Request[AnyContent]): Option[SolType] =
    request.body.asJson flatMap (_.asObj) flatMap {
      _.field(solutionName) flatMap (solution => readSolutionForPartFromJson(user, id, solution, part))
    }

  protected def readSolutionForPartFromJson(user: User, id: Int, jsValue: JsValue, part: PartType): Option[SolType]

  protected def correctEx(user: User, sol: SolType, exercise: CompExType, solutionSaved: Boolean): Future[Try[CompResult]]

  // Views

  override def exercisesOverviewForIndex: Html = Html(
    s"""<div class="form-group">
       |  <a class="btn btn-primary btn-block" href="${controllers.routes.ExerciseController.exerciseList(urlPart)}">Zu den Übungsaufgaben</a>
       |</div>""".stripMargin)

  override def adminIndexView(admin: User): Future[Html] = statistics map { stats =>
    views.html.admin.idExes.idExerciseAdminIndex(admin, stats, this)
  }

  override def previewExercise(user: User, read: ReadAndSaveResult[CompExType]): Html =
    views.html.admin.idExes.idExercisePreview(user, read, this)

  override def adminExerciseList(admin: User, exes: Seq[CompExType]): Html =
    views.html.admin.idExes.idExerciseAdminListView(admin, exes, this)

  def renderExercise(user: User, exercise: CompExType, part: PartType, oldSolution: Option[SolType]): Html

  // Calls

  override def indexCall: Call = controllers.routes.MainExerciseController.index(this.urlPart)

  def exerciseRoutesForUser(user: User, exercise: CompExType): Future[Seq[CallForExPart]] = Future.sequence(exParts map {
    exPart: PartType =>
      // FIXME: check if user can solve this part!

      if (exercise.hasPart(exPart)) {
        tables.futureUserCanSolvePartOfExercise(user.username, exercise.id, exPart) map {
          enabled => Some(CallForExPart(exPart, controllers.routes.ExerciseController.exercise(urlPart, exercise.ex.id, exPart.urlName), enabled))
        }


      } else Future(None)
  }).map(_.flatten)

}
