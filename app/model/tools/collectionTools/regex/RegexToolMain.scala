package model.tools.collectionTools.regex

import model.User
import model.core.matching.MatchingResult
import model.points._
import model.tools.collectionTools._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.matching.Regex.{Match => RegexMatch}
import scala.util.{Failure, Success, Try}

object RegexToolMain extends CollectionToolMain(RegexConsts) {

  override type PartType       = RegexExPart
  override type ExContentType  = RegexExerciseContent
  override type SolType        = String
  override type CompResultType = AbstractRegexResult

  type ExtractedValuesComparison = MatchingResult[RegexMatch, RegexMatchMatch]

  // Members

  override val exParts: Seq[RegexExPart] = RegexExParts.values

  // Yaml, Html forms, Json

  override val toolJsonProtocol: StringSampleSolutionToolJsonProtocol[RegexExerciseContent, RegexExPart] =
    RegexToolJsonProtocol

  override val graphQlModels: ToolGraphQLModelBasics[RegexExerciseContent, String, RegexExPart] = RegexGraphQLModels

  // Correction

  override protected def correctEx(
    user: User,
    sol: String,
    coll: ExerciseCollection,
    exercise: Exercise,
    content: RegexExerciseContent,
    part: RegexExPart,
    solutionSaved: Boolean
  )(implicit executionContext: ExecutionContext): Future[Try[AbstractRegexResult]] = Future.successful {

    Try(sol.r) match {
      case Failure(error) => Success(RegexIllegalRegexResult(solutionSaved, error.getMessage, content.maxPoints.points))

      case Success(userRegex) =>
        content.correctionType match {

          case RegexCorrectionTypes.MATCHING =>
            Success(RegexMatchingCorrector.correctMatching(content, userRegex, solutionSaved))

          case RegexCorrectionTypes.EXTRACTION =>
            Success(RegexExtractionCorrector.correctExtraction(content, userRegex, solutionSaved))
        }

    }
  }

}
