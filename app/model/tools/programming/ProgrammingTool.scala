package model.tools.programming

import better.files.File
import model.User
import model.tools._

import scala.concurrent.{ExecutionContext, Future}

object ProgrammingTool extends CollectionTool("programming", "Programmierung", ToolState.ALPHA) {

  override type SolType       = ProgSolution
  override type ExContentType = ProgrammingExerciseContent
  override type PartType      = ProgExPart
  override type ResType       = ProgrammingAbstractResult

  // Yaml, Html Forms, Json

  override val toolJsonProtocol: ToolJsonProtocol[ProgSolution, ProgrammingExerciseContent, ProgExPart] =
    ProgrammingToolJsonProtocol

  override val graphQlModels
    : ToolGraphQLModelBasics[ProgSolution, ProgrammingExerciseContent, ProgExPart, ProgrammingAbstractResult] =
    ProgrammingGraphQLModels

  override val allTopics: Seq[Topic] = ProgrammingTopics.values

  // Correction

  override def correctAbstract(
    user: User,
    solution: ProgSolution,
    exercise: Exercise[ProgSolution, ProgrammingExerciseContent],
    part: ProgExPart,
    solutionSaved: Boolean
  )(implicit ec: ExecutionContext): Future[ProgrammingAbstractResult] = {

    val solutionTargetDir: File = solutionDirForExercise(user.username, exercise.collectionId, exercise.exerciseId) / part.id

    // Create or truncate result file
    val resultFile = solutionTargetDir / ProgrammingNormalImplementationCorrector.resultFileName

    resultFile
      .createIfNotExists(createParents = true)
      .clear()

    part match {
      case ProgExPart.TestCreation =>
        ProgrammingUnitTestCorrector
          .correctUnitTestPart(solutionTargetDir, solution, exercise.content, resultFile, solutionSaved)
      case _ =>
        val programmingSolutionFilesMounts = solution.files.map { exerciseFile: ExerciseFile =>
          ProgrammingNormalImplementationCorrector.writeExerciseFileAndMount(exerciseFile, solutionTargetDir)
        }

        exercise.content.unitTestPart.unitTestType match {
          case UnitTestTypes.Simplified =>
            ProgrammingSimpleImplementationCorrector.correctSimplifiedImplementation(
              solutionTargetDir,
              exercise.content,
              programmingSolutionFilesMounts,
              resultFile,
              solutionSaved
            )
          case UnitTestTypes.Normal =>
            ProgrammingNormalImplementationCorrector.correctNormalImplementation(
              solutionTargetDir,
              exercise.content,
              programmingSolutionFilesMounts,
              solutionSaved
            )
        }
    }
  }

}
