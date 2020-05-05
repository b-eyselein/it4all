package model.tools.regex

import model.LoggedInUser
import model.core.matching.MatchingResult
import model.graphql.ToolGraphQLModelBasics
import model.points._
import model.tools._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import scala.util.matching.Regex.{Match => RegexMatch}

object RegexTool extends CollectionTool("regex", "Reguläre Ausdrücke") {

  override type SolType       = String
  override type ExContentType = RegexExerciseContent
  override type PartType      = RegexExPart
  override type ResType       = RegexAbstractResult

  type ExtractedValuesComparison = MatchingResult[RegexMatch, RegexMatchMatch]

  // Yaml, Html forms, Json

  override val jsonFormats: StringSampleSolutionToolJsonProtocol[RegexExerciseContent, RegexExPart] =
    RegexToolJsonProtocol

  override val graphQlModels: ToolGraphQLModelBasics[String, RegexExerciseContent, RegexExPart, RegexAbstractResult] =
    RegexGraphQLModels

  // Correction

  override def correctAbstract(
    user: LoggedInUser,
    solution: String,
    exercise: Exercise[String, RegexExerciseContent],
    part: RegexExPart,
    solutionSaved: Boolean
  )(implicit executionContext: ExecutionContext): Future[RegexAbstractResult] = Future.successful {
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
