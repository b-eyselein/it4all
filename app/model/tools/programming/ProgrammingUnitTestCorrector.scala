package model.tools.programming

import better.files.File
import model.core.{DockerBind, DockerConnector, ScalaDockerImage}
import model.points._
import model.tools.programming.ProgrammingToolJsonProtocol.UnitTestTestData
import play.api.Logger
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object ProgrammingUnitTestCorrector extends ProgrammingAbstractCorrector {

  override protected val logger: Logger = Logger(ProgrammingUnitTestCorrector.getClass)

  val programmingUnitTestCorrectionDockerImageName: ScalaDockerImage =
    ScalaDockerImage("ls6uniwue", "py_unit_test_corrector", "0.4.1")

  def correctUnitTestPart(
    solTargetDir: File,
    solution: ProgSolution,
    exerciseContent: ProgrammingExerciseContent,
    unitTestPart: NormalUnitTestPart,
    resultFile: File
  )(implicit ec: ExecutionContext): Future[ProgrammingAbstractResult] = {

    val maxPoints = unitTestPart.unitTestTestConfigs.size.points

    solution.files.find(_.name == unitTestPart.testFileName) match {
      case None               => Future.successful(onError("Could not find test main file", maxPoints))
      case Some(testMainFile) =>
        // write unit test file
        val testFileName = unitTestPart.testFileName
        val testFile     = solTargetDir / testFileName
        createFileAndWrite(testFile, testMainFile.content)

        // write test data file
        val testDataFile = solTargetDir / testDataFileName

        val unitTestTestData = UnitTestTestData(
          unitTestPart.folderName,
          exerciseContent.filename,
          // remove ending '.py'
          unitTestPart.testFileName.substring(0, unitTestPart.testFileName.length - 3),
          unitTestPart.unitTestTestConfigs
        )

        createFileAndWrite(
          testDataFile,
          Json.stringify(ProgrammingToolJsonProtocol.unitTestDataWrites.writes(unitTestTestData))
        )

        val bindDirectory = s"${DockerConnector.DefaultWorkingDir}/${unitTestPart.folderName}"

        // find mounts for implementation files
        val unitTestSolFilesDockerBinds: Seq[DockerBind] = unitTestPart.unitTestTestConfigs
          .filter(tc => implFileRegex.matches(tc.file.name))
          .map { tc => writeExerciseFileAndMount(tc.file, solTargetDir, bindDirectory) }

        // find mounts for exercise files
        val exFilesMounts = unitTestPart.unitTestFiles
          .filter(f => f.name != testFileName && f.name != exerciseContent.implementationPart.implFileName)
          .map { exFile => writeExerciseFileAndMount(exFile, solTargetDir, bindDirectory) }

        val defaultBinds = Seq(
          DockerBind(testFile, s"$bindDirectory/$testFileName", isReadOnly = true),
          DockerBind(testDataFile, s"${DockerConnector.DefaultWorkingDir}/$testDataFileName", isReadOnly = true),
          DockerBind(resultFile, s"${DockerConnector.DefaultWorkingDir}/$resultFileName")
        )

        val dockerBinds = unitTestSolFilesDockerBinds ++ exFilesMounts ++ defaultBinds

        DockerConnector
          .runContainer(imageName = programmingUnitTestCorrectionDockerImageName.name, maybeDockerBinds = dockerBinds)
          .map {
            case Failure(exception) =>
              onError("Error running programming unit test correction image", maxPoints, Some(exception))
            case Success(_) =>
              ResultsFileJsonFormat
                .readTestCorrectionResultFile(resultFile)
                .fold(
                  exception => onError("Error reading unit test correction result file", maxPoints, Some(exception)),
                  results => {
                    val points = ???

                    ProgrammingResult(unitTestResults = results, points = points, maxPoints = maxPoints)
                  }
                )
          }
    }
  }

}
