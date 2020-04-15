package model.tools.regex

import model.User
import model.core.matching.MatchingResult
import model.points._
import model.tools._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.matching.Regex.{Match => RegexMatch}
import scala.util.{Failure, Success, Try}

object RegexTool extends CollectionTool("regex", "Reguläre Ausdrücke") {

  override type ExerciseType   = RegexExercise
  override type PartType       = RegexExPart
  override type SolType        = String
  override type CompResultType = AbstractRegexResult

  type ExtractedValuesComparison = MatchingResult[RegexMatch, RegexMatchMatch]

  // Yaml, Html forms, Json

  override val toolJsonProtocol: StringSampleSolutionToolJsonProtocol[RegexExercise, RegexExPart] =
    RegexToolJsonProtocol

  override val graphQlModels: ToolGraphQLModelBasics[RegexExercise, String, RegexExPart] =
    RegexGraphQLModels

  // Correction

  override def correctAbstract(
    user: User,
    sol: String,
    coll: ExerciseCollection,
    exercise: RegexExercise,
    part: RegexExPart,
    solutionSaved: Boolean
  )(implicit executionContext: ExecutionContext): Future[Try[AbstractRegexResult]] = Future.successful {

    Try(sol.r) match {
      case Failure(error) =>
        Success(RegexIllegalRegexResult(solutionSaved, error.getMessage, exercise.maxPoints.points))

      case Success(userRegex) =>
        exercise.correctionType match {

          case RegexCorrectionTypes.MATCHING =>
            Success(RegexMatchingCorrector.correctMatching(exercise, userRegex, solutionSaved))

          case RegexCorrectionTypes.EXTRACTION =>
            Success(RegexExtractionCorrector.correctExtraction(exercise, userRegex, solutionSaved))
        }

    }
  }

}
