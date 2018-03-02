package model.rose

import javax.inject.{Inject, Singleton}
import model.programming.ProgLanguage
import model.toolMains.AExerciseToolMain
import model.yaml.MyYamlFormat
import model.{Consts, Enums, JsonFormat, User}
import play.api.libs.json.{JsString, JsValue, Json}
import play.api.mvc._
import play.twirl.api.Html
import views.html.rose._

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

  override val consts: Consts = RoseConsts

  override val exParts: Seq[RoseExPart] = RoseExParts.values

  // DB

  override def futureSaveSolution(sol: RoseSolution): Future[Boolean] = tables.saveSolution(sol)

  override def futureReadOldSolution(user: User, exerciseId: Int, part: RoseExPart): Future[Option[RoseSolution]] = ???

  override def readSolutionFromPostRequest(user: User, id: Int, part: RoseExPart)(implicit request: Request[AnyContent]): Option[RoseSolution] = ???

  override def readSolutionForPartFromJson(user: User, id: Int, jsValue: JsValue, part: RoseExPart): Option[RoseSolution] = jsValue.asObj flatMap { jsObj =>
    jsObj.stringField("implementation")
  } map (RoseSolution(user.username, id, _))

  // Other helper methods

  override def instantiateExercise(id: Int, state: Enums.ExerciseState): RoseCompleteEx = RoseCompleteEx(
    RoseExercise(id, title = "", author = "", text = "", state, isMultiplayer = false),
    inputType = Seq.empty, sampleSolution = null
  )

  // Yaml

  override implicit val yamlFormat: MyYamlFormat[RoseCompleteEx] = RoseExYamlProtocol.RoseExYamlFormat

  // Views

  override def renderExercise(user: User, exercise: RoseCompleteEx, maybePart: RoseExPart): Future[Html] = {
    // FIXME: load old solution!
    val futureOldSolOrDec: Future[String] = tables.loadSolution(user.username, exercise.ex.id) map (_ getOrElse exercise.declaration(forUser = true))

    //    val exOptions = ExerciseOptions("rose", "python", 10, 20, updatePrev = false)
    //    val declaration = "def act(self) -> Action:\n  pass"

    futureOldSolOrDec map (oldSolution => roseExercise.render(user, exercise, oldSolution))
  }

  override def renderEditRest(exercise: RoseCompleteEx): Html = ???

  // Correction

  override protected def correctEx(user: User, sol: RoseSolution, exercise: RoseCompleteEx): Future[Try[RoseCompleteResult]] = {
    // FIXME: save solution
    futureSaveSolution(sol)

    RoseCorrector.correct(user, exercise, sol.solution, ProgLanguage.STANDARD_LANG, solutionDirForExercise(user.username, exercise.ex.id), exerciseResourcesFolder) map {
      result => Try(RoseCompleteResult(sol.solution, result))
    }

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
