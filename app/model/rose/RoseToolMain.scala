package model.rose


import javax.inject.{Inject, Singleton}
import model.programming.ProgLanguages
import model.toolMains.{IdExerciseToolMain, ToolState}
import model.yaml.MyYamlFormat
import model.{Consts, ExerciseState, JsonFormat, User}
import play.api.data.Form
import play.api.libs.json.{JsString, JsValue, Json}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

@Singleton
class RoseToolMain @Inject()(val tables: RoseTableDefs)(implicit ec: ExecutionContext) extends IdExerciseToolMain("rose") with JsonFormat {

  // Abstract types

  override type ExType = RoseExercise

  override type CompExType = RoseCompleteEx

  override type Tables = RoseTableDefs

  override type PartType = RoseExPart

  override type SolType = String

  override type DBSolType = RoseSolution

  override type R = RoseEvalResult

  override type CompResult = RoseCompleteResult

  // Other members

  override val toolname: String = "Rose"

  override val toolState: ToolState = ToolState.BETA

  override val consts: Consts = RoseConsts

  override val exParts: Seq[RoseExPart] = RoseExParts.values

  // TODO: create Form mapping ...
  override implicit val compExForm: Form[RoseExercise] = null

  // DB

  override def readSolutionForPartFromJson(user: User, id: Int, jsValue: JsValue, part: RoseExPart): Option[SolType] =
    jsValue.asObj flatMap (_.stringField("implementation"))

  // Other helper methods

  override def instantiateExercise(id: Int, state: ExerciseState): RoseCompleteEx = RoseCompleteEx(
    RoseExercise(id, title = "", author = "", text = "", state, fieldWidth = 0, fieldHeight = 0, isMultiplayer = false),
    inputType = Seq.empty, sampleSolution = null
  )

  override def instantiateSolution(username: String, exerciseId: Int, part: RoseExPart, solution: String, points: Double, maxPoints: Double): RoseSolution =
    RoseSolution(username, exerciseId, part, solution, points, maxPoints)

  // Yaml

  override implicit val yamlFormat: MyYamlFormat[RoseCompleteEx] = RoseExYamlProtocol.RoseExYamlFormat

  // Views

  override def renderExercise(user: User, exercise: RoseCompleteEx, part: RoseExPart, maybeOldSolution: Option[RoseSolution]): Html =
    views.html.idExercises.rose.roseExercise(user, exercise, maybeOldSolution map (_.solution) getOrElse exercise.declaration(forUser = true), this)

  override def renderEditRest(exercise: RoseCompleteEx): Html = ???

  // Correction

  override protected def correctEx(user: User, sol: SolType, exercise: RoseCompleteEx, part: RoseExPart): Future[Try[RoseCompleteResult]] = {
    val solDir = solutionDirForExercise(user.username, exercise.ex.id)

    for {
      result <- RoseCorrector.correct(user, exercise, sol, ProgLanguages.STANDARD_LANG, exerciseResourcesFolder, solDir)
    } yield Try(RoseCompleteResult(sol, result))
  }

  override def futureSampleSolutionForExerciseAndPart(id: Int, part: RoseExPart): Future[String] = part match {
    case RoseExParts.RoseSingleExPart => futureCompleteExById(id) map {
      case Some(exercise) => exercise.sampleSolution.head.solution
      case None           => ""
    }
  }

  // Result handlers

  override def onLiveCorrectionResult(pointsSaved: Boolean, result: RoseCompleteResult): JsValue = {
    val (resultType, resultJson): (String, JsValue) = result.result match {
      case rer: RoseExecutionResult    => ("success", Json.parse(rer.result))
      case rser: RoseSyntaxErrorResult => ("syntaxError", JsString(rser.cause))
      case other                       => (RoseConsts.errorName, JsString(other.toString))
    }

    Json.obj("resultType" -> resultType, "result" -> resultJson)
  }

}
