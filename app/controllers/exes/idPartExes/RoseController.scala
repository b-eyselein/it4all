package controllers.exes.idPartExes

import javax.inject.{Inject, Singleton}
import model.programming.ProgLanguage
import model.rose._
import model.yaml.MyYamlFormat
import model.{Consts, JsonFormat, User}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json.{JsString, JsValue, Json}
import play.api.mvc._
import play.twirl.api.Html
import views.html.rose._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

@Singleton
class RoseController @Inject()(cc: ControllerComponents, dbcp: DatabaseConfigProvider, val tables: RoseTableDefs)(implicit ec: ExecutionContext)
  extends JsonFormat with AIdPartExToolMain[RoseExPart, RoseSolution, RoseExercise, RoseCompleteEx] {

  override val urlPart : String = "rose"
  override val toolname: String = "Rose"
  override val exType  : String = "rose"
  override val consts  : Consts = RoseConsts

  override def partTypeFromUrl(urlName: String): Option[RoseExPart] = Some(RoseSingleExPart)

  // Result types

  override type Tables = RoseTableDefs

  override type R = RoseEvalResult

  override type CompResult = RoseCompleteResult

  override def saveSolution(sol: RoseSolution): Future[Boolean] = tables.saveSolution(sol)

  override def readOldSolution(user: User, exerciseId: Int, part: RoseExPart): Future[Option[RoseSolution]] = ???

  override def readSolutionFromPostRequest(user: User, id: Int)(implicit request: Request[AnyContent]): Option[RoseSolution] = ???

  override def readSolutionForPartFromJson(user: User, id: Int, jsValue: JsValue, part: RoseExPart): Option[RoseSolution] = jsValue.asObj flatMap { jsObj =>
    jsObj.stringField("implementation")
  } map (RoseSolution(user.username, id, _))

  // Yaml

  override implicit val yamlFormat: MyYamlFormat[RoseCompleteEx] = RoseExYamlProtocol.RoseExYamlFormat

  // Views
  override def renderExercise(user: User, exercise: RoseCompleteEx, maybePart: Option[RoseExPart]): Future[Html] = maybePart match {
    case Some(part) =>


      // FIXME: load old solution!
      val futureOldSolOrDec: Future[String] = tables.loadSolution(user.username, exercise.id) map (_ getOrElse exercise.declaration(forUser = true))

      //    val exOptions = ExerciseOptions("rose", "python", 10, 20, updatePrev = false)
      //    val declaration = "def act(self) -> Action:\n  pass"

      futureOldSolOrDec map (oldSolution => roseExercise.render(user, exercise, oldSolution))
    case None       => ???
  }

  // Correction

  override protected def correctEx(user: User, sol: RoseSolution, exercise: RoseCompleteEx): Future[Try[RoseCompleteResult]] = {
    // FIXME: save solution
    saveSolution(sol)

    RoseCorrector.correct(user, exercise, sol.solution, ProgLanguage.STANDARD_LANG) map (result => Try(RoseCompleteResult(sol.solution, result)))
  }

  // Result handlers

  override def onSubmitCorrectionResult(user: User, result: RoseCompleteResult): Html = ??? // Ok(views.html.rose.roseTestSolution.render(user))

  override def onSubmitCorrectionError(user: User, error: Throwable): Html = ???

  override def onLiveCorrectionResult(result: RoseCompleteResult): JsValue = {
    val (resultType, resultJson): (String, JsValue) = result.result match {
      case rer: RoseExecutionResult    => ("success", Json.parse(rer.result))
      case rser: RoseSyntaxErrorResult => ("syntaxError", JsString(rser.cause))
      case other                       => ("error", JsString(other.toString))
    }

    Json.obj("resultType" -> resultType, "result" -> resultJson)
  }

}
