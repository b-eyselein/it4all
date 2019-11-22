package model.tools.collectionTools.regex

import model._
import model.points._
import model.tools.collectionTools.{CollectionToolMain, Exercise, ExerciseCollection, ToolJsonProtocol}
import net.jcazevedo.moultingyaml.YamlFormat
import play.api.libs.json.JsString
import play.api.mvc.{AnyContent, Request}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object RegexToolMain extends CollectionToolMain(RegexConsts) {

  override type PartType = RegexExPart
  override type ExContentType = RegexExerciseContent

  override type SolType = String
  override type SampleSolType = StringSampleSolution
  override type UserSolType = StringUserSolution[RegexExPart]

  override type ResultType = RegexEvalutationResult
  override type CompResultType = RegexCompleteResult

  // Members

  override val exParts: Seq[RegexExPart] = RegexExParts.values

  // Yaml, Html forms, Json

  override protected val toolJsonProtocol: ToolJsonProtocol[RegexExPart, RegexExerciseContent, String, StringSampleSolution, StringUserSolution[RegexExPart], RegexCompleteResult] =
    RegexToolJsonProtocol

  override protected val exerciseContentYamlFormat: YamlFormat[RegexExerciseContent] = RegexToolYamlProtocol.regexExerciseYamlFormat

  // Database helpers

  override protected def instantiateSolution(id: Int, exercise: Exercise, part: RegexExPart, solution: String, points: Points, maxPoints: Points): StringUserSolution[RegexExPart] =
    StringUserSolution[RegexExPart](id, part, solution, points, maxPoints)

  override def updateSolSaved(compResult: RegexCompleteResult, solSaved: Boolean): RegexCompleteResult =
    compResult.copy(solutionSaved = solSaved)

  // Correction

  override protected def readSolution(request: Request[AnyContent], part: RegexExPart): Either[String, String] = request.body.asJson match {
    case None          => Left("Body did not contain json!")
    case Some(jsValue) => jsValue match {
      case JsString(regex) => Right(regex)
      case other           => Left(s"Json was no string but $other")
    }
  }

  override protected def correctEx(
    user: User, sol: String, coll: ExerciseCollection, exercise: Exercise, content: RegexExerciseContent, part: RegexExPart
  )(implicit executionContext: ExecutionContext): Future[Try[RegexCompleteResult]] = Future.successful(RegexCorrector.correct(sol, content))

}
