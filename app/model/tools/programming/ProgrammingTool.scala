package model.tools.programming

import better.files.File
import initialData.InitialData
import initialData.programming.ProgrammingInitialData
import model.graphql.ToolGraphQLModelBasics
import model.tools._
import model.{Exercise, ExerciseFile, LoggedInUser, Topic}

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

    val solutionTargetDir: File =
      solutionDirForExercise(user.username, exercise.collectionId, exercise.exerciseId) / part.id

    // Create or truncate result file
    val resultFile = solutionTargetDir / ProgrammingNormalImplementationCorrector.resultFileName

    resultFile
      .createIfNotExists(createParents = true)
      .clear()

    part match {
      case ProgExPart.TestCreation =>
        ProgrammingUnitTestCorrector.correctUnitTestPart(solutionTargetDir, solution, exercise.content, resultFile)
      case _ =>
        val programmingSolutionFilesMounts = solution.files.map { exerciseFile: ExerciseFile =>
          ProgrammingNormalImplementationCorrector.writeExerciseFileAndMount(exerciseFile, solutionTargetDir)
        }

        exercise.content.unitTestPart.unitTestType match {
          case UnitTestType.Simplified =>
            ProgrammingSimpleImplementationCorrector.correctSimplifiedImplementation(
              solutionTargetDir,
              exercise.content,
              programmingSolutionFilesMounts,
              resultFile
            )
          case UnitTestType.Normal =>
            ProgrammingNormalImplementationCorrector.correctNormalImplementation(
              solutionTargetDir,
              exercise.content,
              programmingSolutionFilesMounts
            )
        }
    }
  }

  override val initialData: InitialData[ProgrammingExerciseContent] = ProgrammingInitialData

}
