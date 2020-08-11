package model.tools.programming

import better.files.File
import model.SampleSolution
import model.core.{DockerBind, DockerConnector, RunContainerResult, ScalaDockerImage}
import model.points._
import play.api.Logger

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object ProgrammingNormalImplementationCorrector extends ProgrammingAbstractCorrector {

  override protected val logger: Logger = Logger(ProgrammingNormalImplementationCorrector.getClass)

  val programmingNormalCorrectionDockerImage: ScalaDockerImage =
    ScalaDockerImage("ls6uniwue", "py_normal_prog_corrector", "0.3.1")

  def correctNormalImplementation(
    solutionFilesMounts: Seq[DockerBind],
    solTargetDir: File,
    exerciseContent: ProgrammingExerciseContent,
    normalUnitTestPart: NormalUnitTestPart
  )(implicit ec: ExecutionContext): Future[ProgrammingAbstractResult] = {

    val maxPoints = normalUnitTestPart.unitTestTestConfigs.size.points


    exerciseContent.sampleSolutions.headOption match {
      case None => Future.successful(onError("No sample solution found!", maxPoints))
      case Some(SampleSolution(_, ProgSolution(files))) =>
        val maybeTestFileContent = files
          .find(_.name == normalUnitTestPart.testFileName)
          .map(_.content)

        maybeTestFileContent match {
          case None => Future.successful(onError("No content for unit test file found!", maxPoints))
          case Some(unitTestFileContent) =>
            val unitTestFileName = s"${exerciseContent.filename}_test.py"

            val unitTestFile = solTargetDir / unitTestFileName
            unitTestFile
              .createIfNotExists(createParents = true)
              .write(unitTestFileContent)

            val unitTestFileMount = DockerBind(unitTestFile, baseBindPath / unitTestFileName, isReadOnly = true)

            DockerConnector
              .runContainer(
                programmingNormalCorrectionDockerImage.name,
                maybeDockerBinds = solutionFilesMounts :+ unitTestFileMount,
                deleteContainerAfterRun = false
              )
              .map {
                case Failure(exception) => onError("Error while running docker container", maxPoints, Some(exception))
                case Success(RunContainerResult(statusCode, logs)) =>
                  ProgrammingResult(
                    normalResult = Some(NormalExecutionResult(statusCode == 0, logs)),
                    points = normalUnitTestPart.unitTestTestConfigs.size.points,
                    maxPoints = maxPoints
                  )
              }
        }
    }
  }

}
