package model.web

import java.nio.file.Path

import javax.inject._
import model.toolMains.{IdExerciseToolMain, ToolState}
import model.yaml.MyYamlFormat
import model.{Consts, ExerciseState, JsonFormat, User}
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import play.api.data.Form
import play.api.libs.json.JsValue
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions
import scala.util.Try

@Singleton
class WebToolMain @Inject()(val tables: WebTableDefs)(implicit ec: ExecutionContext) extends IdExerciseToolMain("web") with JsonFormat {

  // Result types

  override type ExType = WebExercise

  override type CompExType = WebCompleteEx

  override type Tables = WebTableDefs

  override type PartType = WebExPart

  override type SolType = String

  override type DBSolType = WebSolution

  override type R = WebResult

  override type CompResult = WebCompleteResult

  // Other members

  override val hasPlayground: Boolean = true

  override val toolname: String = "Web"

  override val toolState: ToolState = ToolState.LIVE

  override val consts: Consts = WebConsts

  override val exParts: Seq[WebExPart] = WebExParts.values

  // TODO: create Form mapping ...
  override implicit val compExForm: Form[WebExercise] = null

  // DB

  override def futureSaveSolution(sol: WebSolution): Future[Boolean] = tables.futureSaveSolution(sol)

  def writeWebSolutionFile(username: String, exerciseId: Int, part: WebExPart, content: String): Try[Path] = {
    val fileEnding = part match {
      case WebExParts.PHPPart => "php"
      case _                  => "html"
    }

    val target: Path = solutionDirForExercise(username, exerciseId) / ("test." + fileEnding)
    write(target, content)
  }

  override def futureOldOrDefaultSolution(user: User, exerciseId: Int, part: WebExPart): Future[Option[DBSolType]] =
    super.futureOldOrDefaultSolution(user, exerciseId, part) flatMap {
      case Some(solution) => Future(Some(solution))
      case None           =>
        part match {
          case WebExParts.JsPart => super.futureOldOrDefaultSolution(user, exerciseId, WebExParts.HtmlPart)
          case _                 => Future(None)
        }
    }

  // Reading solution from request

  override def readSolutionForPartFromJson(user: User, id: Int, jsValue: JsValue, part: WebExPart): Option[SolType] = jsValue.asStr

  // Other helper methods

  override def instantiateExercise(id: Int, state: ExerciseState): WebCompleteEx = WebCompleteEx(
    WebExercise(id, title = "", author = "", text = "", state, htmlText = None, jsText = None, phpText = None),
    htmlTasks = Seq.empty, jsTasks = Seq.empty
  )

  override def instantiateSolution(username: String, exerciseId: Int, part: WebExPart, solution: String, points: Double, maxPoints: Double): WebSolution =
    WebSolution(username, exerciseId, part, solution, points, maxPoints)

  // Yaml

  override val yamlFormat: MyYamlFormat[WebCompleteEx] = WebExYamlProtocol.WebExYamlFormat

  // Views

  override def renderExerciseEditForm(user: User, newEx: WebCompleteEx, isCreation: Boolean): Html =
    views.html.idExercises.web.editWebExercise(user, this, newEx, isCreation)

  override def renderExercise(user: User, exercise: WebCompleteEx, part: WebExPart, maybeOldSolution: Option[WebSolution]): Html =
    views.html.idExercises.web.webExercise(user, exercise, part, maybeOldSolution map (_.solution) getOrElse WebConsts.STANDARD_HTML, this)

  override def renderEditRest(exercise: WebCompleteEx): Html =
    views.html.idExercises.web.editWebExRest(exercise)

  override def playground(user: User): Html =
    views.html.idExercises.web.webPlayground(user)

  // Correction

  override def correctEx(user: User, learnerSolution: String, exercise: WebCompleteEx, part: WebExPart): Future[Try[WebCompleteResult]] = Future {
    writeWebSolutionFile(user.username, exercise.id, part, learnerSolution) flatMap { _ =>

      val driver = new HtmlUnitDriver(true)
      driver.get(getSolutionUrl(user, exercise.ex.id, part))

      val results = Try(exercise.tasksForPart(part) map (task => WebCorrector.evaluateWebTask(task, driver)))

      results map (WebCompleteResult(learnerSolution, exercise, part, _))
    }
  }

  override def futureSampleSolutionForExerciseAndPart(id: Int, part: WebExPart): Future[String] = ???

  // Other helper methods

  def getSolutionUrl(user: User, exerciseId: Int, part: WebExPart): String = part match {
    case WebExParts.PHPPart => s"http://localhost:9080/${user.username}/$exerciseId/test.php"
    case _                  => s"http://localhost:9080/${user.username}/$exerciseId/test.html"
  }

}