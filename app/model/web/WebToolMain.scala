package model.web

import java.nio.file.Path

import controllers.exes.AExerciseToolMain
import javax.inject._
import model.core._
import model.web.WebConsts._
import model.yaml.MyYamlFormat
import model.{Consts, Enums, JsonFormat, User}
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import play.api.libs.json.JsValue
import play.api.mvc._
import play.twirl.api.Html
import views.html.web._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.Try

@Singleton
class WebToolMain @Inject()(val tables: WebTableDefs)(implicit ec: ExecutionContext) extends AExerciseToolMain("web") with JsonFormat {

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

  override val consts: Consts = WebConsts

  override val exParts: Seq[WebExPart] = WebExParts.values

  // DB

  override def futureSaveSolution(sol: WebSolution): Future[Boolean] = {
    val target: Path = solutionDirForExercise(sol.username, sol.exerciseId) / "test.html"
    val fileWritten = write(target, sol.solution).isSuccess

    tables.futureSaveSolution(sol) map (_ && fileWritten)
  }

  override def futureReadOldSolution(user: User, exerciseId: Int, part: WebExPart): Future[Option[WebSolution]] = tables.futureOldSolution(user.username, exerciseId)

  // Reading solution from request

  override def readSolutionFromPostRequest(user: User, id: Int, part: WebExPart)(implicit request: Request[AnyContent]): Option[WebSolution] =
    SolutionFormHelper.stringSolForm.bindFromRequest().fold(_ => None, _ => ??? /*Some(sol.learnerSolution)*/)

  override def readSolutionForPartFromJson(user: User, id: Int, jsValue: JsValue, part: WebExPart): Option[WebSolution] =
    jsValue.asStr map (sol => WebSolution(user.username, id, part, sol))

  // Other helper methods

  override def instantiateExercise(id: Int, state: Enums.ExerciseState): WebCompleteEx = WebCompleteEx(
    WebExercise(id, title = "", author = "", text = "", state, htmlText = None, hasHtmlPart = false, jsText = None, hasJsPart = false),
    htmlTasks = Seq.empty, jsTasks = Seq.empty
  )

  // Yaml

  override val yamlFormat: MyYamlFormat[WebCompleteEx] = WebExYamlProtocol.WebExYamlFormat

  // Views

  def renderExercise(user: User, exercise: WebCompleteEx, part: WebExPart): Future[Html] =
    futureReadOldSolution(user, exercise.ex.id, part) map (_ map (_.solution) getOrElse STANDARD_HTML) map {
      maybeOldSolution => webExercise.render(user, exercise, part, getTasks(exercise, part), maybeOldSolution)
    }

  //  override def renderExesListRest = new Html(a(cls := "btn btn-primary btn-block", href := routes.WebController.webPlayground().url)("Web-Playground").toString + "<hr>")

  override def renderEditRest(exercise: WebCompleteEx): Html = views.html.web.editWebExRest(exercise)

  // Correction

  override def correctEx(user: User, learnerSolution: WebSolution, exercise: WebCompleteEx): Future[Try[WebCompleteResult]] = {

    val futureSolutionSaved = futureSaveSolution(learnerSolution)

    val driver = new HtmlUnitDriver(true)
    driver.get(s"http://localhost:9080/${user.username}/${exercise.ex.id}/test.html")

    val results = Try(getTasks(exercise, learnerSolution.part) map (task => WebCorrector.evaluateWebTask(task, driver)))

    futureSolutionSaved map { solutionSaved =>
      results map (WebCompleteResult(learnerSolution.solution, exercise, learnerSolution.part, solutionSaved, _))
    }
  }

  // Handlers for results

  override def onSubmitCorrectionResult(user: User, result: WebCompleteResult): Html = views.html.core.correction(result, result.render, user, this)

  override def onSubmitCorrectionError(user: User, error: Throwable): Html = ???

  override def onLiveCorrectionResult(result: WebCompleteResult): JsValue = result.toJson

  // Other helper methods

  private def getTasks(exercise: WebCompleteEx, part: WebExPart): Seq[WebCompleteTask] = part match {
    case HtmlPart => exercise.htmlTasks
    case JsPart   => exercise.jsTasks
  }

}