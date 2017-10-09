package controllers.web

import scala.collection.JavaConverters.{ asScalaBufferConverter, seqAsJavaListConverter }
import scala.collection.mutable.Buffer
import scala.util.{ Failure, Success, Try }

import org.openqa.selenium.htmlunit.HtmlUnitDriver

import controllers.core.{ BaseController, IdExController }
import javax.inject.Inject
import model.{ StringConsts, WebExercise, WebSolution, WebSolutionKey, WebUser }
import model.logging.{ ExerciseCompletionEvent, ExerciseStartEvent }
import model.result.{ CompleteResult, WebResult }
import model.task.WebTask
import model.user.User
import play.data.{ DynamicForm, FormFactory }
import play.mvc.{ Controller, Results }
import play.twirl.api.Html

class WebController @Inject() (f: FormFactory) extends IdExController[WebExercise, WebResult](f, "web", WebExercise.finder, WebToolObject) {

  val HTML_TYPE = "html"
  val JS_TYPE = "js"

  val BASE_URL = "http://localhost:9000"

  val ALLOWED_TYPES = List(HTML_TYPE, JS_TYPE)

  def getUser: User = {
    val user = BaseController.getUser

    if (WebUser.finder.byId(user.name) == null)
      // Make sure there is a corresponding entrance in other db...
      new WebUser(user.name).save()

    user
  }

  def correctEx(learnerSolution: String, exercise: WebExercise, user: User, exType: String) = {
    saveSolution(learnerSolution, new WebSolutionKey(user.name, exercise.id))

    val solutionUrl = BASE_URL + routes.SolutionController.site(user.name, exercise.getId).url

    val driver = new HtmlUnitDriver(true)
    driver.get(solutionUrl)

    val tasksTry: Try[Buffer[_ <: WebTask]] = exType match {
      case JS_TYPE   => Success(exercise.jsTasks.asScala)
      case HTML_TYPE => Success(exercise.htmlTasks.asScala)
      case _         => Failure(null)
    }

    tasksTry.map(tasks => new CompleteResult(learnerSolution, tasks.map(_.evaluate(driver)).asJava))
  }

  def saveSolution(learnerSolution: String, key: WebSolutionKey) = {
    val solution = Option(WebSolution.finder.byId(key)).getOrElse(new WebSolution(key))

    solution.sol = learnerSolution
    solution.save
  }

  def correct(id: Int, exType: String) = {
    val user = getUser
    val learnerSolution = factory.form().bindFromRequest().get(StringConsts.FORM_VALUE)

    correctEx(learnerSolution, WebExercise.finder.byId(id), user, exType) match {
      case Success(result) =>
        BaseController.log(user, new ExerciseCompletionEvent(Controller.request, id, result))
        Results.ok(views.html.correction.render("Web", result, renderResult(result), user, routes.WebController.index(0)))
      case Failure(_) => Results.badRequest("Es gab einen internen Fehler!")
    }
  }

  def correctLive(id: Int, exType: String) = {
    val user = getUser
    val learnerSolution = factory.form().bindFromRequest().get(StringConsts.FORM_VALUE)

    correctEx(learnerSolution, WebExercise.finder.byId(id), user, exType) match {
      case Success(result) =>
        BaseController.log(user, new ExerciseCompletionEvent(Controller.request, id, result))
        Results.ok(renderResult(result))
      case Failure(_) => Results.badRequest("Es gab einen internen Fehler!")
    }
  }

  def exercise(id: Int, exType: String) = exType match {
    case (JS_TYPE | HTML_TYPE) =>
      val user = getUser

      BaseController.log(user, new ExerciseStartEvent(Controller.request(), id))

      Results.ok(views.html.webExercise.render(user, WebExercise.finder.byId(id), exType,
                                               WebController.getOldSolOrDefault(user.name, id), "Html-Korrektur"))
    case _ =>
      Results.redirect(routes.WebController.index(0))
  }

  def playground = Results.ok(views.html.webPlayground.render(getUser))

  override def correctEx(form: DynamicForm, exercise: WebExercise, user: User) = // FIXME
    ???

  override def renderExercise(user: User, exercise: WebExercise) = // FIXME
    ???

  override def renderExesListRest = new Html(s"""<div class="panel panel-default">
  <a class="btn btn-primary btn-block" href="${controllers.web.routes.WebController.playground}">Web-Playground</a>
</div>
<hr>""")

  override def renderResult(correctionResult: CompleteResult[WebResult]) = views.html.webResult.render(correctionResult.results)

}

class SolutionController extends Controller {

  def site(username: String, exerciseId: Int) = Results.ok(new Html(WebController.getOldSolOrDefault(username, exerciseId)))

}

object WebController {

  def getOldSolOrDefault(userName: String, exerciseId: Int) =
    Try(WebSolution.finder.byId(new WebSolutionKey(userName, exerciseId)).sol).getOrElse(STANDARD_HTML)

  val STANDARD_HTML = """<!doctype html>
<html>
<head>
  
</head>
<body>
  
</body>
</html>"""

  val STANDARD_HTML_PLAYGROUND = s"""<!doctype html>
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