package model.tools.programming

import model.User
import model.tools._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object ProgTool extends CollectionTool("programming", "Programmierung", ToolState.ALPHA) {

  override type ExerciseType   = ProgrammingExercise
  override type SolType        = ProgSolution
  override type PartType       = ProgExPart
  override type CompResultType = ProgCompleteResult

  // Yaml, Html Forms, Json

  override val toolJsonProtocol: ToolJsonProtocol[ProgrammingExercise, ProgSolution, ProgExPart] =
    ProgrammingToolJsonProtocol

  override val graphQlModels: ToolGraphQLModelBasics[ProgrammingExercise, ProgSolution, ProgExPart] =
    ProgrammingGraphQLModels

  // Correction

  override def correctAbstract(
    user: User,
    sol: ProgSolution,
    collection: ExerciseCollection,
    exercise: ProgrammingExercise,
    part: ProgExPart,
    solutionSaved: Boolean
  )(implicit ec: ExecutionContext): Future[Try[ProgCompleteResult]] =
    ProgCorrector.correct(user, sol, exercise, part, solutionSaved)

}
