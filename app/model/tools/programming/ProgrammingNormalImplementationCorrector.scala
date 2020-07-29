package model.tools.programming

import better.files.File
import model.SampleSolution
import model.core.{DockerBind, DockerConnector, ScalaDockerImage}
import model.result.SuccessType
import play.api.Logger

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object ProgrammingNormalImplementationCorrector extends ProgrammingAbstractCorrector {

  override protected val logger: Logger = Logger(ProgrammingNormalImplementationCorrector.getClass)

  val programmingNormalCorrectionDockerImage: ScalaDockerImage =
    ScalaDockerImage("ls6uniwue", "py_normal_prog_corrector", "0.3.1")

  def correctNormalImplementation(
    solTargetDir: File,
    exerciseContent: ProgrammingExerciseContent,
    normalUnitTestPart: NormalUnitTestPart,
    programmingSolutionFilesMounts: Seq[DockerBind]
  )(implicit ec: ExecutionContext): Future[ProgrammingAbstractResult] =
    exerciseContent.sampleSolutions.headOption match {
      case None => Future.successful(onError("No sample solution found!"))
      case Some(SampleSolution(_, ProgSolution(files))) =>
        files.find(_.name == normalUnitTestPart.testFileName).map(_.content) match {
          case None => Future.successful(onError("No content for unit test file found!"))
          case Some(unitTestFileContent) =>
            val unitTestFileName = s"${exerciseContent.filename}_test.py"
            val unitTestFile     = solTargetDir / unitTestFileName
            createFileAndWrite(unitTestFile, unitTestFileContent)

            val unitTestFileMount =
              DockerBind(unitTestFile, s"${DockerConnector.DefaultWorkingDir}/$unitTestFileName}", isReadOnly = true)

            DockerConnector
              .runContainer(
                programmingNormalCorrectionDockerImage.name,
                maybeDockerBinds = programmingSolutionFilesMounts :+ unitTestFileMount,
                deleteContainerAfterRun = false
              )
              .map {
                case Failure(exception) =>
                  onError("Error while running docker container", maybeException = Some(exception))
                case Success(runContainerResult) =>
                  val successType = if (runContainerResult.statusCode == 0) SuccessType.COMPLETE else SuccessType.ERROR

                  ProgrammingResult(normalResult = Some(NormalExecutionResult(successType, runContainerResult.logs)))
              }
        }
    }

}
