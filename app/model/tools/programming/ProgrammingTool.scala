package model.tools.programming

import initialData.InitialData
import initialData.programming.ProgrammingInitialData
import model._
import model.graphql.FilesSolutionToolGraphQLModelBasics
import model.tools.{FilesSolutionToolJsonProtocol, ToolWithParts}

import scala.concurrent.{ExecutionContext, Future}

object ProgrammingTool extends ToolWithParts("programming", "Programmierung", true) {

  override type SolInputType = FilesSolutionInput
  override type ExContType     = ProgrammingExerciseContent
  override type PartType          = ProgExPart
  override type ResType           = ProgrammingResult

  type ProgrammingExercise = Exercise[ProgrammingExerciseContent]

  // Yaml, Html Forms, Json

  override val jsonFormats: FilesSolutionToolJsonProtocol[ProgrammingExerciseContent, ProgExPart] = ProgrammingToolJsonProtocol

  override val graphQlModels: FilesSolutionToolGraphQLModelBasics[ProgrammingExerciseContent, ProgrammingResult, ProgExPart] = ProgrammingGraphQLModels

  // Correction

  override def correctAbstract(
    user: User,
    solution: FilesSolutionInput,
    exercise: ProgrammingExercise,
    part: ProgExPart
  )(implicit ec: ExecutionContext): Future[ProgrammingResult] = {

    val solTargetDir = solutionDirForExercise(user.username, exercise.collectionId, exercise.exerciseId) / part.id

    ProgrammingCorrector.correct(exercise, solution, solTargetDir, part)
  }

  override val initialData: InitialData[ProgrammingExerciseContent] = ProgrammingInitialData.initialData

}
