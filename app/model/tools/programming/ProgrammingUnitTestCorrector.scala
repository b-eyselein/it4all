package model.tools.programming

import better.files.File
import model.core.{DockerBind, DockerConnector, ScalaDockerImage}
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
  )(implicit ec: ExecutionContext): Future[ProgrammingAbstractResult] =
    solution.files.find(_.name == unitTestPart.testFileName) match {
      case None               => Future.successful(onError("Could not find test main file"))
      case Some(testMainFile) =>
        // write unit test file
        val testFileName = unitTestPart.testFileName
        val testFile     = solTargetDir / testFileName
        createFileAndWrite(testFile, testMainFile.content)

        // write test data file
        val testDataFile = solTargetDir / testDataFileName

        // remove ending '.py'
        val testFileNameForTestData = unitTestPart.testFileName
          .substring(0, unitTestPart.testFileName.length - 3)

        createFileAndWrite(
          testDataFile,
          Json.prettyPrint(
            ProgrammingToolJsonProtocol.unitTestDataWrites.writes(
              UnitTestTestData(
                unitTestPart.foldername,
                exerciseContent.filename,
                testFileNameForTestData,
                unitTestPart.unitTestTestConfigs
              )
            )
          )
        )

        // find mounts for implementation files
        val unitTestSolFilesDockerBinds: Seq[DockerBind] = unitTestPart.unitTestTestConfigs
          .filter(tc => implFileRegex.matches(tc.file.name))
          .map { tc =>
            writeExerciseFileAndMount(
              tc.file,
              solTargetDir,
              s"${DockerConnector.DefaultWorkingDir}/${unitTestPart.foldername}"
            )
          }

        // find mounts for exercise files
        val exFilesMounts = unitTestPart.unitTestFiles
          .filter(f => f.name != testFileName && f.name != exerciseContent.implementationPart.implFileName)
          .map { exFile =>
            writeExerciseFileAndMount(
              exFile,
              solTargetDir,
              s"${DockerConnector.DefaultWorkingDir}/${unitTestPart.foldername}"
            )
          }

        val dockerBinds = unitTestSolFilesDockerBinds ++ exFilesMounts ++ Seq(
          DockerBind(
            testFile,
            s"${DockerConnector.DefaultWorkingDir}/${unitTestPart.foldername}/$testFileName",
            isReadOnly = true
          ),
          DockerBind(testDataFile, s"${DockerConnector.DefaultWorkingDir}/$testDataFileName", isReadOnly = true),
          DockerBind(resultFile, s"${DockerConnector.DefaultWorkingDir}/$resultFileName")
        )

        DockerConnector
          .runContainer(imageName = programmingUnitTestCorrectionDockerImageName.name, maybeDockerBinds = dockerBinds)
          .map {
            case Failure(exception) =>
              onError(
                "Error while running programming unit test correction image",
                maybeException = Some(exception)
              )
            case Success(_) =>
              ResultsFileJsonFormat
                .readTestCorrectionResultFile(resultFile)
                .fold(
                  exception =>
                    onError(
                      "Error while reading unit test correction result file",
                      maybeException = Some(exception)
                    ),
                  results => ProgrammingResult(unitTestResults = results)
                )
          }
    }

}
