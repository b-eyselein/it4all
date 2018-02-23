package controllers.exes.idPartExes

import javax.inject._
import model.core._
import model.web.WebConsts._
import model.web.WebCorrector.evaluateWebTask
import model.web.{WebExPart, _}
import model.yaml.MyYamlFormat
import model.{Consts, JsonFormat, User}
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.JsValue
import play.api.mvc._
import play.twirl.api.Html
import views.html.web._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.Try


@Singleton
class WebController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, val tables: WebTableDefs)(implicit ec: ExecutionContext)
  extends JsonFormat with AIdPartExToolMain[WebExPart, WebSolution, WebExercise, WebCompleteEx] {

  override val urlPart          = "web"
  override val toolname: String = "web"
  override val exType  : String = "Web"
  override val consts  : Consts = WebConsts


  override def partTypeFromUrl(urlName: String): Option[WebExPart] = WebExParts.values.find(_.urlName == urlName)

  // Result types

  override type Tables = WebTableDefs

  override type R = WebResult

  override type CompResult = WebCompleteResult

  // Reading solution from requests

  //  override type SolType = WebSolution

  override def saveSolution(sol: WebSolution): Future[Boolean] = tables.saveSolution(sol)

  override def readOldSolution(user: User, exerciseId: Int, part: WebExPart): Future[Option[WebSolution]] = tables.getSolution(user.username, exerciseId)

  override def readSolutionFromPostRequest(user: User, id: Int)(implicit request: Request[AnyContent]): Option[WebSolution] =
    Solution.stringSolForm.bindFromRequest().fold(_ => None, _ => ??? /*Some(sol.learnerSolution)*/)

  override def readSolutionForPartFromJson(user: User, id: Int, jsValue: JsValue, part: WebExPart): Option[WebSolution] =
    jsValue.asStr map (sol => WebSolution(id, user.username, part, sol))

  // Yaml

  override val yamlFormat: MyYamlFormat[WebCompleteEx] = WebExYamlProtocol.WebExYamlFormat


  // Views

  override def renderExercise(user: User, exercise: WebCompleteEx, maybePart: Option[WebExPart]): Future[Html] = maybePart match {
    case Some(part) =>
      val oldSolution = readOldSolution(user, exercise.ex.id, part) map (_ map (_.solution) getOrElse STANDARD_HTML)

      oldSolution map (oldSol => webExercise.render(user, exercise, part, getTasks(exercise, part), oldSol))
    case None       => ???
  }

  //  override def renderExesListRest = new Html(a(cls := "btn btn-primary btn-block", href := routes.WebController.webPlayground().url)("Web-Playground").toString + "<hr>")

  override def renderEditRest(exercise: Option[WebCompleteEx]): Html = editWebExRest.render(exercise)

  // Correction

  override def correctEx(user: User, learnerSolution: WebSolution, exercise: WebCompleteEx): Future[Try[WebCompleteResult]] =
    saveSolution(learnerSolution) map { solutionSaved =>
      val solutionUrl = BASE_URL + routes.AIdPartExController.webSolution(user.username, exercise.ex.id, learnerSolution.part.urlName).url

      val driver = new HtmlUnitDriver(true)
      driver get solutionUrl

      val results = Try(getTasks(exercise, learnerSolution.part) map (task => evaluateWebTask(task, driver)))

      results map (WebCompleteResult(learnerSolution.solution, exercise, learnerSolution.part, solutionSaved, _))
    }

  // Handlers for results

  override def onSubmitCorrectionResult(user: User, result: WebCompleteResult): Html =
    views.html.core.correction.render(result, result.render, user, this)

  override def onSubmitCorrectionError(user: User, error: Throwable): Html = ???

  override def onLiveCorrectionResult(result: WebCompleteResult): JsValue = result.toJson

  // Other helper methods

  private def getTasks(exercise: WebCompleteEx, part: WebExPart): Seq[WebCompleteTask] = part match {
    case HtmlPart => exercise.htmlTasks
    case JsPart   => exercise.jsTasks
  }

}