package controllers.exes

import javax.inject._

import controllers.core.AIdPartExController
import controllers.exes.WebController._
import model.User
import model.core._
import model.core.result.CompleteResult
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

object WebController {

  val STANDARD_HTML: String =
    """<!doctype html>
      |<html>
      |<head>
      |  <!-- Hier: CSS und Javascript -->
      |</head>
      |<body>
      |  <!-- Html-Elemente -->
      |</body>
      |</html>""".stripMargin

  val STANDARD_HTML_PLAYGROUND: String =
    """<!doctype html>
      |<html>
      |<head>
      |  <style>
      |    /* Css-Anweisungen */
      |  </style>
      |  <script type="text/javascript">
      |    // Javascript-Code
      |  </script>
      |</head>
      |<body>
      |  <!-- Html-Elemente -->
      |</body>
      |</html>""".stripMargin

}

@Singleton
class WebController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)(implicit ec: ExecutionContext)
  extends AIdPartExController[WebExercise, WebResult](cc, dbcp, r, WebToolObject) with Secured {

  override type SolutionType = StringSolution

  override def solForm: Form[StringSolution] = Solution.stringSolForm

  // db

  // FIXME: Use some kind of that?
  type ToRead = (WebExercise, List[HtmlTask], List[JsTask])

  override type DbType = WebExercise

  override implicit val yamlFormat: YamlFormat[WebExercise] = WebExYamlProtocol.WebExYamlFormat

  override type TQ = repo.WebExerciseTable

  override def tq = repo.webExercises

  val HTML_TYPE = "html"
  val JS_TYPE   = "js"

  val BASE_URL = "http://localhost:9000"

  import profile.api._

  //noinspection TypeAnnotation
  def exAction(exId: Int) = repo.webExercises.findBy(_.id).apply(exId).result

  override def allExes: Future[Seq[WebExercise]] = db.run(repo.webExercises.result)

  override def exById(id: Int): Future[Option[WebExercise]] = allExes.map(_.find(_.id == id))

  def exAndHtmlTasks(exId: Int): Future[(WebExercise, Seq[HtmlTask])] = db.run(exAction(exId).head zip repo.htmlTasksForEx(exId).result)

  def exAndJsTasks(exId: Int): Future[(WebExercise, Seq[JsTask])] = db.run(exAction(exId).head zip repo.jsTasksForEx(exId).result)

  def getOldSolOrDefault(userName: String, exerciseId: Int): Future[String] =
    db.run(repo.webSolutions.filter(_.userName === userName).filter(_.exerciseId === exerciseId).result.headOption).map {
      case Some(solution) => solution.solution
      case None           => STANDARD_HTML
    }


  def exRest(exerciseId: Int): EssentialAction = withAdmin { user =>
    implicit request =>
      Ok(views.html.web.webExRest.render(user, null /*exById(exerciseId).get*/))
  }

  def playground: EssentialAction = withUser { user => implicit request => Ok(views.html.web.webPlayground.render(user)) }

  def site(username: String, exerciseId: Int): Action[AnyContent] = Action.async { implicit request =>
    getOldSolOrDefault(username, exerciseId).map(sol => Ok(new Html(sol)))
  }

  override def correctEx(user: User, learnerSolution: StringSolution, exercise: WebExercise, part: String): Try[CompleteResult[WebResult]] = {
    val solutionUrl = BASE_URL + controllers.exes.routes.WebController.site(user.username, exercise.id).url
    val newSol = WebSolution(exercise.id, user.username, learnerSolution.learnerSolution)

    Await.result(db.run(repo.webSolutions.insertOrUpdate(newSol)), Duration(2, duration.SECONDS))
    val driver = new HtmlUnitDriver(true)
    driver.get(solutionUrl)

    val tasks = Await.result(getTasks(exercise, part), Duration(2, duration.SECONDS))

    Try(new CompleteResult(learnerSolution.learnerSolution, tasks.map(task => WebCorrector.evaluate(task, driver)).toList))
  }

  override protected def renderExercise(user: User, exercise: WebExercise, part: String): Future[Html] = {
    val oldSolution = getOldSolOrDefault(user.username, exercise.id)
    oldSolution zip getTasks(exercise, part) map {
      case (oldSol, tasks) => views.html.web.webExercise.render(user, exercise, part, tasks, oldSol)
    }
  }


  override def renderExesListRest = new Html(
    s"""<div class="panel panel-default">
       |  <a class="btn btn-primary btn-block" href="${controllers.exes.routes.WebController.playground()}">Web-Playground</a>
       |</div>
       |<hr>""".stripMargin)

  override def renderResult(correctionResult: CompleteResult[WebResult]): Html = views.html.web.webResult.render(correctionResult.results)


  //noinspection TypeAnnotation
  private def getTasks(exercise: WebExercise, part: String): Future[Seq[WebTask]] = part match {
    case JS_TYPE   => db.run(repo.jsTasks.filter(_.id === exercise.id).result)
    case HTML_TYPE => db.run(repo.htmlTasks.filter(_.id === exercise.id).result)
  }

}