package controllers.web

import javax.inject.Inject

import controllers.core.IdExController
import model._
import model.logging.{ExerciseCompletionEvent, ExerciseStartEvent}
import model.result.CompleteResult
import model.task.WebTask
import model.user.User
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import play.data.{DynamicForm, FormFactory}
import play.mvc.{Controller, Result, Results}
import play.twirl.api.Html

import scala.collection.JavaConverters.{asScalaBufferConverter, seqAsJavaListConverter}
import scala.util.{Failure, Success, Try}

class WebController @Inject()(f: FormFactory)
  extends IdExController[WebExercise, WebResult](f, WebExercise.finder, WebToolObject) {

  val HTML_TYPE = "html"
  val JS_TYPE = "js"

  val BASE_URL = "http://localhost:9000"

  val ALLOWED_TYPES = List(HTML_TYPE, JS_TYPE)

  override def getUser: User = {
    val user = getUser

    if (WebUser.finder.byId(user.name) == null)
    // Make sure there is a corresponding entrance in other db...
      new WebUser(user.name).save()

    user
  }

  def correctEx(learnerSolution: String, exercise: WebExercise, user: User, exType: String): Try[CompleteResult[WebResult]] = {
    saveSolution(learnerSolution, new WebSolutionKey(user.name, exercise.id))

    val solutionUrl = BASE_URL + routes.SolutionController.site(user.name, exercise.getId).url

    val driver = new HtmlUnitDriver(true)
    driver.get(solutionUrl)

    val tasksTry: Try[scala.collection.mutable.Buffer[_ <: WebTask]] = exType match {
      case JS_TYPE => Success(exercise.jsTasks.asScala)
      case HTML_TYPE => Success(exercise.htmlTasks.asScala)
      case _ => Failure(null)
    }

    tasksTry.map(tasks => new CompleteResult(learnerSolution, tasks.map(WebCorrector.evaluate(_, driver)).asJava))
  }

  def saveSolution(learnerSolution: String, key: WebSolutionKey): Unit = {
    val solution = Option(WebSolution.finder.byId(key)).getOrElse(new WebSolution(key))

    solution.sol = learnerSolution
    solution.save()
  }

  def correct(id: Int, exType: String): Result = {
    val user = getUser
    val learnerSolution = factory.form().bindFromRequest().get(StringConsts.FORM_VALUE)

    correctEx(learnerSolution, WebExercise.finder.byId(id), user, exType) match {
      case Success(result) =>
        log(user, new ExerciseCompletionEvent(Controller.request, id, result))
        Results.ok(views.html.correction.render("Web", result, renderResult(result), user, routes.WebController.index(0)))
      case Failure(_) => Results.badRequest("Es gab einen internen Fehler!")
    }
  }

  def correctLive(id: Int, exType: String): Result = {
    val user = getUser
    val learnerSolution = factory.form().bindFromRequest().get(StringConsts.FORM_VALUE)

    correctEx(learnerSolution, WebExercise.finder.byId(id), user, exType) match {
      case Success(result) =>
        log(user, new ExerciseCompletionEvent(Controller.request, id, result))
        Results.ok(renderResult(result))
      case Failure(_) => Results.badRequest("Es gab einen internen Fehler!")
    }
  }

  def exercise(id: Int, exType: String): Result = exType match {
    case (JS_TYPE | HTML_TYPE) =>
      val user = getUser

      log(user, new ExerciseStartEvent(Controller.request(), id))

      Results.ok(views.html.webExercise.render(user, WebExercise.finder.byId(id), exType,
        WebController.getOldSolOrDefault(user.name, id), "Html-Korrektur"))
    case _ =>
      Results.redirect(routes.WebController.index(0))
  }

  def playground: Result = Results.ok(views.html.webPlayground.render(getUser))

  override def correctEx(form: DynamicForm, exercise: WebExercise, user: User): Try[CompleteResult[WebResult]] = ??? // FIXME


  override def renderExercise(user: User, exercise: WebExercise): Html = ??? // FIXME


  override def renderExesListRest = new Html(
    s"""<div class="panel panel-default">
  <a class="btn btn-primary btn-block" href="${controllers.web.routes.WebController.playground()}">Web-Playground</a>
</div>
<hr>""")

  override def renderResult(correctionResult: CompleteResult[WebResult]): Html = views.html.webResult.render(correctionResult.results)

}

class SolutionController extends Controller {

  def site(username: String, exerciseId: Int): Result = Results.ok(new Html(WebController.getOldSolOrDefault(username, exerciseId)))

}

object WebController {

  def getOldSolOrDefault(userName: String, exerciseId: Int): String =
    Try(WebSolution.finder.byId(new WebSolutionKey(userName, exerciseId)).sol).getOrElse(STANDARD_HTML)

  val STANDARD_HTML =
    """<!doctype html>
<html>
<head>
  
</head>
<body>
  
</body>
</html>"""

  val STANDARD_HTML_PLAYGROUND =
    s"""<!doctype html>
<html>
<head>
  <style>
    /* Css-Anweisungen */
  </style>
  <script type=\"text/javascript\">
    // Javascript-Code
  </script>
</head>
<body>
  <!-- Html-Elemente -->

</body>
</html>"""
}