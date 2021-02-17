package model.tools.programming

import better.files.File
import model.FilesSolution
import model.core.{DockerBind, ScalaDockerImage}
import model.tools.programming.ProgrammingTool.ProgrammingExercise

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object ProgrammingCorrector
    extends ProgrammingSimpleImplementationCorrector
    with ProgrammingUnitTestCorrector
    with ProgrammingNormalImplementationCorrector {

  override protected val dockerImage: ScalaDockerImage = programmingCorrectionDockerImage

  def correct(
    exercise: ProgrammingExercise,
    solution: FilesSolution,
    solutionTargetDir: File,
    part: ProgExPart
  )(implicit ec: ExecutionContext): Future[Try[ProgrammingAbstractResult]] = {

    // Create or truncate result file
    val resultFile = solutionTargetDir / resultFileName
    resultFile.createIfNotExists(createParents = true).clear()

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
              normalUnitTestPart,
              resultFile
            )
        }
    }
  }

}
