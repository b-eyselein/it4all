package model.web

import java.nio.file.Path

import javax.inject._
import model._
import model.toolMains.{IdExerciseToolMain, ToolList, ToolState}
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import play.api.data.Forms._
import play.api.data._
import model.web.WebConsts._
import play.api.i18n.MessagesProvider
import play.api.libs.json.JsString
import play.api.mvc.{AnyContent, Request, RequestHeader}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}

@Singleton
class WebToolMain @Inject()(val tables: WebTableDefs)(implicit ec: ExecutionContext) extends IdExerciseToolMain("Web", "web") {

  // Result types

  override type ExType = WebExercise

  override type CompExType = WebCompleteEx

  override type Tables = WebTableDefs

  override type PartType = WebExPart

  override type SolType = String

  override type DBSolType = WebSolution

  override type R = WebResult

  override type CompResult = WebCompleteResult

  override type ReviewType = WebExerciseReview

  // Other members

  override val hasPlayground: Boolean = true

  override val toolState: ToolState = ToolState.LIVE

  override val consts: Consts = WebConsts

  override val exParts: Seq[WebExPart] = WebExParts.values

  // Forms

  override val compExForm: Form[WebCompleteEx] = WebCompleteExerciseForm.format

  override def exerciseReviewForm(username: String, completeExercise: WebCompleteEx, exercisePart: WebExPart): Form[WebExerciseReview] = {

    val apply = (diffStr: String, maybeDuration: Option[Int]) =>
      WebExerciseReview(username, completeExercise.ex.id, completeExercise.ex.semanticVersion, exercisePart, Difficulties.withNameInsensitive(diffStr), maybeDuration)

    val unapply = (cr: WebExerciseReview) => Some((cr.difficulty.entryName, cr.maybeDuration))

    Form(
      mapping(
        difficultyName -> nonEmptyText,
        durationName -> optional(number(min = 0, max = 100))
      )(apply)(unapply)
    )

  }

  // DB

  def writeWebSolutionFile(username: String, exerciseId: Int, part: WebExPart, content: String): Try[Path] = {
    val fileEnding = part match {
      case WebExParts.PHPPart => "php"
      case _                  => "html"
    }

    val target: Path = solutionDirForExercise(username, exerciseId) / ("test." + fileEnding)
    write(target, content)
  }

  override def futureOldOrDefaultSolution(user: User, exerciseId: Int, part: WebExPart): Future[Option[DBSolType]] =
    super.futureOldOrDefaultSolution(user, exerciseId, part) flatMap {
      case Some(solution) => Future(Some(solution))
      case None           =>
        part match {
          case WebExParts.JsPart => super.futureOldOrDefaultSolution(user, exerciseId, WebExParts.HtmlPart)
          case _                 => Future(None)
        }
    }

  // Reading solution from request

  override protected def readSolution(user: User, exercise: WebCompleteEx, part: WebExPart)(implicit request: Request[AnyContent]): Try[String] =
    request.body.asJson match {
      case None          => Failure(new Exception("Request body does not contain json!"))
      case Some(jsValue) => jsValue match {
        case JsString(solution) => Success(solution)
        case other              => Failure(new Exception("Wrong json content: " + other.toString))
      }
    }

  // Other helper methods

  override def instantiateExercise(id: Int, author: String, state: ExerciseState): WebCompleteEx = WebCompleteEx(
    WebExercise(id, SemanticVersion(0, 1, 0), title = "", author, text = "", state, sampleSolution = "", htmlText = None, jsText = None, phpText = None),
    htmlTasks = Seq[HtmlCompleteTask](), jsTasks = Seq[JsCompleteTask]()
  )

  override def instantiateSolution(username: String, exercise: WebCompleteEx, part: WebExPart, solution: String, points: Points, maxPoints: Points): WebSolution =
    WebSolution(username, exercise.ex.id, exercise.ex.semanticVersion, part, solution, points, maxPoints)

  // Yaml

  override val yamlFormat: MyYamlFormat[WebCompleteEx] = WebExYamlProtocol.WebExYamlFormat

  // Views

  override def renderAdminExerciseEditForm(user: User, newEx: WebCompleteEx, isCreation: Boolean, toolList: ToolList): Html =
    views.html.idExercises.web.editWebExercise(user, newEx, isCreation, this, toolList)

  override def renderUserExerciseEditForm(user: User, newExForm: Form[WebCompleteEx], isCreation: Boolean)
                                         (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html =
    views.html.idExercises.web.editWebExerciseForm(user, newExForm, isCreation, this)

  override def renderExercise(user: User, exercise: WebCompleteEx, part: WebExPart, maybeOldSolution: Option[WebSolution])
                             (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html =
    views.html.idExercises.web.webExercise(user, exercise, part, maybeOldSolution map (_.solution) getOrElse WebConsts.STANDARD_HTML, this)

  override def renderEditRest(exercise: WebCompleteEx): Html =
    views.html.idExercises.web.editWebExRest(exercise)

  override def playground(user: User): Html =
    views.html.idExercises.web.webPlayground(user)

  // Correction

  override def correctEx(user: User, learnerSolution: String, exercise: WebCompleteEx, part: WebExPart): Future[Try[WebCompleteResult]] = Future {
    writeWebSolutionFile(user.username, exercise.ex.id, part, learnerSolution) flatMap { _ =>

      val driver = new HtmlUnitDriver(true)
      driver.get(getSolutionUrl(user, exercise.ex.id, part))

      val results = Try(exercise.tasksForPart(part) map (task => WebCorrector.evaluateWebTask(task, driver)))

      results map (WebCompleteResult(learnerSolution, exercise, part, _))
    }
  }

  override def futureSampleSolutionForExerciseAndPart(id: Int, part: WebExPart): Future[Option[String]] =
    tables.futureCompleteExById(id) map (maybeCompleteEx => maybeCompleteEx.map(_.ex.sampleSolution))


  // Other helper methods

  def getSolutionUrl(user: User, exerciseId: Int, part: WebExPart): String = part match {
    case WebExParts.PHPPart => s"http://localhost:9080/${user.username}/$exerciseId/test.php"
    case _                  => s"http://localhost:9080/${user.username}/$exerciseId/test.html"
  }

}