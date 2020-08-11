package model.tools.programming

import better.files.File
import model.core.{DockerBind, DockerConnector, RunContainerResult}
import model.points._
import model.tools.programming.ProgrammingToolJsonProtocol.UnitTestTestData
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

trait ProgrammingUnitTestCorrector extends ProgrammingAbstractCorrector {

  protected def correctUnitTestPart(
    defaultFileMounts: Seq[DockerBind],
    solTargetDir: File,
    exerciseContent: ProgrammingExerciseContent,
    unitTestPart: NormalUnitTestPart,
    resultFile: File
  )(implicit ec: ExecutionContext): Future[ProgrammingAbstractResult] = {

    val maxPoints = unitTestPart.unitTestTestConfigs.size.points

    // write test data file

    val unitTestTestData = UnitTestTestData(
      folderName = unitTestPart.folderName,
      filename = exerciseContent.filename,
      // remove ending '.py'
      testFilename = unitTestPart.testFileName.substring(0, unitTestPart.testFileName.length - 3),
      testConfigs = unitTestPart.unitTestTestConfigs
    )

    val testDataFile = solTargetDir / testDataFileName
    testDataFile
      .createIfNotExists(createParents = true)
      .write(Json.stringify(ProgrammingToolJsonProtocol.unitTestDataWrites.writes(unitTestTestData)))

    val testDataFileMount = DockerBind(testDataFile, baseBindPath / testDataFileName, isReadOnly = true)

    // find mounts for implementation and exercise files

    val bindDirectory = baseBindPath / unitTestPart.folderName

    val unitTestSolFilesDockerBinds: Seq[DockerBind] = unitTestPart.unitTestTestConfigs
      .filter { tc => implFileRegex.matches(tc.file.name) }
      .map {
        case UnitTestTestConfig(_, _, _, ef) =>
          val targetPath = writeExerciseFileToDirectory(ef, solTargetDir)
          DockerBind(targetPath, bindDirectory / ef.name)
      }

    val exFilesMounts = unitTestPart.unitTestFiles
      .filter(ef => ef.name != unitTestPart.testFileName && ef.name != exerciseContent.implementationPart.implFileName)
      .map { ef =>
        val targetPath = writeExerciseFileToDirectory(ef, solTargetDir)

        DockerBind(targetPath, bindDirectory / ef.name)
      }

    val allDockerBinds = defaultFileMounts ++ unitTestSolFilesDockerBinds ++ exFilesMounts :+ testDataFileMount

    DockerConnector
      .runContainer(
        imageName = programmingCorrectionDockerImage.name,
        maybeDockerBinds = allDockerBinds,
        maybeCmd = Some(Seq("unit_test"))
      )
      .map {
        case Failure(exception) =>
          onError("Error running programming unit test correction image", maxPoints, Some(exception))
        case Success(RunContainerResult(statusCode, logs)) =>
          if (statusCode != 0) {
            ProgrammingInternalErrorResult(logs, maxPoints)
          } else {
            ResultsFileJsonFormat
              .readTestCorrectionResultFile(resultFile)
              .fold(
                exception => onError("Error reading unit test correction result file", maxPoints, Some(exception)),
                results =>
                  ProgrammingResult(
                    unitTestResults = results,
                    points = results.count(_.successful).points,
                    maxPoints = maxPoints
                  )
              )
          }
      }

  }

}
