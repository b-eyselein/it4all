package model.tools.programming

import initialData.InitialData
import initialData.programming.ProgrammingInitialData
import model.graphql.ToolGraphQLModelBasics
import model.tools.{Tool, ToolJsonProtocol, ToolState}
import model.{Exercise, FilesSolution, LoggedInUser, Topic}

import scala.concurrent.{ExecutionContext, Future}

object ProgrammingTool extends Tool("programming", "Programmierung", ToolState.BETA) {

  override type SolType       = FilesSolution
  override type ExContentType = ProgrammingExerciseContent
  override type PartType      = ProgExPart
  override type ResType       = ProgrammingAbstractResult

  type ProgrammingExercise = Exercise[ProgrammingExerciseContent]

  // Yaml, Html Forms, Json

  override val jsonFormats: ToolJsonProtocol[FilesSolution, ProgrammingExerciseContent, ProgExPart] =
    ProgrammingToolJsonProtocol

  override val graphQlModels
    : ToolGraphQLModelBasics[FilesSolution, ProgrammingExerciseContent, ProgExPart, ProgrammingAbstractResult] =
    ProgrammingGraphQLModels

  override val allTopics: Seq[Topic] = ProgrammingTopics.values

  // Correction

  override def correctAbstract(
    user: LoggedInUser,
    solution: FilesSolution,
    exercise: ProgrammingExercise,
    part: ProgExPart
  )(implicit ec: ExecutionContext): Future[ProgrammingAbstractResult] = {

    val solTargetDir = solutionDirForExercise(user.username, exercise.collectionId, exercise.exerciseId) / part.id

    ProgrammingCorrector.correct(exercise, solution, solTargetDir, part)
  }

  override val initialData: InitialData[ProgrammingExerciseContent] = ProgrammingInitialData

}
