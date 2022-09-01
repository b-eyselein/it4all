package model.tools.regex

import initialData.InitialData
import initialData.regex.RegexInitialData
import model.graphql.ToolWithoutPartsGraphQLModel
import model.matching.MatchingResult
import model.tools._
import model.{Exercise, User}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import scala.util.matching.Regex.{Match => RegexMatch}

object RegexTool extends ToolWithoutParts("regex", "Reguläre Ausdrücke") {

  override type SolInputType = String
  override type ExContType     = RegexExerciseContent
  override type ResType           = RegexAbstractResult

  type RegexExercise = Exercise[RegexExerciseContent]

  type ExtractedValuesComparison = MatchingResult[RegexMatch, RegexMatchMatch]

  // Yaml, Html forms, Json

  override val jsonFormats: StringSolutionToolJsonProtocol[RegexExerciseContent] = RegexToolJsonProtocol

  override val graphQlModels: ToolWithoutPartsGraphQLModel[String, RegexExerciseContent, RegexAbstractResult] = RegexGraphQLModels

  // Correction

  override def correctAbstract(
    user: User,
    solution: String,
    exercise: RegexExercise
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
