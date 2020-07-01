package model.tools.regex

import initialData.InitialData
import initialData.regex.RegexInitialData
import model.core.matching.MatchingResult
import model.graphql.ToolGraphQLModelBasics
import model.points._
import model.tools._
import model.{Exercise, LoggedInUser}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import scala.util.matching.Regex.{Match => RegexMatch}

object RegexTool extends Tool("regex", "Reguläre Ausdrücke") {

  override type SolType       = String
  override type ExContentType = RegexExerciseContent
  override type PartType      = RegexExPart
  override type ResType       = RegexAbstractResult

  type RegexExercise = Exercise[RegexExerciseContent]

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
    exercise: RegexExercise,
    part: RegexExPart,
    solutionSaved: Boolean
  )(implicit executionContext: ExecutionContext): Future[RegexAbstractResult] =
    Future.successful {
      Try(solution.r).fold(
        error =>
          RegexInternalErrorResult(
            "Your regex could not be parsed: " + error.getMessage,
            solutionSaved,
            exercise.content.maxPoints.points
          ),
        userRegex =>
          exercise.content.correctionType match {
            case RegexCorrectionType.MATCHING =>
              RegexMatchingCorrector.correctMatching(exercise.content, userRegex, solutionSaved)

            case RegexCorrectionType.EXTRACTION =>
              RegexExtractionCorrector.correctExtraction(exercise.content, userRegex, solutionSaved)
          }
      )
    }

  override val initialData: InitialData[RegexExerciseContent] = RegexInitialData

}
