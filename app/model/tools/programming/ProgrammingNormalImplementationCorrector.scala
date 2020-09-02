package model.tools.programming

import better.files.File
import model.SampleSolution
import model.core.{DockerBind, DockerConnector, RunContainerResult}
import model.points._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait ProgrammingNormalImplementationCorrector extends ProgrammingAbstractCorrector {

  def correctNormalImplementation(
    solutionFilesMounts: Seq[DockerBind],
    solTargetDir: File,
    exerciseContent: ProgrammingExerciseContent,
    normalUnitTestPart: NormalUnitTestPart
  )(implicit ec: ExecutionContext): Future[ProgrammingAbstractResult] = {

    val mp = maxPoints(normalUnitTestPart)

    exerciseContent.sampleSolutions.headOption match {
      case None => Future.successful(onError("No sample solution found!", mp))
      case Some(SampleSolution(_, ProgSolution(files))) =>
        val maybeTestFileContent = files
          .find(_.name == normalUnitTestPart.testFileName)
          .map(_.content)

        maybeTestFileContent match {
          case None => Future.successful(onError("No content for unit test file found!", mp))
          case Some(unitTestFileContent) =>
            val unitTestFileName = s"${exerciseContent.filename}_test.py"

            val unitTestFile = solTargetDir / unitTestFileName
            unitTestFile
              .createIfNotExists(createParents = true)
              .write(unitTestFileContent)

            val unitTestFileMount = DockerBind(unitTestFile, baseBindPath / unitTestFileName, isReadOnly = true)

            DockerConnector
              .runContainer(
                programmingCorrectionDockerImage.name,
                maybeDockerBinds = solutionFilesMounts :+ unitTestFileMount,
                maybeCmd = Some(Seq("normal"))
              )
              .map {
                case Failure(exception) => onError("Error while running docker container", mp, Some(exception))
                case Success(RunContainerResult(statusCode, logs)) =>
                  ProgrammingResult(
                    normalResult = Some(NormalExecutionResult(statusCode == 0, logs)),
                    points = normalUnitTestPart.unitTestTestConfigs.size.points,
                    maxPoints = mp
                  )
              }
        }
    }
  }

}
