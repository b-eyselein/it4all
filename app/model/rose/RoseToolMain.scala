package model.rose

import javax.inject.{Inject, Singleton}
import model.Enums.ToolState
import model.programming.ProgLanguage
import model.toolMains.AExerciseToolMain
import model.yaml.MyYamlFormat
import model.{Consts, Enums, JsonFormat, User}
import play.api.data.Form
import play.api.libs.json.{JsString, JsValue, Json}
import play.api.mvc._
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

@Singleton
class RoseToolMain @Inject()(val tables: RoseTableDefs)(implicit ec: ExecutionContext) extends AExerciseToolMain("rose") with JsonFormat {

  // Abstract types

  override type ExType = RoseExercise

  override type CompExType = RoseCompleteEx

  override type Tables = RoseTableDefs

  override type PartType = RoseExPart

  override type SolType = RoseSolution

  override type R = RoseEvalResult

  override type CompResult = RoseCompleteResult

  // Other members

  override val toolname: String = "Rose"

  override val toolState: ToolState = ToolState.BETA

  override val consts: Consts = RoseConsts

  override val exParts: Seq[RoseExPart] = RoseExParts.values

  // TODO: create Form mapping ...
  override implicit val compExForm: Form[RoseCompleteEx] = null

  // DB

  override def futureSaveSolution(sol: RoseSolution): Future[Boolean] = tables.futureSaveSolution(sol)

  override def readSolutionFromPostRequest(user: User, id: Int, part: RoseExPart)(implicit request: Request[AnyContent]): Option[RoseSolution] = ???

  override def readSolutionForPartFromJson(user: User, id: Int, jsValue: JsValue, part: RoseExPart): Option[RoseSolution] = jsValue.asObj flatMap { jsObj =>
    jsObj.stringField("implementation")
  } map (RoseSolution(user.username, id, part, _))

  // Other helper methods

  override def instantiateExercise(id: Int, state: Enums.ExerciseState): RoseCompleteEx = RoseCompleteEx(
    RoseExercise(id, title = "", author = "", text = "", state, fieldWidth = 0, fieldHeight = 0, isMultiplayer = false),
    inputType = Seq.empty, sampleSolution = null
  )

  // Yaml

  override implicit val yamlFormat: MyYamlFormat[RoseCompleteEx] = RoseExYamlProtocol.RoseExYamlFormat

  // Views

  override def renderExercise(user: User, exercise: RoseCompleteEx, part: RoseExPart, maybeOldSolution: Option[RoseSolution]): Html =
    views.html.rose.roseExercise(user, exercise, maybeOldSolution map (_.solution) getOrElse exercise.declaration(forUser = true))

  override def renderEditRest(exercise: RoseCompleteEx): Html = ???

  // Correction

  override protected def correctEx(user: User, sol: RoseSolution, exercise: RoseCompleteEx): Future[Try[RoseCompleteResult]] = {
    val solDir = solutionDirForExercise(user.username, exercise.ex.id)

    for {
      solutionSaved <- futureSaveSolution(sol)
      result <- RoseCorrector.correct(user, exercise, sol.solution, ProgLanguage.STANDARD_LANG, exerciseResourcesFolder, solDir)
    } yield Try(RoseCompleteResult(solutionSaved, sol.solution, result))
  }

  // Result handlers

  override def onSubmitCorrectionResult(user: User, result: RoseCompleteResult): Html = ??? // Ok(views.html.rose.roseTestSolution.render(user))

  override def onSubmitCorrectionError(user: User, error: Throwable): Html = ???

  override def onLiveCorrectionResult(result: RoseCompleteResult): JsValue = {
    val (resultType, resultJson): (String, JsValue) = result.result match {
      case rer: RoseExecutionResult    => ("success", Json.parse(rer.result))
      case rser: RoseSyntaxErrorResult => ("syntaxError", JsString(rser.cause))
      case other                       => (RoseConsts.errorName, JsString(other.toString))
    }

    Json.obj("resultType" -> resultType, "result" -> resultJson)
  }

}
