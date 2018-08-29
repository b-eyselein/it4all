package model.rose


import javax.inject.{Inject, Singleton}
import model._
import model.programming.ProgLanguages
import model.toolMains.{IdExerciseToolMain, ToolState}
import model.yaml.MyYamlFormat
import play.api.data.Form
import play.api.libs.json._
import play.api.mvc.{AnyContent, Request}
import play.twirl.api.Html

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps
import scala.util.Try

@Singleton
class RoseToolMain @Inject()(val tables: RoseTableDefs)(implicit ec: ExecutionContext)
  extends IdExerciseToolMain("Rose", "rose") {

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

  override val toolState: ToolState = ToolState.BETA

  override val consts: Consts = RoseConsts

  override val exParts: Seq[RoseExPart] = RoseExParts.values

  // TODO: create Form mapping ...
  override implicit val compExForm: Form[RoseExercise] = null

  // DB

//  private def roseSolutionJsonReads(ex: RoseCompleteEx, user: User): Reads[RoseSolution] = (
//    (__ \ implementationName).read[String] and
//      (__ \ languageName).readNullable[ProgLanguage](ProgLanguages.jsonFormat) // TODO: temporary fix!
//    ) (RoseSolution.apply(user.username, ex.ex.id, ex.ex.semanticVersion, RoseExParts.RoseSingleExPart, _, -1 point, -1 point))

  override protected def readSolution(user: User, exercise: RoseCompleteEx, part: RoseExPart)(implicit request: Request[AnyContent]): Option[String] = request.body.asJson flatMap {
    case JsString(solution) => Some(solution)
    case _                  => None
  }

  //    request.body.asJson flatMap { jsValue =>
  //      roseSolutionJsonReads(exercise, user).reads(jsValue) match {
  //        case JsSuccess(solution, _) => Some(solution)
  //        case JsError(jsErrors)      =>
  //          jsErrors.foreach(println)
  //          None
  //      }
  //    }

  // Other helper methods

  override def instantiateExercise(id: Int, state: ExerciseState): RoseCompleteEx = RoseCompleteEx(
    RoseExercise(id, SemanticVersion(0, 1, 0), title = "", author = "", text = "", state, fieldWidth = 0, fieldHeight = 0, isMultiplayer = false),
    inputType = Seq.empty, sampleSolution = null
  )

  override def instantiateSolution(username: String, exercise: RoseCompleteEx, part: RoseExPart, solution: String, points: Points, maxPoints: Points): RoseSolution =
    RoseSolution(username, exercise.ex.id, exercise.ex.semanticVersion, part, solution, points, maxPoints)

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
