package model.tools.collectionTools.programming

import model.User
import model.tools.collectionTools._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object ProgToolMain extends CollectionToolMain(ProgConsts) {

  override type PartType       = ProgExPart
  override type ExContentType  = ProgExerciseContent
  override type SolType        = ProgSolution
  override type CompResultType = ProgCompleteResult

  // Other members

  override val exParts: Seq[ProgExPart] = ProgExParts.values

  // Yaml, Html Forms, Json

  override val toolJsonProtocol: ToolJsonProtocol[ProgExerciseContent, ProgSolution, ProgCompleteResult] =
    ProgrammingToolJsonProtocol

  override val graphQlModels: ToolGraphQLModelBasics[ProgExerciseContent, ProgSolution] = ProgrammingGraphQLModels

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
