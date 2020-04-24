package model.tools.regex

import model.User
import model.core.matching.MatchingResult
import model.points._
import model.tools._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.matching.Regex.{Match => RegexMatch}
import scala.util.{Success, Try}

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
    solution: String,
    exercise: Exercise[String, RegexExerciseContent],
    part: RegexExPart,
    solutionSaved: Boolean
  )(implicit executionContext: ExecutionContext): Future[Try[AbstractRegexResult]] = Future.successful {
    Success {
      Try(solution.r).fold(
        error => RegexIllegalRegexResult(solutionSaved, error.getMessage, exercise.content.maxPoints.points),
        userRegex =>
          exercise.content.correctionType match {
            case RegexCorrectionTypes.MATCHING =>
              RegexMatchingCorrector.correctMatching(exercise.content, userRegex, solutionSaved)

            case RegexCorrectionTypes.EXTRACTION =>
              RegexExtractionCorrector.correctExtraction(exercise.content, userRegex, solutionSaved)
          }
      )
    }
  }

}
