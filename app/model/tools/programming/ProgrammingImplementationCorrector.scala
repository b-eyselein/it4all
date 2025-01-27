package model.tools.programming

import better.files.File
import model.FilesSolution
import model.core.DockerBind
import model.points._
import model.tools.programming.ProgrammingToolJsonProtocol.implementationCorrectionResultReads

import scala.concurrent.{ExecutionContext, Future}

trait ProgrammingImplementationCorrector extends ProgrammingAbstractCorrector {

  def correctImplementationPart(
    solutionFilesMounts: Seq[DockerBind],
    solTargetDir: File,
    exerciseContent: ProgrammingExerciseContent,
    resultFile: File
  )(implicit ec: ExecutionContext): Future[ProgrammingResult] = exerciseContent.sampleSolutions.headOption match {
    case None => Future.failed(new Exception("No sample solution found!"))
    case Some(FilesSolution(files)) =>
      val maybeTestFileContent = files
        .find(_.name == exerciseContent.unitTestPart.testFileName)
        .map(_.content)

      maybeTestFileContent match {
        case None => Future.failed(new Exception("No content for unit test file found!"))
        case Some(unitTestFileContent) =>
          val unitTestFileName = s"${exerciseContent.filename}_test.py"

          val unitTestFile = solTargetDir / unitTestFileName
          unitTestFile
            .createIfNotExists(createParents = true)
            .write(unitTestFileContent)

          val unitTestFileMount = DockerBind(unitTestFile, baseBindPath / unitTestFileName, isReadOnly = true)

          runContainer(
            solutionFilesMounts :+ unitTestFileMount,
            implementationCorrectionResultReads,
            resultFile,
            maybeCmd = Some(Seq("normal")),
            convertResult = (normalExecutionResult: ImplementationCorrectionResult) => {

              val theMaxPoints = maxPoints(exerciseContent.unitTestPart)

              ProgrammingResult(
                implementationCorrectionResult = Some(normalExecutionResult),
                points = if (normalExecutionResult.successful) theMaxPoints else zeroPoints,
                maxPoints = theMaxPoints
              )
            }
          )
      }
  }

}
