package model.tools.programming

import better.files.File
import model.core.DockerBind
import model.points._
import model.tools.programming.ProgrammingToolJsonProtocol.{UnitTestTestData, unitTestCorrectionResultReads}
import play.api.libs.json.{Json, Reads}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

trait ProgrammingUnitTestCorrector extends ProgrammingAbstractCorrector {

  def correctUnitTestPart(
    defaultFileMounts: Seq[DockerBind],
    solTargetDir: File,
    exerciseContent: ProgrammingExerciseContent,
    resultFile: File
  )(implicit ec: ExecutionContext): Future[Try[ProgrammingResult]] = {

    val unitTestPart: UnitTestPart = exerciseContent.unitTestPart

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
      .map { case UnitTestTestConfig(_, _, ef, _) =>
        val targetPath = ef.writeOrCopyToDirectory(solTargetDir)

        DockerBind(targetPath, bindDirectory / ef.name)
      }

    val exFilesMounts = unitTestPart.unitTestFiles
      .filter(ef => ef.name != unitTestPart.testFileName && ef.name != exerciseContent.implementationPart.implFileName)
      .map { ef =>
        val targetPath = ef.writeOrCopyToDirectory(solTargetDir)

        DockerBind(targetPath, bindDirectory / ef.name)
      }

    val allDockerBinds = defaultFileMounts ++ unitTestSolFilesDockerBinds ++ exFilesMounts :+ testDataFileMount

    runContainer(
      allDockerBinds,
      Reads.seq(unitTestCorrectionResultReads),
      resultFile,
      maybeCmd = Some(Seq("unit_test")),
      deleteContainerAfterRun = _ == 0
    )(results =>
      ProgrammingResult(
        unitTestResults = results,
        points = results.count(_.successful).points,
        maxPoints = maxPoints(unitTestPart)
      )
    )
  }

}
