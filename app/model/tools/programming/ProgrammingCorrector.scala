package model.tools.programming

import better.files._
import model.FilesSolution
import model.core.DockerBind
import model.tools.programming.ProgrammingTool.ProgrammingExercise

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object ProgrammingCorrector extends ProgrammingUnitTestCorrector with ProgrammingNormalImplementationCorrector {

  def correct(
    exercise: ProgrammingExercise,
    solution: FilesSolution,
    solutionTargetDir: File,
    part: ProgExPart
  )(implicit ec: ExecutionContext): Future[Try[ProgrammingResult]] = {

    // Create or truncate result file
    val resultFile = solutionTargetDir / resultFileName
    resultFile.createIfNotExists(createParents = true).clear()

    val resultFileMount = DockerBind(resultFile, baseBindPath / resultFileName)

    // Mount other files
    val solutionFileMounts = solution.files
      .map { ef =>
        val targetPath = solutionTargetDir / ef.name

        targetPath
          .createIfNotExists(createParents = true)
          .write(ef.content)

        DockerBind(targetPath, baseBindPath / ef.name)
      }

    val defaultFileMounts = solutionFileMounts :+ resultFileMount

    part match {
      case ProgExPart.TestCreation =>
        correctUnitTestPart(defaultFileMounts, solutionTargetDir, exercise.content, resultFile)
      case _ => correctImplementationPart(defaultFileMounts, solutionTargetDir, exercise.content, resultFile)
    }
  }

}
