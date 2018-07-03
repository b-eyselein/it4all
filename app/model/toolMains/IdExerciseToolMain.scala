package model.toolMains

import java.nio.file.{Files, Path}

import model.core.CoreConsts.solutionName
import model.core._
import model.core.result.CompleteResult
import model.persistence.SingleExerciseTableDefs
import model.{DBPartSolution, JsonFormat, SemanticVersion, User}
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContent, Call, Request}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

abstract class IdExerciseToolMain(urlPart: String)(implicit ec: ExecutionContext) extends ASingleExerciseToolMain(urlPart) with JsonFormat {

  // Abstract types

  type CompResult <: CompleteResult[R]

  type SolType

  type DBSolType <: DBPartSolution[PartType, SolType]

  override type Tables <: SingleExerciseTableDefs[ExType, CompExType, SolType, DBSolType, PartType]

  // Methods

  def checkAndCreateSolDir(username: String, exercise: CompExType): Try[Path] =
    Try(Files.createDirectories(solutionDirForExercise(username, exercise.ex.id)))

  def futureSaveSolution(sol: DBSolType): Future[Boolean] = tables.futureSaveSolution(sol)

  def futureOldOrDefaultSolution(user: User, exerciseId: Int, part: PartType): Future[Option[DBSolType]] =
    tables.futureOldSolution(user.username, exerciseId, part)

  def futureSolutionsForExercise(exerciseId: Int): Future[Seq[DBSolType]] = tables.futureOldSolutions(exerciseId)

  // Result handling

  def onLiveCorrectionResult(solutionSaved: Boolean, result: CompResult): JsValue = result.toJson(solutionSaved)

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
  def correctAbstract(user: User, id: Int, partStr: String)(implicit request: Request[AnyContent], ec: ExecutionContext): Future[Try[JsValue]] =
    partTypeFromUrl(partStr) match {
      case None       => Future(Failure(NoSuchPartException(partStr)))
      case Some(part) =>
        futureCompleteExById(id) flatMap {
          case None => Future(Failure(NoSuchExerciseException(id)))

          case Some(exercise) =>
            readSolution(user, exercise, part) match {
              case None           => Future(Failure(SolutionTransferException))
              case Some(solution) => onSolution(user, solution, exercise, part)
            }
        }
    }

  def futureSampleSolutionForExerciseAndPart(id: Int, part: PartType): Future[String]

  protected def instantiateSolution(username: String, exercise: CompExType, part: PartType, solution: SolType, points: Double, maxPoints: Double): DBSolType

  private def onSolution(user: model.User, solution: SolType, exercise: CompExType, part: PartType): Future[Try[JsValue]] =
    correctEx(user, solution, exercise, part) flatMap {
      case Failure(error) => Future(Failure(error))
      case Success(res)   =>
        val dbSolution = instantiateSolution(user.username, exercise, part, solution, res.points, res.maxPoints)

        tables.futureSaveSolution(dbSolution) map { solutionSaved =>
          Success(onLiveCorrectionResult(solutionSaved, res))
        }
    }

  protected def readSolution(user: User, exercise: CompExType, part: PartType)(implicit request: Request[AnyContent]): Option[SolType] =
    request.body.asJson flatMap (_.asObj) flatMap {
      _.field(solutionName) flatMap (solution => readSolutionForPartFromJson(user, exercise, solution, part))
    }

  protected def readSolutionForPartFromJson(user: User, exercise: CompExType, jsValue: JsValue, part: PartType): Option[SolType]

  protected def correctEx(user: User, sol: SolType, exercise: CompExType, part: PartType): Future[Try[CompResult]]

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

  def renderExercise(user: User, exercise: CompExType, part: PartType, oldSolution: Option[DBSolType]): Html

  // Calls

  override def indexCall: Call = controllers.routes.MainExerciseController.index(this.urlPart)

  def exerciseRoutesForUser(user: User, exercise: CompExType): Future[Seq[CallForExPart]] = Future.sequence(exParts map {
    exPart: PartType =>
      // FIXME: check if user can solve this part!

      if (exercise.hasPart(exPart)) {
        tables.futureUserCanSolvePartOfExercise(user.username, exercise.ex.id, exPart) map {
          enabled => Some(CallForExPart(exPart, controllers.routes.ExerciseController.exercise(urlPart, exercise.ex.id, exPart.urlName), enabled))
        }


      } else Future(None)
  }).map(_.flatten)

}
