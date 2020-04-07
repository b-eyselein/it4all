package model.tools.programming

import model.User
import model.tools._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object ProgToolMain extends CollectionToolMain("programming", "Programmierung", ToolState.ALPHA) {

  override type PartType       = ProgExPart
  override type ExContentType  = ProgExerciseContent
  override type SolType        = ProgSolution
  override type CompResultType = ProgCompleteResult

  // Other members

  override val exParts: Seq[ProgExPart] = ProgExPart.values

  // Yaml, Html Forms, Json

  override val toolJsonProtocol: ToolJsonProtocol[ProgExerciseContent, ProgSolution, ProgExPart] =
    ProgrammingToolJsonProtocol

  override val graphQlModels: ToolGraphQLModelBasics[ProgExerciseContent, ProgSolution, ProgExPart] =
    ProgrammingGraphQLModels

  // Correction

  override protected def correctEx(
    user: User,
    sol: ProgSolution,
    collection: ExerciseCollection,
    exercise: Exercise,
    content: ProgExerciseContent,
    part: ProgExPart,
    solutionSaved: Boolean
  )(implicit ec: ExecutionContext): Future[Try[ProgCompleteResult]] =
    ProgCorrector.correct(user, sol, exercise, content, part, solutionSaved)

}
