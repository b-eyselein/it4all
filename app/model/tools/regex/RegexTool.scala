package model.tools.regex

import initialData.InitialData
import initialData.regex.RegexInitialData
import model.graphql.ToolGraphQLModelBasics
import model.matching.MatchingResult
import model.tools._
import model.{Exercise, User}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import scala.util.matching.Regex.{Match => RegexMatch}

object RegexTool extends Tool("regex", "Reguläre Ausdrücke") {

  override type SolutionInputType = String
  override type ExContentType     = RegexExerciseContent
  override type PartType          = RegexExPart
  override type ResType           = RegexAbstractResult

  type RegexExercise = Exercise[RegexExerciseContent]

  type ExtractedValuesComparison = MatchingResult[RegexMatch, RegexMatchMatch]

  // Yaml, Html forms, Json

  override val jsonFormats: StringSolutionToolJsonProtocol[RegexExerciseContent, RegexExPart] = RegexToolJsonProtocol

  override val graphQlModels: ToolGraphQLModelBasics[String, RegexExerciseContent, RegexExPart, RegexAbstractResult] = RegexGraphQLModels

  // Correction

  override def correctAbstract(
    user: User,
    solution: String,
    exercise: RegexExercise,
    part: RegexExPart
  )(implicit executionContext: ExecutionContext): Future[RegexAbstractResult] = Future.fromTry {
    for {
      userRegex <- Try(solution.r)

      result <- exercise.content.correctionType match {
        case RegexCorrectionType.MATCHING   => RegexMatchingCorrector.correctMatching(exercise.content, userRegex)
        case RegexCorrectionType.EXTRACTION => RegexExtractionCorrector.correctExtraction(exercise.content, userRegex)
      }
    } yield result
  }

  override val initialData: InitialData[RegexExerciseContent] = RegexInitialData.initialData

}
