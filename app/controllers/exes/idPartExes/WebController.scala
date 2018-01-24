package controllers.exes.idPartExes

import javax.inject._

import controllers.Secured
import model.core._
import model.web.WebConsts._
import model.web.WebCorrector.evaluateWebTask
import model.web.WebExParts.WebExPart
import model.web._
import model.{JsonFormat, User}
import net.jcazevedo.moultingyaml.YamlFormat
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{JsObject, JsValue}
import play.api.mvc._
import play.twirl.api.Html
import views.html.web._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future, duration}
import scala.language.implicitConversions
import scala.util.Try

case class WebSolutionType(part: WebExPart, solution: String)

@Singleton
class WebController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, t: WebTableDefs)(implicit ec: ExecutionContext)
  extends AIdPartExController[WebExercise, WebCompleteEx, WebResult, WebCompleteResult, WebTableDefs](cc, dbcp, t, WebToolObject)
    with Secured with JsonFormat {

  override type PartType = WebExPart

  override def partTypeFromUrl(urlName: String): Option[WebExPart] = WebExParts.values.find(_.urlName == urlName)

  case class WebExIdentifier(id: Int, part: WebExPart) extends IdPartExIdentifier

  // Reading solution from requests

  override type SolType = WebSolutionType

  override def readSolutionFromPostRequest(user: User, id: Int)(implicit request: Request[AnyContent]): Option[WebSolutionType] =
    Solution.stringSolForm.bindFromRequest().fold(_ => None, sol => ??? /*Some(sol.learnerSolution)*/)

  override protected def readSolutionForPartFromJson(user: User, id: Int, jsValue: JsValue, part: WebExPart): Option[WebSolutionType] =
    jsValue.asStr map (sol => WebSolutionType(part, sol))

  // Yaml

  override implicit val yamlFormat: YamlFormat[WebCompleteEx] = WebExYamlProtocol.WebExYamlFormat

  // db

  import profile.api._

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

  override def renderExesListRest = new Html(
    s"""<a class="btn btn-primary btn-block" href="${routes.WebController.playground()}">Web-Playground</a>
       |<hr>""".stripMargin)

  override protected def renderEditRest(exercise: Option[WebCompleteEx]): Html = editWebExRest.render(exercise)

  // Correction

  override def correctEx(user: User, learnerSolution: WebSolutionType, exercise: WebCompleteEx): Future[Try[WebCompleteResult]] =
    tables.saveSolution(WebSolution(exercise.ex.id, user.username, learnerSolution.solution)) map { solutionSaved =>
      Try {
        val solutionUrl = BASE_URL + routes.WebController.site(user.username, exercise.ex.id).url

        val newSol = WebSolution(exercise.ex.id, user.username, learnerSolution.solution)

        Await.result(db.run(tables.webSolutions insertOrUpdate newSol), Duration(2, duration.SECONDS))
        val driver = new HtmlUnitDriver(true)
        driver get solutionUrl

        WebCompleteResult(learnerSolution.solution, solutionSaved, getTasks(exercise, learnerSolution.part) map (task => evaluateWebTask(task, driver)))
      }
    }

  // Handlers for results

  protected def onSubmitCorrectionResult(user: User, result: WebCompleteResult): Result =
    Ok(views.html.core.correction.render(result, result.render, user, toolObject))

  protected def onSubmitCorrectionError(user: User, error: Throwable): Result = ???

  protected def onLiveCorrectionResult(result: WebCompleteResult): Result = Ok(result.render)

  protected def onLiveCorrectionError(error: Throwable): Result = ???

  //    Ok(Html(s"""<div class="alert alert-danger">Es gab einen Fehler bei der Korrektur ihrer L&ouml;sung: ${error.getMessage}</div>"""))

  // Other helper methods

  private def getOldSolOrDefault(username: String, exerciseId: Int): Future[String] =
    db.run(tables.webSolutions.filter(sol => sol.userName === username && sol.exerciseId === exerciseId).result.headOption) map {
      case Some(solution) => solution.solution
      case None           => STANDARD_HTML
    }

  private def getTasks(exercise: WebCompleteEx, part: WebExPart): Seq[WebCompleteTask] = part match {
    case WebExParts.HtmlPart => exercise.htmlTasks
    case WebExParts.JsPart   => exercise.jsTasks
  }

}