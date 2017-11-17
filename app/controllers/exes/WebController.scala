package controllers.exes

import javax.inject._

import controllers.Secured
import controllers.core.AIdPartExController
import model.User
import model.core._
import model.web.WebConsts._
import model.web._
import net.jcazevedo.moultingyaml.YamlFormat
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import play.api.data.Form
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc._
import play.twirl.api.Html

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future, duration}
import scala.language.implicitConversions
import scala.util.Try

@Singleton
class WebController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)(implicit ec: ExecutionContext)
  extends AIdPartExController[WebExercise, WebResult](cc, dbcp, r, WebToolObject) with Secured {

  override type SolutionType = StringSolution

  override def solForm: Form[StringSolution] = Solution.stringSolForm

  // Yaml

  override type CompEx = WebCompleteEx

  override implicit val yamlFormat: YamlFormat[WebCompleteEx] = WebExYamlProtocol.WebExYamlFormat

  // db

  import profile.api._

  override type TQ = repo.WebExerciseTable

  override def tq: repo.ExerciseTableQuery[WebExercise, WebCompleteEx, repo.WebExerciseTable] = repo.webExercises

  override def completeExes: Future[Seq[WebCompleteEx]] = repo.webExercises.completeExes

  override def completeExById(id: Int): Future[Option[WebCompleteEx]] = repo.webExercises.completeById(id)

  override def saveRead(read: Seq[WebCompleteEx]): Future[Seq[Int]] = Future.sequence(read map (repo.webExercises.saveCompleteEx(_)))

  private def getOldSolOrDefault(userName: String, exerciseId: Int): Future[String] =
    db.run(repo.webSolutions.filter(_.userName === userName).filter(_.exerciseId === exerciseId).result.headOption) map {
      case Some(solution) => solution.solution
      case None           => STANDARD_HTML
    }

  // Other routes

  def exRest(exerciseId: Int): EssentialAction = futureWithAdmin { user =>
    implicit request =>
      completeExById(exerciseId) map {
        case Some(compEx) => Ok(views.html.web.webExRest.render(user, compEx))
        case None         => BadRequest("TODO")
      }
  }

  def playground: EssentialAction = withUser { user => implicit request => Ok(views.html.web.webPlayground.render(user)) }

  def site(username: String, exerciseId: Int): Action[AnyContent] = Action.async { implicit request =>
    getOldSolOrDefault(username, exerciseId).map(sol => Ok(new Html(sol)))
  }

  // Views

  override protected def renderExercise(user: User, exercise: WebCompleteEx, part: String): Future[Html] = {
    val tasks = part match {
      case HTML_TYPE => exercise.htmlTasks
      case JS_TYPE   => exercise.jsTasks
    }
    getOldSolOrDefault(user.username, exercise.ex.id) map (oldSol => views.html.web.webExercise.render(user, exercise, part, tasks, oldSol))
  }

  override def renderExesListRest = new Html(
    s"""<div class="panel panel-default">
       |  <a class="btn btn-primary btn-block" href="${controllers.exes.routes.WebController.playground()}">Web-Playground</a>
       |</div>
       |<hr>""".stripMargin)

  override def renderResult(correctionResult: CompleteResult[WebResult]): Html = views.html.web.webResult.render(correctionResult.results)

  // Correction

  override def correctEx(user: User, learnerSolution: StringSolution, exercise: WebCompleteEx, part: String): Try[CompleteResult[WebResult]] = {
    val solutionUrl = BASE_URL + controllers.exes.routes.WebController.site(user.username, exercise.ex.id).url
    val newSol = WebSolution(exercise.ex.id, user.username, learnerSolution.learnerSolution)

    Await.result(db.run(repo.webSolutions insertOrUpdate newSol), Duration(2, duration.SECONDS))
    val driver = new HtmlUnitDriver(true)
    driver.get(solutionUrl)

    val tasks = part match {
      case HTML_TYPE => exercise.htmlTasks
      case JS_TYPE   => exercise.jsTasks
    }

    Try(new CompleteResult(learnerSolution.learnerSolution, tasks map (task => WebCorrector.evaluate(task, driver))))
  }

}