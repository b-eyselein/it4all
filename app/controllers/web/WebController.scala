package controllers.web

import javax.inject._

import controllers.core.excontrollers.{AExerciseAdminController, IdPartExController}
import model.User
import model.core._
import model.core.result.CompleteResult
import model.web._
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import play.api.data.Form
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.Reads
import play.api.mvc._
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

@Singleton
class WebAdmin @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)(implicit ec: ExecutionContext)
  extends AExerciseAdminController[WebExercise](cc, dbcp, r, WebToolObject) with Secured {

  override def reads: Reads[WebExercise] = WebExerciseReads.webExReads

  override type TQ = repo.WebExerciseTable

  override def tq = repo.webExercises

  override def statistics: Future[Html] = Future(new Html(
    s""", davon
       |  <ul>
       |    <li>${0 /*WebExerciseHelper.withHtmlPart*/} Aufgaben mit HTML-Teil</li>
       |    <li>${0 /*WebExerciseHelper.withJsPart*/} Aufgaben mit JS-Teil</li>
       |  </ul>""".stripMargin))

  def exRest(exerciseId: Int): EssentialAction = withAdmin { user => implicit request => Ok(views.html.web.webExRest.render(user, null /*exById(exerciseId).get*/)) }

}

@Singleton
class WebController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, r: Repository)(implicit ec: ExecutionContext)
  extends IdPartExController[WebExercise, WebResult](cc, dbcp, r, WebToolObject) with Secured {

  override type SolType = StringSolution

  override def solForm: Form[StringSolution] = ???


  override type TQ = repo.WebExerciseTable

  override def tq = repo.webExercises


  val HTML_TYPE = "html"
  val JS_TYPE   = "js"

  val BASE_URL = "http://localhost:9000"

  val ALLOWED_TYPES = List(HTML_TYPE, JS_TYPE)


  protected def correctEx(learnerSolution: String, exercise: WebExercise, user: User, exType: String): Try[CompleteResult[WebResult]] = {
    //    saveSolution(learnerSolution, new WebSolutionKey(user.name, exercise.id))

    val solutionUrl = BASE_URL + routes.SolutionController.site(user.username, exercise.id).url

    val driver = new HtmlUnitDriver(true)
    driver.get(solutionUrl)

    val tasksTry: Try[List[_ <: WebTask]] = exType match {
      case JS_TYPE   => Success(exercise.jsTasks)
      case HTML_TYPE => Success(exercise.htmlTasks)
      case _         => Failure(null)
    }

    tasksTry.map(tasks => new CompleteResult(learnerSolution, tasks.map(WebCorrector.evaluate(_, driver)).toList))
  }

  //  def saveSolution(learnerSolution: String, key: WebSolutionKey): Unit = {
  //    val solution = Option(exById(key)).getOrElse(new WebSolution(key))
  //
  //    solution.sol = learnerSolution
  //    solution.save()
  //  }

  //  override def exercise(id: Int, part: String): EssentialAction = withUser { user =>
  //    implicit request =>
  //      part match {
  //        case (JS_TYPE | HTML_TYPE) =>
  //          exById(id).map {
  //            case None           => BadRequest("FEHLER!")
  //            case Some(exercise) =>
  //              log(user, ExerciseStartEvent(request, id))
  //              Ok(views.html.web.webExercise.render(user, exercise, part,
  //                WebController.getOldSolOrDefault(user.username, id), "Html-Korrektur"))
  //          }
  //          Ok("TODO")
  //        case _                     =>
  //          Redirect(routes.WebController.index(0))
  //      }
  //  }

  def playground: EssentialAction = withUser { user => implicit request => Ok(views.html.web.webPlayground.render(user)) }

  override def correctEx(solType: StringSolution, exercise: Option[WebExercise], user: User): Try[CompleteResult[WebResult]] = Failure(new Throwable("Not used..."))

  override def renderExercise(user: User, exercise: WebExercise): Html = ??? // FIXME

  override def renderExesListRest = new Html(
    s"""<div class="panel panel-default">
       |  <a class="btn btn-primary btn-block" href="${controllers.web.routes.WebController.playground()}">Web-Playground</a>
       |</div>
       |<hr>""".stripMargin)

  override def renderResult(correctionResult: CompleteResult[WebResult]): Html = views.html.web.webResult.render(correctionResult.results)

}

class SolutionController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def site(username: String, exerciseId: Int) = Action { implicit request => Ok(new Html(WebController.getOldSolOrDefault(username, exerciseId))) }

}

object WebController {

  def getOldSolOrDefault(userName: String, exerciseId: Int): String =
    "" //    Try(WebSolution.exById(new WebSolutionKey(userName, exerciseId)).sol).getOrElse(STANDARD_HTML)

  val STANDARD_HTML: String =
    """<!doctype html>
      |<html>
      |<head>
      |
      |</head>
      |<body>
      |
      |</body>
      |</html>""".stripMargin

  val STANDARD_HTML_PLAYGROUND: String =
    """<!doctype html>
      |<html>
      |<head>
      |  <style>
      |    /* Css-Anweisungen */
      |  </style>
      |  <script type=\"text/javascript\">
      |    // Javascript-Code
      |  </script>
      |</head>
      |<body>
      |  <!-- Html-Elemente -->
      |</body>
      |</html>""".stripMargin
}