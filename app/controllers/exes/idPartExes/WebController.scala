package controllers.exes.idPartExes

import javax.inject._

import controllers.Secured
import model.User
import model.core._
import model.web.WebConsts._
import model.web.WebCorrector.evaluateWebTask
import model.web.WebEnums.WebExPart
import model.web._
import net.jcazevedo.moultingyaml.YamlFormat
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc._
import play.twirl.api.Html
import views.html.web._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future, duration}
import scala.language.implicitConversions
import scala.util.Try

@Singleton
class WebController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, t: WebTableDefs)(implicit ec: ExecutionContext)
  extends AIdPartExController[WebExercise, WebCompleteEx, WebResult, CompleteResult[WebResult], WebTableDefs](cc, dbcp, t, WebToolObject) with Secured {

  override type PartType = WebExPart

  override def partTypeFromString(str: String): Option[WebExPart] = WebExPart.byShortName(str)

  case class WebExIdentifier(id: Int, part: WebExPart) extends IdPartExIdentifier

  override type ExIdentifier = WebExIdentifier

  override def identifier(id: Int, part: String): WebExIdentifier = WebExIdentifier(id, partTypeFromString(part) getOrElse WebExPart.HTML_PART)


  override type SolType = String

  override def readSolutionFromPostRequest(implicit request: Request[AnyContent]): Option[String] =
    Solution.stringSolForm.bindFromRequest().fold(_ => None, sol => Some(sol.learnerSolution))

  override def readSolutionFromPutRequest(implicit request: Request[AnyContent]): Option[String] =
    Solution.stringSolForm.bindFromRequest().fold(_ => None, sol => Some(sol.learnerSolution))

  // Yaml

  override implicit val yamlFormat: YamlFormat[WebCompleteEx] = WebExYamlProtocol.WebExYamlFormat

  // db

  import profile.api._

  override def saveRead(read: Seq[WebCompleteEx]): Future[Seq[Boolean]] = Future.sequence(read map tables.saveCompleteEx)

  // Other routes

  def exRest(exerciseId: Int): EssentialAction = futureWithAdmin { user =>
    implicit request =>
      futureCompleteExById(exerciseId) map {
        case Some(compEx) => Ok(webExRest(user, compEx))
        case None         => BadRequest("TODO")
      }
  }

  def playground: EssentialAction = withUser { user => implicit request => Ok(webPlayground(user)) }

  def site(username: String, exerciseId: Int): Action[AnyContent] = Action.async { implicit request =>
    getOldSolOrDefault(username, exerciseId) map (sol => Ok(new Html(sol)))
  }

  // Views

  override protected def renderExercise(user: User, exercise: WebCompleteEx, part: WebExPart): Future[Html] =
    getOldSolOrDefault(user.username, exercise.ex.id) map (oldSol => webExercise(user, exercise, part, getTasks(exercise, part), oldSol))


  override def renderExesListRest = new Html(
    s"""<a class="btn btn-primary btn-block" href="${routes.WebController.playground()}">Web-Playground</a>
       |<hr>""".stripMargin)

  override def renderResult(correctionResult: CompleteResult[WebResult]): Html = Html(correctionResult.results.map(res =>
    s"""|<div class="alert alert-${res.getBSClass}">
        |  <p data-toggle="collapse" href="#task${res.task.task.id}">${res.task.task.id}. ${res.task.task.text}</p>
        |  <div id="task${res.task.task.id}" class="collapse ${if (res.isSuccessful) "" else "in"}">
        |    <hr>
        |    ${res.render}
        |  </div>
        |</div>""".stripMargin) mkString "\n")

  override protected def renderEditRest(exercise: Option[WebCompleteEx]): Html = editWebExRest(exercise)

  // Correction

  override def correctEx(user: User, learnerSolution: String, exercise: WebCompleteEx, identifier: WebExIdentifier): Try[CompleteResult[WebResult]] = Try {
    val solutionUrl = BASE_URL + routes.WebController.site(user.username, exercise.ex.id).url

    val newSol = WebSolution(exercise.ex.id, user.username, learnerSolution)

    Await.result(db.run(tables.webSolutions insertOrUpdate newSol), Duration(2, duration.SECONDS))
    val driver = new HtmlUnitDriver(true)
    driver get solutionUrl

    new GenericCompleteResult[WebResult](learnerSolution, getTasks(exercise, identifier.part) map (task => evaluateWebTask(task, driver)))
  }

  // Other helper methods

  private def getOldSolOrDefault(username: String, exerciseId: Int): Future[String] =
    db.run(tables.webSolutions.filter(sol => sol.userName === username && sol.exerciseId === exerciseId).result.headOption) map {
      case Some(solution) => solution.solution
      case None           => STANDARD_HTML
    }

  private def getTasks(exercise: WebCompleteEx, part: WebExPart): Seq[WebCompleteTask] = part match {
    case WebExPart.HTML_PART => exercise.htmlTasks
    case WebExPart.JS_PART   => exercise.jsTasks
  }

}