package model.tools.programming

import model.User
import model.tools._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object ProgTool extends CollectionTool("programming", "Programmierung", ToolState.ALPHA) {

  override type SolType        = ProgSolution
  override type ExContentType  = ProgrammingExerciseContent
  override type PartType       = ProgExPart
  override type CompResultType = ProgCompleteResult

  // Yaml, Html Forms, Json

  override val toolJsonProtocol: ToolJsonProtocol[ProgSolution, ProgrammingExerciseContent, ProgExPart] =
    ProgrammingToolJsonProtocol

  override val graphQlModels: ToolGraphQLModelBasics[ProgSolution, ProgrammingExerciseContent, ProgExPart] =
    ProgrammingGraphQLModels

  override val allTopics: Seq[Topic] = ProgrammingTopics.values

  // Correction

  override def correctAbstract(
    user: User,
    sol: ProgSolution,
    collection: ExerciseCollection,
    exercise: Exercise[ProgSolution, ProgrammingExerciseContent],
    part: ProgExPart,
    solutionSaved: Boolean
  )(implicit ec: ExecutionContext): Future[Try[ProgCompleteResult]] = ProgCorrector.correct(
    user,
    sol,
    exercise,
    part,
    solutionSaved
  )

}
