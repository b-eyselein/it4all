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


  val HTML_TYPE = "html"

  val JS_TYPE = "js"

  val BASE_URL = "http://localhost:9000"

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

  // Yaml

  override type CompEx = WebCompleteEx

  override implicit val yamlFormat: YamlFormat[WebCompleteEx] = WebExYamlProtocol.WebExYamlFormat

  // db

  override type TQ = repo.WebExerciseTable

  override def tq = repo.webExercises

  import profile.api._

  private def dbRes2WebCompleteEx(dbRes: Seq[(WebExercise, Option[HtmlTask], Option[JsTask])]): Seq[WebCompleteEx] =
    dbRes.groupBy(_._1)
      .mapValues(_.unzip { case (ex, htmlTasks, jsTasks) => (htmlTasks, jsTasks) })
      .map { case (ex, (htmlTasks, jsTasks)) => WebCompleteEx(ex, htmlTasks.flatten, jsTasks.flatten) }
      .toSeq

  private val completeExQuery = repo.webExercises
    .joinLeft(repo.htmlTasks).on { case (ex, htmlTask) => ex.id === htmlTask.exerciseId }
    .joinLeft(repo.jsTasks).on { case ((ex, htmlTask), jsTask) => ex.id === jsTask.exerciseId }
    .map { case ((ex, htmlTask), jsTask) => (ex, htmlTask, jsTask) }

  override def completeExes: Future[Seq[WebCompleteEx]] = db.run(completeExQuery.result) map dbRes2WebCompleteEx

  override def completeExById(id: Int): Future[Option[WebCompleteEx]] = completeExes map (_ find (_.ex.id == id))

  override def saveRead(read: Seq[WebCompleteEx]): Future[Seq[Int]] = Future.sequence(read.map(completeEx =>
    db.run(repo.webExercises insertOrUpdate completeEx.ex)
      andThen { case _ => completeEx.htmlTasks.map(ht => db.run(repo.htmlTasks insertOrUpdate ht)) }
      andThen { case _ => completeEx.jsTasks.map(jt => db.run(repo.jsTasks insertOrUpdate jt)) }
  ))

  private def getOldSolOrDefault(userName: String, exerciseId: Int): Future[String] =
    db.run(repo.webSolutions.filter(_.userName === userName).filter(_.exerciseId === exerciseId).result.headOption).map {
      case Some(solution) => solution.solution
      case None           => STANDARD_HTML
    }

  def exRest(exerciseId: Int): EssentialAction = futureWithAdmin { user =>
    implicit request =>
      completeExById(exerciseId) map {
        case Some(compEx) => Ok(views.html.web.webExRest.render(user, compEx.ex))
        case None         => BadRequest("TODO")
      }
  }

  def playground: EssentialAction = withUser { user => implicit request => Ok(views.html.web.webPlayground.render(user)) }

  def site(username: String, exerciseId: Int): Action[AnyContent] = Action.async { implicit request =>
    getOldSolOrDefault(username, exerciseId).map(sol => Ok(new Html(sol)))
  }

  override def correctEx(user: User, learnerSolution: StringSolution, exercise: WebCompleteEx, part: String): Try[CompleteResult[WebResult]] = {
    val solutionUrl = BASE_URL + controllers.exes.routes.WebController.site(user.username, exercise.ex.id).url
    val newSol = WebSolution(exercise.ex.id, user.username, learnerSolution.learnerSolution)

    Await.result(db.run(repo.webSolutions.insertOrUpdate(newSol)), Duration(2, duration.SECONDS))
    val driver = new HtmlUnitDriver(true)
    driver.get(solutionUrl)

    val tasks = part match {
      case HTML_TYPE => exercise.htmlTasks
      case JS_TYPE   => exercise.jsTasks
    }

    Try(new CompleteResult(learnerSolution.learnerSolution, tasks.map(task => WebCorrector.evaluate(task, driver)).toList))
  }

  override protected def renderExercise(user: User, exercise: WebCompleteEx, part: String): Future[Html] = {
    val oldSolution = getOldSolOrDefault(user.username, exercise.ex.id)
    val tasks = part match {
      case HTML_TYPE => exercise.htmlTasks
      case JS_TYPE   => exercise.jsTasks
    }
    oldSolution map (oldSol => views.html.web.webExercise.render(user, exercise, part, tasks, oldSol))
  }


  override def renderExesListRest = new Html(
    s"""<div class="panel panel-default">
       |  <a class="btn btn-primary btn-block" href="${controllers.exes.routes.WebController.playground()}">Web-Playground</a>
       |</div>
       |<hr>""".stripMargin)

  override def renderResult(correctionResult: CompleteResult[WebResult]): Html = views.html.web.webResult.render(correctionResult.results)

}