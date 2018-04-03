package model.web

import java.nio.file.Path

import javax.inject._
import model.Enums.ToolState
import model.toolMains.IdExerciseToolMain
import model.yaml.MyYamlFormat
import model.{Consts, Enums, JsonFormat, User}
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import play.api.data.Form
import play.api.libs.json.JsValue
import play.api.mvc._
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.Try

@Singleton
class WebToolMain @Inject()(val tables: WebTableDefs)(implicit ec: ExecutionContext) extends IdExerciseToolMain("web") with JsonFormat {

  // Result types

  override type ExType = WebExercise

  override type CompExType = WebCompleteEx

  override type Tables = WebTableDefs

  override type PartType = WebExPart

  override type SolType = WebSolution

  override type R = WebResult

  override type CompResult = WebCompleteResult

  // Other members

  override val toolname: String = "Web"

  override val toolState: ToolState = ToolState.LIVE

  override val consts: Consts = WebConsts

  override val exParts: Seq[WebExPart] = WebExParts.values

  // TODO: create Form mapping ...
  override implicit val compExForm: Form[WebExercise] = null

  // DB

  override def futureSaveSolution(sol: WebSolution): Future[Boolean] = {
    val fileEnding = sol.part match {
      case PHPPart => "php"
      case _       => "html"
    }

    val target: Path = solutionDirForExercise(sol.username, sol.exerciseId) / ("test." + fileEnding)
    val fileWritten = write(target, sol.solution).isSuccess

    tables.futureSaveSolution(sol) map (_ && fileWritten)
  }

  // Reading solution from request

  override def readSolutionFromPostRequest(user: User, id: Int, part: WebExPart)(implicit request: Request[AnyContent]): Option[WebSolution] = None

  override def readSolutionForPartFromJson(user: User, id: Int, jsValue: JsValue, part: WebExPart): Option[WebSolution] =
    jsValue.asStr map (sol => WebSolution(user.username, id, part, sol))

  // Other helper methods

  override def instantiateExercise(id: Int, state: Enums.ExerciseState): WebCompleteEx = WebCompleteEx(
    WebExercise(id, title = "", author = "", text = "", state, htmlText = None, jsText = None, phpText = None),
    htmlTasks = Seq.empty, jsTasks = Seq.empty
  )

  // Yaml

  override val yamlFormat: MyYamlFormat[WebCompleteEx] = WebExYamlProtocol.WebExYamlFormat

  // Views

  override def renderExerciseEditForm(user: User, newEx: WebCompleteEx, isCreation: Boolean): Html =
    views.html.web.editWebExercise(user, this, newEx, isCreation)

  def renderExercise(user: User, exercise: WebCompleteEx, part: WebExPart, maybeOldSolution: Option[WebSolution]): Html =
    views.html.web.webExercise(user, exercise, part, getTasks(exercise, part), maybeOldSolution map (_.solution) getOrElse WebConsts.STANDARD_HTML)

  override def renderEditRest(exercise: WebCompleteEx): Html = views.html.web.editWebExRest(exercise)

  // Correction

  override def correctEx(user: User, learnerSolution: WebSolution, exercise: WebCompleteEx, solutionSaved: Boolean): Future[Try[WebCompleteResult]] = Future {
    val driver = new HtmlUnitDriver(true)
    driver.get(getSolutionUrl(user, exercise.ex.id, learnerSolution.part))

    val results = Try(getTasks(exercise, learnerSolution.part) map (task => WebCorrector.evaluateWebTask(task, driver)))

    results map (WebCompleteResult(learnerSolution.solution, exercise, learnerSolution.part, solutionSaved, _))
  }

  // Handlers for results

  override def onSubmitCorrectionResult(user: User, result: WebCompleteResult): Html = views.html.core.correction(result, result.render, user, this)

  override def onSubmitCorrectionError(user: User, error: Throwable): Html = ???

  override def onLiveCorrectionResult(result: WebCompleteResult): JsValue = result.toJson

  // Other helper methods

  def getSolutionUrl(user: User, exerciseId: Int, part: WebExPart): String = part match {
    case PHPPart => s"http://localhost:9080/${user.username}/$exerciseId/test.php"
    case _       => s"http://localhost:9080/${user.username}/$exerciseId/test.html"
  }

  private def getTasks(exercise: WebCompleteEx, part: WebExPart): Seq[WebCompleteTask] = part match {
    case HtmlPart => exercise.htmlTasks
    case JsPart   => exercise.jsTasks
    case PHPPart  => exercise.phpTasks
  }

}