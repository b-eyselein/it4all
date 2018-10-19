package model.toolMains

import java.nio.file.{Files, Path}

import model._
import model.core._
import model.core.result.CompleteResult
import model.persistence.SingleExerciseTableDefs
import play.api.Logger
import play.api.i18n.MessagesProvider
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContent, Call, Request, RequestHeader}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

abstract class IdExerciseToolMain(tn: String, up: String)(implicit ec: ExecutionContext)
  extends ASingleExerciseToolMain(tn, up) {

  // Abstract types

  type CompResult <: CompleteResult[R]

  type SolType

  type DBSolType <: DBPartSolution[PartType, SolType]

  override type Tables <: SingleExerciseTableDefs[ExType, CompExType, SolType, DBSolType, PartType, ReviewType]

  // Methods

  def checkAndCreateSolDir(username: String, exercise: CompExType): Try[Path] =
    Try(Files.createDirectories(solutionDirForExercise(username, exercise.ex.id)))

  def futureSaveSolution(sol: DBSolType): Future[Boolean] = tables.futureSaveSolution(sol)

  def futureOldOrDefaultSolution(user: User, exId: Int, exSemVer: SemanticVersion, part: PartType): Future[Option[DBSolType]] =
    tables.futureOldSolution(user.username, exId, exSemVer, part)

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
  def correctAbstract(user: User, exercise: CompExType, part: PartType)(implicit request: Request[AnyContent], ec: ExecutionContext): Future[Try[JsValue]] =
    readSolution(user, exercise, part) match {
      case Failure(exception)    => Future(Failure(exception))
      case Success(userSolution) => correctEx(user, userSolution, exercise, part) flatMap {
        case Failure(error) => Future(Failure(error))
        case Success(res)   =>
          val dbSolution = instantiateSolution(id = 0, user.username, exercise, part, userSolution, res.points, res.maxPoints)

          tables.futureSaveSolution(dbSolution) map { solutionSaved =>
            Success(onLiveCorrectionResult(solutionSaved, res))
          }
      }
    }

  def futureSampleSolutionForExerciseAndPart(id: Int, part: PartType): Future[Option[String]]

  protected def instantiateSolution(id: Int, username: String, exercise: CompExType, part: PartType, solution: SolType, points: Points, maxPoints: Points): DBSolType

  protected def readSolution(user: User, exercise: CompExType, part: PartType)(implicit request: Request[AnyContent]): Try[SolType]

  protected def correctEx(user: User, sol: SolType, exercise: CompExType, part: PartType): Future[Try[CompResult]]

  // Views

  override def exercisesOverviewForIndex: Html = Html(
    s"""<div class="form-group">
       |  <a class="btn btn-primary btn-block" href="${controllers.routes.ExerciseController.exerciseList(up)}">Zu den Übungsaufgaben</a>
       |</div>
       |""".stripMargin + {
      if (usersCanCreateExes) {
        s"""<div class="form-group">
           |  <a class="btn btn-success btn-block" href="${controllers.routes.ExerciseController.newExerciseForm(up)}">Neue Aufgabe erstellen</a>
           |</div>""".stripMargin
      } else ""
    })

  override def adminIndexView(admin: User, toolList: ToolList): Future[Html] = statistics map { stats =>
    views.html.admin.idExes.idExerciseAdminIndex(admin, stats, this, toolList)
  }

  override def previewReadAndSaveResult(user: User, read: ReadAndSaveResult[CompExType], toolList: ToolList): Html =
    views.html.admin.idExes.idExercisePreview(user, read, this, toolList)

  override def adminExerciseList(admin: User, exes: Seq[CompExType], toolList: ToolList): Html =
    views.html.admin.idExes.idExerciseAdminListView(admin, exes, this, toolList)

  def renderExercise(user: User, exercise: CompExType, part: PartType, oldSolution: Option[DBSolType])
                    (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html

  def renderExerciseReviewForm(user: User, exercise: CompExType, part: PartType)(
    implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html =
    views.html.idExercises.evaluateExerciseForm(user, exercise, part, this)

  // Calls

  override def indexCall: Call = controllers.routes.MainExerciseController.index(this.up)

  def exerciseRoutesForUser(user: User, exercise: CompExType): Future[Seq[CallForExPart]] = Future.sequence(exParts map {
    exPart: PartType =>
      // FIXME: check if user can solve this part!

      if (exercise.hasPart(exPart)) {
        tables.futureUserCanSolvePartOfExercise(user.username, exercise.ex.id, exercise.ex.semanticVersion, exPart) map {
          enabled => Some(CallForExPart(exPart, controllers.routes.ExerciseController.exercise(up, exercise.ex.id, exPart.urlName), enabled))
        }


      } else Future(None)
  }).map(_.flatten)

}
