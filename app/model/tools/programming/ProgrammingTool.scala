package model.tools.programming

import initialData.InitialData
import initialData.programming.ProgrammingInitialData
import model._
import model.graphql.ToolGraphQLModelBasics
import model.tools.{Tool, ToolJsonProtocol, ToolState}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object ProgrammingTool extends Tool("programming", "Programmierung", ToolState.BETA) {

  override type SolutionType      = FilesSolution
  override type SolutionInputType = FilesSolutionInput
  override type ExContentType     = ProgrammingExerciseContent
  override type PartType          = ProgExPart
  override type ResType           = ProgrammingResult

  type ProgrammingExercise = Exercise[ProgrammingExerciseContent]

  // Yaml, Html Forms, Json

  override val jsonFormats
    : ToolJsonProtocol[FilesSolution, FilesSolutionInput, ProgrammingExerciseContent, ProgExPart] =
    ProgrammingToolJsonProtocol

  override val graphQlModels
    : ToolGraphQLModelBasics[FilesSolutionInput, ProgrammingExerciseContent, ProgExPart, ProgrammingResult] =
    ProgrammingGraphQLModels

  override val allTopics: Seq[Topic] = ProgrammingTopics.values

  // Correction

  override def correctAbstract(
    user: LoggedInUser,
    solution: FilesSolutionInput,
    exercise: ProgrammingExercise,
    part: ProgExPart
  )(implicit ec: ExecutionContext): Future[Try[ProgrammingResult]] = {

    val solTargetDir = solutionDirForExercise(user.username, exercise.collectionId, exercise.exerciseId) / part.id

    ProgrammingCorrector.correct(exercise, solution, solTargetDir, part)
  }

  override val initialData: InitialData[ProgrammingExerciseContent] = ProgrammingInitialData

}
