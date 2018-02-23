package controllers.exes.idPartExes

import controllers.Secured
import javax.inject.Inject
import model.programming.ProgLanguage
import model.rose.{RoseCompleteResult, RoseEvalResult, _}
import model.yaml.MyYamlFormat
import model.{JsonFormat, User}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{JsString, JsValue, Json}
import play.api.mvc._
import play.twirl.api.Html
import views.html.rose._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

class RoseController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, protected val tables: RoseTableDefs)(implicit ec: ExecutionContext)
  extends AIdPartExController[RoseExPart](cc, dbcp, RoseToolObject)
    with Secured with JsonFormat {

  override protected def partTypeFromUrl(urlName: String): Option[RoseExPart] = Some(RoseSingleExPart)

  // Result types

  override type ExType = RoseExercise

  override type CompExType = RoseCompleteEx

  override type Tables = RoseTableDefs

  override type R = RoseEvalResult

  override type CompResult = RoseCompleteResult

  // Reading solution from requests

  override type SolType = String

  override protected def readSolutionFromPostRequest(user: User, id: Int)(implicit request: Request[AnyContent]): Option[String] = ???

  override protected def readSolutionForPartFromJson(user: User, id: Int, jsValue: JsValue, part: RoseExPart): Option[String] = jsValue.asObj flatMap { jsObj =>
    jsObj.stringField("implementation")
  }

  // Yaml

  override implicit val yamlFormat: MyYamlFormat[RoseCompleteEx] = RoseExYamlProtocol.RoseExYamlFormat

  // Views
  override protected def renderExercise(user: User, exercise: RoseCompleteEx, part: RoseExPart): Future[Html] = {

    // FIXME: load old solution!
    val futureOldSolOrDec: Future[String] = tables.loadSolution(user.username, exercise.id) map (_ getOrElse exercise.declaration(forUser = true))

    //    val exOptions = ExerciseOptions("rose", "python", 10, 20, updatePrev = false)
    //    val declaration = "def act(self) -> Action:\n  pass"

    futureOldSolOrDec map (oldSolution => roseExercise.render(user, RoseToolObject, exercise, oldSolution))
  }

  // Correction

  override protected def correctEx(user: User, sol: String, exercise: RoseCompleteEx): Future[Try[RoseCompleteResult]] = {
    // FIXME: save solution
    tables.saveSolution(RoseSolution(user.username, exercise.id, sol))

    RoseCorrector.correct(user, exercise, sol, ProgLanguage.STANDARD_LANG) map (result => Try(RoseCompleteResult(sol, result)))
  }

  // Result handlers

  override protected def onSubmitCorrectionResult(user: User, result: RoseCompleteResult): Html = ??? // Ok(views.html.rose.roseTestSolution.render(user))

  override protected def onSubmitCorrectionError(user: User, error: Throwable): Html = ???

  override protected def onLiveCorrectionResult(result: RoseCompleteResult): JsValue = {
    val (resultType, resultJson): (String, JsValue) = result.result match {
      case rer: RoseExecutionResult    => ("success", Json.parse(rer.result))
      case rser: RoseSyntaxErrorResult => ("syntaxError", JsString(rser.cause))
      case other                       => ("error", JsString(other.toString))
    }

    Json.obj("resultType" -> resultType, "result" -> resultJson)
  }

}
