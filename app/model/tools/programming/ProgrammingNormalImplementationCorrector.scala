package model.tools.programming

import better.files.File
import model.FilesSolution
import model.core.DockerBind
import model.points._
import model.tools.programming.ProgrammingToolJsonProtocol.normalExecutionResultFileJsonReads

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Try}

trait ProgrammingNormalImplementationCorrector extends ProgrammingAbstractCorrector {

  def correctNormalImplementation(
    solutionFilesMounts: Seq[DockerBind],
    solTargetDir: File,
    exerciseContent: ProgrammingExerciseContent,
    normalUnitTestPart: NormalUnitTestPart,
    resultFile: File
  )(implicit ec: ExecutionContext): Future[Try[ProgrammingAbstractResult]] =
    exerciseContent.sampleSolutions.headOption match {
      case None => Future.successful(Failure(new Exception("No sample solution found!")))
      case Some(FilesSolution(files)) =>
        val maybeTestFileContent = files
          .find(_.name == normalUnitTestPart.testFileName)
          .map(_.content)

        maybeTestFileContent match {
          case None => Future.successful(Failure(new Exception("No content for unit test file found!")))
          case Some(unitTestFileContent) =>
            val unitTestFileName = s"${exerciseContent.filename}_test.py"

            val unitTestFile = solTargetDir / unitTestFileName
            unitTestFile
              .createIfNotExists(createParents = true)
              .write(unitTestFileContent)

            val unitTestFileMount = DockerBind(unitTestFile, baseBindPath / unitTestFileName, isReadOnly = true)

            runContainer(
              solutionFilesMounts :+ unitTestFileMount,
              normalExecutionResultFileJsonReads,
              resultFile,
              maybeCmd = Some(Seq("normal"))
            )(normalExecutionResult =>
              //FIXME: points!
              ProgrammingResult(
                normalResult = Some(normalExecutionResult),
                points = (-1).points /* normalUnitTestPart.unitTestTestConfigs.size.points */,
                maxPoints = maxPoints(normalUnitTestPart)
              )
            )
        }
    }

}
