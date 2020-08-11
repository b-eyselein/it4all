package model.tools.programming

import initialData.InitialData
import initialData.programming.ProgrammingInitialData
import model.graphql.ToolGraphQLModelBasics
import model.tools.{Tool, ToolJsonProtocol, ToolState}
import model.{Exercise, LoggedInUser, Topic}

import scala.concurrent.{ExecutionContext, Future}

object ProgrammingTool extends Tool("programming", "Programmierung", ToolState.ALPHA) {

  override type SolType       = ProgSolution
  override type ExContentType = ProgrammingExerciseContent
  override type PartType      = ProgExPart
  override type ResType       = ProgrammingAbstractResult

  type ProgrammingExercise = Exercise[ProgrammingExerciseContent]

  // Yaml, Html Forms, Json

  override val jsonFormats: ToolJsonProtocol[ProgSolution, ProgrammingExerciseContent, ProgExPart] =
    ProgrammingToolJsonProtocol

  override val graphQlModels
    : ToolGraphQLModelBasics[ProgSolution, ProgrammingExerciseContent, ProgExPart, ProgrammingAbstractResult] =
    ProgrammingGraphQLModels

  override val allTopics: Seq[Topic] = ProgrammingTopics.values

  // Correction

  override def correctAbstract(
    user: LoggedInUser,
    solution: ProgSolution,
    exercise: ProgrammingExercise,
    part: ProgExPart
  )(implicit ec: ExecutionContext): Future[ProgrammingAbstractResult] = {

    val solTargetDir = solutionDirForExercise(user.username, exercise.collectionId, exercise.exerciseId) / part.id

    ProgrammingCorrector.correct(exercise, solution, solTargetDir, part)
  }

  override val initialData: InitialData[ProgrammingExerciseContent] = ProgrammingInitialData

}
