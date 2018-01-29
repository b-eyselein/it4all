package controllers.exes.idPartExes

import javax.inject._

import controllers.Secured
import model.core._
import model.web.WebConsts._
import model.web.WebCorrector.evaluateWebTask
import model.web.{WebExPart, _}
import model.{JsonFormat, User}
import net.jcazevedo.moultingyaml.YamlFormat
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.JsValue
import play.api.mvc._
import play.twirl.api.Html
import views.html.web._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.Try
import scalatags.Text.all._

case class WebSolutionType(part: WebExPart, solution: String)

@Singleton
class WebController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, t: WebTableDefs)(implicit ec: ExecutionContext)
  extends AIdPartExController[WebExercise, WebCompleteEx, WebExPart, WebResult, WebCompleteResult, WebTableDefs](cc, dbcp, t, WebToolObject)
    with Secured with JsonFormat {

  override def partTypeFromUrl(urlName: String): Option[WebExPart] = WebExParts.values.find(_.urlName == urlName)

  // Reading solution from requests

  override type SolType = WebSolutionType

  override def readSolutionFromPostRequest(user: User, id: Int)(implicit request: Request[AnyContent]): Option[WebSolutionType] =
    Solution.stringSolForm.bindFromRequest().fold(_ => None, sol => ??? /*Some(sol.learnerSolution)*/)

  override protected def readSolutionForPartFromJson(user: User, id: Int, jsValue: JsValue, part: WebExPart): Option[WebSolutionType] =
    jsValue.asStr map (sol => WebSolutionType(part, sol))

  // Yaml

  override implicit val yamlFormat: YamlFormat[WebCompleteEx] = WebExYamlProtocol.WebExYamlFormat

  // Other routes

  def exRest(exerciseId: Int): EssentialAction = futureWithAdmin { user =>
    implicit request =>
      futureCompleteExById(exerciseId) map {
        case Some(compEx) => Ok(webExRest.render(user, compEx))
        case None         => BadRequest("TODO")
      }
  }

  def playground: EssentialAction = withUser { user => implicit request => Ok(webPlayground.render(user)) }

  def site(username: String, exerciseId: Int): Action[AnyContent] = Action.async { implicit request =>
    getOldSolOrDefault(username, exerciseId) map (sol => Ok(new Html(sol)))
  }

  // Views

  override protected def renderExercise(user: User, exercise: WebCompleteEx, part: WebExPart): Future[Html] =
    getOldSolOrDefault(user.username, exercise.ex.id) map (oldSol => webExercise.render(user, exercise, part, getTasks(exercise, part), oldSol))

  override def renderExesListRest = new Html(a(cls := "btn btn-primary btn-block", href := routes.WebController.playground().url)("Web-Playground").toString + "<hr>")

  override protected def renderEditRest(exercise: Option[WebCompleteEx]): Html = editWebExRest.render(exercise)

  // Correction

  override def correctEx(user: User, learnerSolution: WebSolutionType, exercise: WebCompleteEx): Future[Try[WebCompleteResult]] =
    tables.saveSolution(WebSolution(exercise.ex.id, user.username, learnerSolution.solution)) map { solutionSaved =>
      val solutionUrl = BASE_URL + routes.WebController.site(user.username, exercise.ex.id).url

      val driver = new HtmlUnitDriver(true)
      driver get solutionUrl

      val results = Try(getTasks(exercise, learnerSolution.part) map (task => evaluateWebTask(task, driver)))

      results map (WebCompleteResult(learnerSolution.solution, solutionSaved, _))
    }

  // Handlers for results

  protected def onSubmitCorrectionResult(user: User, result: WebCompleteResult): Result =
    Ok(views.html.core.correction.render(result, result.render, user, toolObject))

  protected def onSubmitCorrectionError(user: User, msg: String, error: Option[Throwable]): Result = ???

  protected def onLiveCorrectionResult(result: WebCompleteResult): Result = Ok(result.render)

  protected def onLiveCorrectionError(msg: String, error: Option[Throwable]): Result = ???

  // Other helper methods

  private def getOldSolOrDefault(username: String, exerciseId: Int): Future[String] = tables.getSolution(username, exerciseId) map {
    case Some(solution) => solution.solution
    case None           => STANDARD_HTML
  }

  private def getTasks(exercise: WebCompleteEx, part: WebExPart): Seq[WebCompleteTask] = part match {
    case HtmlPart => exercise.htmlTasks
    case JsPart   => exercise.jsTasks
  }

}