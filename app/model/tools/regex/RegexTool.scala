package model.tools.regex

import model.User
import model.core.matching.MatchingResult
import model.points._
import model.tools._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.matching.Regex.{Match => RegexMatch}
import scala.util.{Failure, Success, Try}

object RegexTool extends CollectionTool("regex", "Reguläre Ausdrücke") {

  override type SolType        = String
  override type ExContentType  = RegexExerciseContent
  override type PartType       = RegexExPart
  override type CompResultType = AbstractRegexResult

  type ExtractedValuesComparison = MatchingResult[RegexMatch, RegexMatchMatch]

  // Yaml, Html forms, Json

  override val toolJsonProtocol: StringSampleSolutionToolJsonProtocol[RegexExerciseContent, RegexExPart] =
    RegexToolJsonProtocol

  override val graphQlModels: ToolGraphQLModelBasics[String, RegexExerciseContent, RegexExPart] = RegexGraphQLModels

  // Correction

  override def correctAbstract(
    user: User,
    sol: String,
    coll: ExerciseCollection,
    exercise: Exercise[String, RegexExerciseContent],
    part: RegexExPart,
    solutionSaved: Boolean
  )(implicit executionContext: ExecutionContext): Future[Try[AbstractRegexResult]] = Future.successful {

    Try(sol.r) match {
      case Failure(error) =>
        Success(RegexIllegalRegexResult(solutionSaved, error.getMessage, exercise.content.maxPoints.points))

      case Success(userRegex) =>
        exercise.content.correctionType match {

          case RegexCorrectionTypes.MATCHING =>
            Success(RegexMatchingCorrector.correctMatching(exercise.content, userRegex, solutionSaved))

          case RegexCorrectionTypes.EXTRACTION =>
            Success(RegexExtractionCorrector.correctExtraction(exercise.content, userRegex, solutionSaved))
        }

    }
  }

}
