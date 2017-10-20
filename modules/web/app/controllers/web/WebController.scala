package controllers.web

import javax.inject.Inject

import controllers.excontrollers.{AExerciseAdminController, IdPartExController}
import model._
import model.logging.ExerciseStartEvent
import model.result.CompleteResult
import model.task.WebTask
import model.user.User
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import play.api.data.Form
import play.api.mvc._
import play.twirl.api.Html

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.util.{Failure, Success, Try}

class WebAdmin @Inject()(cc: ControllerComponents)
  extends AExerciseAdminController[WebExercise](cc, WebToolObject, WebExercise.finder, WebExerciseReader) {

  override def statistics = new Html(
    s"""<li>Es existieren insgesamt ${WebExercise.finder.all.size} Aufgaben, davon
       |  <ul>
       |    <li>${WebExercise.withHtmlPart} Aufgaben mit HTML-Teil</li>
       |    <li>${WebExercise.withJsPart} Aufgaben mit JS-Teil</li>
       |  </ul>
       |</li>""".stripMargin)

  def exRest(exerciseId: Int) = Action { implicit request => Ok(views.html.webAdmin.webExRest.render(getUser, finder.byId(exerciseId))) }

}

class WebController @Inject()(cc: ControllerComponents)
  extends IdPartExController[WebExercise, WebResult](cc, WebExercise.finder, WebToolObject) {

  override type SolType = StringSolution

  override def solForm: Form[StringSolution] = ???

  val HTML_TYPE = "html"
  val JS_TYPE = "js"

  val BASE_URL = "http://localhost:9000"

  val ALLOWED_TYPES = List(HTML_TYPE, JS_TYPE)

  override def getUser(implicit request: Request[AnyContent]): User = {
    val user = super.getUser

    Option(WebUser.finder.byId(user.name)) match {
      case None =>
        // Make sure there is a corresponding entrance in other db...
        new WebUser(user.name).save()
      case _ => Unit
    }

    user
  }

  protected def correctEx(learnerSolution: String, exercise: WebExercise, user: User, exType: String): Try[CompleteResult[WebResult]] = {
    saveSolution(learnerSolution, new WebSolutionKey(user.name, exercise.id))

    val solutionUrl = BASE_URL + routes.SolutionController.site(user.name, exercise.getId).url

    val driver = new HtmlUnitDriver(true)
    driver.get(solutionUrl)

    val tasksTry: Try[scala.collection.mutable.Buffer[_ <: WebTask]] = exType match {
      case JS_TYPE => Success(exercise.jsTasks.asScala)
      case HTML_TYPE => Success(exercise.htmlTasks.asScala)
      case _ => Failure(null)
    }

    tasksTry.map(tasks => new CompleteResult(learnerSolution, tasks.map(WebCorrector.evaluate(_, driver)).toList))
  }

  def saveSolution(learnerSolution: String, key: WebSolutionKey): Unit = {
    val solution = Option(WebSolution.finder.byId(key)).getOrElse(new WebSolution(key))

    solution.sol = learnerSolution
    solution.save()
  }

  override def exercise(id: Int, part: String) = Action { implicit request =>
    part match {
      case (JS_TYPE | HTML_TYPE) =>
        val user = getUser

        log(user, new ExerciseStartEvent(request, id))

        Ok(views.html.webExercise.render(user, WebExercise.finder.byId(id), part,
          WebController.getOldSolOrDefault(user.name, id), "Html-Korrektur"))
      case _ =>
        Redirect(routes.WebController.index(0))
    }
  }

  def playground = Action { implicit request => Ok(views.html.webPlayground.render(getUser)) }

  override def correctEx(solType: StringSolution, exercise: WebExercise, user: User): Try[CompleteResult[WebResult]] = Failure(new Throwable("Not used..."))

  override def renderExercise(user: User, exercise: WebExercise): Html = ??? // FIXME

  override def renderExesListRest = new Html(
    s"""<div class="panel panel-default">
  <a class="btn btn-primary btn-block" href="${controllers.web.routes.WebController.playground()}">Web-Playground</a>
</div>
<hr>""")

  override def renderResult(correctionResult: CompleteResult[WebResult]): Html = views.html.webResult.render(correctionResult.results)

}

class SolutionController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def site(username: String, exerciseId: Int) = Action { implicit request => Ok(new Html(WebController.getOldSolOrDefault(username, exerciseId))) }

}

object WebController {

  def getOldSolOrDefault(userName: String, exerciseId: Int): String =
    Try(WebSolution.finder.byId(new WebSolutionKey(userName, exerciseId)).sol).getOrElse(STANDARD_HTML)

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