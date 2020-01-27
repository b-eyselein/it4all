package model.tools.collectionTools.regex

import model.User
import model.core.matching.{GenericAnalysisResult, MatchingResult}
import model.tools.collectionTools.{CollectionToolMain, Exercise, ExerciseCollection, StringSampleSolutionToolJsonProtocol}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import scala.util.matching.Regex.{Match => RegexMatch}

object RegexToolMain extends CollectionToolMain(RegexConsts) {

  override type PartType = RegexExPart
  override type ExContentType = RegexExerciseContent
  override type SolType = String
  override type CompResultType = RegexCompleteResult

  type ExtractedValuesComparison = MatchingResult[RegexMatch, GenericAnalysisResult, RegexMatchMatch]

  // Members

  override val exParts: Seq[RegexExPart] = RegexExParts.values

  // Yaml, Html forms, Json

  override protected val toolJsonProtocol: StringSampleSolutionToolJsonProtocol[RegexExerciseContent, RegexCompleteResult] =
    RegexToolJsonProtocol

  // Correction

  override protected def correctEx(
    user: User,
    sol: String,
    coll: ExerciseCollection,
    exercise: Exercise,
    content: RegexExerciseContent,
    part: RegexExPart,
    solutionSaved: Boolean
  )(implicit executionContext: ExecutionContext): Future[Try[RegexCompleteResult]] =
    Future.successful(RegexCorrector.correct(sol, content, solutionSaved))

}