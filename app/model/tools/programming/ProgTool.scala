package model.tools.programming

import better.files.File
import model.User
import model.tools._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object ProgTool extends CollectionTool("programming", "Programmierung", ToolState.ALPHA) {

  override type SolType        = ProgSolution
  override type ExContentType  = ProgrammingExerciseContent
  override type PartType       = ProgExPart
  override type CompResultType = ProgrammingAbstractResult

  // Yaml, Html Forms, Json

  override val toolJsonProtocol: ToolJsonProtocol[ProgSolution, ProgrammingExerciseContent, ProgExPart] =
    ProgrammingToolJsonProtocol

  override val graphQlModels: ToolGraphQLModelBasics[ProgSolution, ProgrammingExerciseContent, ProgExPart] =
    ProgrammingGraphQLModels

  override val allTopics: Seq[Topic] = ProgrammingTopics.values

  // Correction

  override def correctAbstract(
    user: User,
    solution: ProgSolution,
    exercise: Exercise[ProgSolution, ProgrammingExerciseContent],
    part: ProgExPart,
    solutionSaved: Boolean
  )(implicit ec: ExecutionContext): Future[Try[ProgrammingAbstractResult]] = {

    val solutionTargetDir: File = solutionDirForExercise(user.username, exercise.collectionId, exercise.id) / part.id

    ProgCorrector.correct(solution, exercise, part, solutionTargetDir, solutionSaved)
  }

}
