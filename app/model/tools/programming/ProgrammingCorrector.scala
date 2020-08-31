package model.tools.programming

import better.files.File
import model.core.DockerBind
import model.tools.programming.ProgrammingTool.ProgrammingExercise
import play.api.Logger

import scala.concurrent.{ExecutionContext, Future}

object ProgrammingCorrector
    extends ProgrammingSimpleImplementationCorrector
    with ProgrammingUnitTestCorrector
    with ProgrammingNormalImplementationCorrector {

  override protected val logger: Logger = Logger(ProgrammingCorrector.getClass)

  def correct(
    exercise: ProgrammingExercise,
    solution: ProgSolution,
    solutionTargetDir: File,
    part: ProgExPart
  )(implicit ec: ExecutionContext): Future[ProgrammingAbstractResult] = {

    // Create or truncate result file
    val resultFile = solutionTargetDir / resultFileName
    resultFile
      .createIfNotExists(createParents = true)
      .clear()

    val defaultFileMounts = solution.files
      .map { ef =>
        val targetPath = writeExerciseFileToDirectory(ef, solutionTargetDir)
        DockerBind(targetPath, baseBindPath / ef.name)
      } :+ DockerBind(resultFile, baseBindPath / resultFileName)

    part match {
      case ProgExPart.TestCreation =>
        exercise.content.unitTestPart match {
          case normalUnitTestPart: NormalUnitTestPart =>
            correctUnitTestPart(
              defaultFileMounts,
              solutionTargetDir,
              exercise.content,
              normalUnitTestPart,
              resultFile
            )
          case _ => ???
        }
      case _ =>
        exercise.content.unitTestPart match {
          case simplifiedUnitTestPart: SimplifiedUnitTestPart =>
            correctSimplifiedImplementation(
              defaultFileMounts,
              solutionTargetDir,
              simplifiedUnitTestPart,
              resultFile
            )
          case normalUnitTestPart: NormalUnitTestPart =>
            correctNormalImplementation(
              defaultFileMounts,
              solutionTargetDir,
              exercise.content,
              normalUnitTestPart
            )
        }
    }
  }

}
