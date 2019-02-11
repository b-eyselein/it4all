package model.web

import better.files.File
import com.gargoylesoftware.htmlunit.ScriptException
import javax.inject._
import model._
import model.toolMains.{IdExerciseToolMain, ToolList, ToolState}
import model.web.WebConsts._
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import play.api.data.Forms._
import play.api.data._
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

  override protected val exParts: Seq[WebExPart] = WebExParts.values

  override protected val completeResultJsonProtocol: WebCompleteResultJsonProtocol.type = WebCompleteResultJsonProtocol

  // Forms

  override val compExForm: Form[WebCompleteEx] = WebCompleteExerciseForm.format

  override def exerciseReviewForm(username: String, completeExercise: WebCompleteEx, exercisePart: WebExPart): Form[WebExerciseReview] = Form(
    mapping(
      difficultyName -> Difficulties.formField,
      durationName -> optional(number(min = 0, max = 100))
    )
    (WebExerciseReview(username, completeExercise.ex.id, completeExercise.ex.semanticVersion, exercisePart, _, _))
    (wer => Some((wer.difficulty, wer.maybeDuration)))
  )

  // DB

  def writeWebSolutionFile(username: String, exerciseId: Int, part: WebExPart, content: String): Try[File] = Try {
    val target: File = solutionDirForExercise(username, exerciseId) / s"test.html"
    target.createFileIfNotExists(createParents = true).write(content)
  }

  override def futureOldOrDefaultSolution(user: User, exId: Int, exSemVer: SemanticVersion, part: WebExPart): Future[Option[DBSolType]] =
    super.futureOldOrDefaultSolution(user, exId, exSemVer, part) flatMap {
      case Some(solution) => Future.successful(Some(solution))
      case None           =>
        part match {
          case WebExParts.JsPart => super.futureOldOrDefaultSolution(user, exId, exSemVer, WebExParts.HtmlPart)
          case _                 => Future.successful(None)
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

  override def instantiateExercise(id: Int, author: String, state: ExerciseState): WebCompleteEx = {
    val semVer = SemanticVersionHelper.DEFAULT

    WebCompleteEx(
      WebExercise(id, semVer, title = "", author, text = "", state, htmlText = None, jsText = None),
      htmlTasks = Seq(
        HtmlCompleteTask(HtmlTask(1, id, semVer, "", "", None), attributes = Seq[Attribute]())
      ),
      jsTasks = Seq(
        JsCompleteTask(JsTask(1, id, semVer, "", "", JsActionType.FILLOUT, None), conditions = Seq[JsCondition]())
      ),
      sampleSolutions = Seq(
        WebSampleSolution(1, id, semVer, htmlSample = None, jsSample = None)
      )
    )
  }

  override def instantiateSolution(id: Int, username: String, exercise: WebCompleteEx, part: WebExPart, solution: String,
                                   points: Points, maxPoints: Points): WebSolution =
    WebSolution(id, username, exercise.ex.id, exercise.ex.semanticVersion, part, solution, points, maxPoints)

  // Yaml

  override val yamlFormat: MyYamlFormat[WebCompleteEx] = WebExYamlProtocol.WebExYamlFormat

  // Views

  override def renderAdminExerciseEditForm(user: User, newEx: WebCompleteEx, isCreation: Boolean, toolList: ToolList)
                                          (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html =
    views.html.idExercises.web.editWebExercise(user, WebCompleteExerciseForm.format.fill(newEx), isCreation, this, toolList)

  override def renderUserExerciseEditForm(user: User, newExForm: Form[WebCompleteEx], isCreation: Boolean)
                                         (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html =
    views.html.idExercises.web.editWebExerciseForm(user, newExForm, isCreation, this)

  override def renderExercisePreview(user: User, newExercise: WebCompleteEx, saved: Boolean): Html =
    views.html.idExercises.web.webPreview(newExercise)

  override def renderExercise(user: User, exercise: WebCompleteEx, part: WebExPart, maybeOldSolution: Option[WebSolution])
                             (implicit requestHeader: RequestHeader, messagesProvider: MessagesProvider): Html =
    views.html.idExercises.web.webExercise(user, exercise, part, maybeOldSolution map (_.solution) getOrElse WebConsts.STANDARD_HTML, this)

  override def renderEditRest(exercise: WebCompleteEx): Html =
    views.html.idExercises.web.editWebExRest(exercise)

  override def playground(user: User): Html =
    views.html.idExercises.web.webPlayground(user)

  // Correction

  private def onDriverGetError: Throwable => Try[WebCompleteResult] = {
    case syntaxError: WebDriverException =>
      Option(syntaxError.getCause) match {
        case Some(scriptException: ScriptException) => Failure(scriptException)
        case _                                      => ???
      }
    case otherError                      =>
      print(otherError)
      ???
  }

  private def onDriverGetSuccess(learnerSolution: String, exercise: WebCompleteEx, part: WebExPart, driver: HtmlUnitDriver): Try[WebCompleteResult] = Try {
    part match {
      case WebExParts.HtmlPart =>
        val elementResults = exercise.htmlTasks.map(WebCorrector.evaluateHtmlTask(_, driver))
        WebCompleteResult(learnerSolution, exercise, part, elementResults, Seq[JsWebResult]())
      case WebExParts.JsPart   =>
        val jsWebResults = exercise.jsTasks.map(WebCorrector.evaluateJsTask(_, driver))
        WebCompleteResult(learnerSolution, exercise, part, Seq[ElementResult](), jsWebResults)
    }
  }

  override def correctEx(user: User, learnerSolution: String, exercise: WebCompleteEx, part: WebExPart): Future[Try[WebCompleteResult]] = Future {
    writeWebSolutionFile(user.username, exercise.ex.id, part, learnerSolution) flatMap { _ =>

      val driver = new HtmlUnitDriver(true)
      Try {
        driver.get(getSolutionUrl(user, exercise.ex.id, part))
      }.transform(_ => onDriverGetSuccess(learnerSolution, exercise, part, driver), onDriverGetError)
    }
  }

  // Other helper methods

  def getSolutionUrl(user: User, exerciseId: Int, part: WebExPart): String = s"http://localhost:9080/${user.username}/$exerciseId/test.html"

}