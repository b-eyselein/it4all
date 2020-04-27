package model.tools.programming

import better.files.File
import better.files.File._
import model.core.{DockerBind, DockerConnector, ScalaDockerImage}
import model.tools.programming.ProgrammingToolJsonProtocol.UnitTestTestData
import play.api.Logger
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object ProgrammingUnitTestCorrector extends ProgrammingAbstractCorrector {

  override protected val logger: Logger = Logger(ProgrammingUnitTestCorrector.getClass)

  val programmingUnitTestCorrectionDockerImageName: ScalaDockerImage =
    ScalaDockerImage("ls6uniwue", "py_unit_test_corrector", "0.3.1")

  def correctUnitTestPart(
    solTargetDir: File,
    solution: ProgSolution,
    exerciseContent: ProgrammingExerciseContent,
    resultFile: File,
    solutionSaved: Boolean
  )(implicit ec: ExecutionContext): Future[ProgrammingAbstractResult] = {

    // write unit test file
    val testFileName = exerciseContent.unitTestPart.testFileName
    val testFile     = solTargetDir / testFileName
    createFileAndWrite(testFile, solution.files.find(_.name == testFileName).map(_.content).getOrElse(???))

    // write test data file
    val testDataFile = solTargetDir / testDataFileName
    // remove ending '.py'
    val testFileNameForTestData = exerciseContent.unitTestPart.testFileName
      .substring(0, exerciseContent.unitTestPart.testFileName.length - 3)

    createFileAndWrite(
      testDataFile,
      Json.prettyPrint(
        ProgrammingToolJsonProtocol.unitTestDataWrites.writes(
          UnitTestTestData(
            exerciseContent.foldername,
            exerciseContent.filename,
            testFileNameForTestData,
            exerciseContent.unitTestPart.unitTestTestConfigs
          )
        )
      )
    )

    // find mounts for implementation files
    val unitTestSolFilesDockerBinds: Seq[DockerBind] = exerciseContent.unitTestPart.unitTestTestConfigs
      .filter(tc => implFileRegex.matches(tc.file.name))
      .map { tc =>
        writeExerciseFileAndMount(
          tc.file,
          solTargetDir,
          DockerConnector.DefaultWorkingDir / exerciseContent.foldername
        )
      }

    // find mounts for exercise files
    val exFilesMounts = exerciseContent.unitTestPart.unitTestFiles
      .filter(f => f.name != testFileName && f.name != exerciseContent.implementationPart.implFileName)
      .map { exFile =>
        writeExerciseFileAndMount(exFile, solTargetDir, DockerConnector.DefaultWorkingDir / exerciseContent.foldername)
      }

    val dockerBinds = unitTestSolFilesDockerBinds ++ exFilesMounts ++ Seq(
      DockerBind(
        testFile,
        DockerConnector.DefaultWorkingDir / exerciseContent.foldername / testFileName,
        isReadOnly = true
      ),
      DockerBind(testDataFile, DockerConnector.DefaultWorkingDir / testDataFileName, isReadOnly = true),
      DockerBind(resultFile, DockerConnector.DefaultWorkingDir / resultFileName)
    )

    DockerConnector
      .runContainer(imageName = programmingUnitTestCorrectionDockerImageName.name, maybeDockerBinds = dockerBinds)
      .map {
        case Failure(exception) =>
          onError(
            "Error while running programming unit test correction image",
            solutionSaved,
            maybeException = Some(exception)
          )
        case Success(_) =>
          ResultsFileJsonFormat
            .readTestCorrectionResultFile(resultFile)
            .fold(
              exception =>
                onError(
                  "Error while reading unit test correction result file",
                  solutionSaved,
                  maybeException = Some(exception)
                ),
              results => ProgrammingResult(solutionSaved, unitTestResults = results)
            )
      }
  }

}
