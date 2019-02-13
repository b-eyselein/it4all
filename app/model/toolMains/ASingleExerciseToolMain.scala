package model.toolMains

import better.files.File
import model.ExerciseState.APPROVED
import model.core.{NoSuchExerciseException, ReadAndSaveResult, SolutionTransferException}
import model.persistence.SingleExerciseTableDefs
import model.{DBPartSolution, ExPart, ExerciseReview, ExerciseState, Points, SemanticVersion, SingleCompleteEx, User}
import net.jcazevedo.moultingyaml.Auto
import play.api.Logger
import play.api.data.Form
import play.api.i18n.MessagesProvider
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContent, Call, Request, RequestHeader}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

abstract class ASingleExerciseToolMain(aToolName: String, aUrlPart: String)(implicit ec: ExecutionContext) extends FixedExToolMain(aToolName, aUrlPart) {

  // Abstract Types


  type SolType

  type DBSolType <: DBPartSolution[PartType, SolType]

  type PartType <: ExPart

  type ReviewType <: ExerciseReview[PartType]

  override type CompExType <: SingleCompleteEx[ExType, PartType]

  //  override type Tables <: IdExerciseTableDefs[ExType, CompExType, PartType, ReviewType]
  override type Tables <: SingleExerciseTableDefs[CompExType, SolType, DBSolType, PartType, ReviewType]

  override type ReadType = CompExType

  // Other members

  protected val exParts: Seq[PartType]

  // Forms

  def exerciseReviewForm(username: String, completeExercise: CompExType, exercisePart: PartType): Form[ReviewType]

  def compExForm: Form[CompExType]

  // DB

  def futureSaveReview(review: ReviewType): Future[Boolean] = tables.futureSaveReview(review)

  def futureCompleteExById(id: Int): Future[Option[CompExType]] = tables.futureCompleteExById(id)

  def futureCompleteExByIdAndVersion(id: Int, semVer: SemanticVersion): Future[Option[CompExType]] =
    tables.futureCompleteExByIdAndVersion(id, semVer)

  override def futureSaveRead(exercises: Seq[ReadType]): Future[Seq[(ReadType, Boolean)]] = Future.sequence(exercises map {
    ex => tables.futureInsertCompleteEx(ex) map (saveRes => (ex, saveRes))
  })

  def futureHighestId: Future[Int] = tables.futureHighestExerciseId

  def updateExerciseState(id: Int, newState: ExerciseState): Future[Boolean] = tables.updateExerciseState(id, newState)

  def futureDeleteExercise(id: Int): Future[Int] = tables.deleteExercise(id)

  def futureAllReviews: Future[Seq[ReviewType]] = tables.futureAllReviews

  def futureReviewsForExercise(id: Int): Future[Seq[ReviewType]] = tables.futureReviewsForExercise(id)

  // Helper methods

  def partTypeFromUrl(urlName: String): Option[PartType] = exParts.find(_.urlName == urlName)

  private def futureCompleteExesForPage(page: Int): Future[Seq[CompExType]] = tables.futureCompleteExes map {
    allExes: Seq[CompExType] =>
      val STEP = 10

      val distinctExes: Seq[CompExType] = allExes.filter(_.ex.state == APPROVED)

      val sliceStart = Math.max(0, (page - 1) * STEP)
      val sliceEnd = Math.min(page * STEP, distinctExes.size)

      distinctExes.slice(sliceStart, sliceEnd)
  }

  private def getRoutesForEx(user: User, exes: Seq[CompExType]): Future[Seq[ExAndRoute]] = Future.sequence {
    exes.groupBy(_.ex.id).map {
      case (id: Int, allVersionsOfEx: Seq[CompExType]) =>
        exerciseRoutesForUser(user, allVersionsOfEx.head).map {
          routes: Seq[CallForExPart] =>
            val versions = allVersionsOfEx.map(_.ex.semanticVersion)
            val exTitle = allVersionsOfEx.head.ex.title

            ExAndRoute(id, exTitle, versions, routes)
        }
    }.toSeq
  }

  def dataForUserExesOverview(user: User, page: Int): Future[UserExOverviewContent] = for {
    numOfExes <- tables.futureNumOfExes
    exes <- futureCompleteExesForPage(page)
    exesAndRoutes <- getRoutesForEx(user, exes)
  } yield UserExOverviewContent(numOfExes, exesAndRoutes)

  // Helper methods for admin

  def instantiateExercise(id: Int, author: String, state: ExerciseState): CompExType

  // TODO: scalarStyle = Folded if fixed...
  override def yamlString: Future[String] = futureCompleteExes map {
    exes => "%YAML 1.2\n---\n" + (exes map (yamlFormat.write(_).print(Auto /*, Folded*/)) mkString "---\n")
  }

  // Views

  def renderAdminExerciseEditForm(user: User, newEx: CompExType, isCreation: Boolean, toolList: ToolList)
                                 (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html =
    views.html.admin.exerciseEditForm(user, newEx, renderEditRest(newEx), isCreation = true, this, toolList)

  def renderUserExerciseEditForm(user: User, newExForm: Form[CompExType], isCreation: Boolean)
                                (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html

  def adminExerciseList(admin: User, exes: Seq[CompExType], toolList: ToolList): Html =
    views.html.admin.idExes.idExerciseAdminListView(admin, exes, this, toolList)

  // FIXME: former class IdExerciseToolMain

  // Methods

  def checkAndCreateSolDir(username: String, exercise: CompExType): Try[File] =
    Try(solutionDirForExercise(username, exercise.ex.id).createDirectories())

  def futureSaveSolution(sol: DBSolType): Future[Boolean] = tables.futureSaveSolution(sol)

  def futureOldOrDefaultSolution(user: User, exId: Int, exSemVer: SemanticVersion, part: PartType): Future[Option[DBSolType]] =
    tables.futureOldSolution(user.username, exId, exSemVer, part)

  def futureSolutionsForExercise(exerciseId: Int): Future[Seq[DBSolType]] = tables.futureOldSolutions(exerciseId)


  // Result handling

  def onLiveCorrectionResult(solutionSaved: Boolean, result: CompResult): JsValue =
    completeResultJsonProtocol.completeResultWrites(solutionSaved).writes(result)

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
      case Failure(exception)    => Future.successful(Failure(exception))
      case Success(userSolution) => correctEx(user, userSolution, exercise, part) flatMap {
        case Failure(error) => Future.successful(Failure(error))
        case Success(res)   =>
          val dbSolution = instantiateSolution(id = 0, user.username, exercise, part, userSolution, res.points, res.maxPoints)

          tables.futureSaveSolution(dbSolution) map { solutionSaved =>
            Success(onLiveCorrectionResult(solutionSaved, res))
          }
      }
    }

  def futureSampleSolutionsForExerciseAndPart(id: Int, part: PartType): Future[Seq[String]] = tables.futureSampleSolutionsForExercisePart(id, part)

  protected def instantiateSolution(id: Int, username: String, exercise: CompExType, part: PartType, solution: SolType, points: Points, maxPoints: Points): DBSolType

  protected def readSolution(user: User, exercise: CompExType, part: PartType)(implicit request: Request[AnyContent]): Try[SolType]

  protected def correctEx(user: User, sol: SolType, exercise: CompExType, part: PartType): Future[Try[CompResult]]

  // Views

  override def exercisesOverviewForIndex: Html = Html(
    s"""<div class="form-group">
       |  <a class="btn btn-primary btn-block" href="${controllers.exes.routes.ExerciseController.exerciseList(urlPart)}">Zu den Übungsaufgaben</a>
       |</div>
       |""".stripMargin + {
      if (usersCanCreateExes) {
        s"""<div class="form-group">
           |  <a class="btn btn-success btn-block" href="${controllers.exes.routes.ExerciseController.newExerciseForm(urlPart)}">Neue Aufgabe erstellen</a>
           |</div>""".stripMargin
      } else ""
    })

  override def adminIndexView(admin: User, toolList: ToolList): Future[Html] = statistics map { stats =>
    views.html.admin.idExes.idExerciseAdminIndex(admin, stats, this, toolList)
  }

  override def previewReadAndSaveResult(user: User, read: ReadAndSaveResult[CompExType], toolList: ToolList): Html =
    views.html.admin.idExes.idExercisePreview(user, read, this, toolList)

  def renderExercise(user: User, exercise: CompExType, part: PartType, oldSolution: Option[DBSolType])
                    (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html

  def renderExerciseReviewForm(user: User, exercise: CompExType, part: PartType)(
    implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html =
    views.html.idExercises.evaluateExerciseForm(user, exercise, part, this)

  // Calls

  override def indexCall: Call = controllers.routes.MainExerciseController.index(urlPart)

  def exerciseRoutesForUser(user: User, exercise: CompExType): Future[Seq[CallForExPart]] = Future.sequence(exParts map {
    exPart: PartType =>
      // FIXME: check if user can solve this part!

      if (exercise.hasPart(exPart)) {
        tables.futureUserCanSolvePartOfExercise(user.username, exercise.ex.id, exercise.ex.semanticVersion, exPart) map {
          enabled => Some(CallForExPart(exPart, controllers.exes.routes.ExerciseController.exercise(urlPart, exercise.ex.id, exPart.urlName), enabled))
        }


      } else Future(None)
  }).map(_.flatten)


}
