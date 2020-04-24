package model.tools.programming

import better.files.File._
import better.files._
import model.core.result.SuccessType
import model.core.{DockerBind, DockerConnector, ScalaDockerImage}
import model.tools.programming.ProgrammingToolJsonProtocol.UnitTestTestData
import model.tools.{AbstractCorrector, Exercise, ExerciseFile, SampleSolution}
import play.api.Logger
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}
import scala.util.matching.Regex
import scala.util.{Failure, Success, Try}

object ProgCorrector extends AbstractCorrector {

  override protected val logger: Logger = Logger(ProgCorrector.getClass)

  val programmingSimplifiedCorrectionDockerImageName: ScalaDockerImage =
    ScalaDockerImage("ls6uniwue", "py_simplified_prog_corrector", "0.2.0")

  val programmingNormalCorrectionDockerImageName: ScalaDockerImage =
    ScalaDockerImage("ls6uniwue", "py_normal_prog_corrector", "0.2.0")

  val programmingUnitTestCorrectionDockerImageName: ScalaDockerImage =
    ScalaDockerImage("ls6uniwue", "py_unit_test_corrector", "0.3.1")

  private val resultFileName   = "result.json"
  private val testDataFileName = "test_data.json"
  private val testMainFileName = "test_main.py"

  private val implFileRegex: Regex = """.*_\d*\.py""".r

  private def createFileAndWrite(file: File, content: String): Unit =
    file
      .createFileIfNotExists(createParents = true)
      .write(content)

  private def writeExerciseFileAndMount(
    exerciseFile: ExerciseFile,
    writeToDirectory: File,
    bindToDirectory: File = DockerConnector.DefaultWorkingDir,
    isReadOnly: Boolean = true
  ): DockerBind = {
    val targetPath = writeToDirectory / exerciseFile.name

    createFileAndWrite(targetPath, exerciseFile.content)

    DockerBind(targetPath, bindToDirectory / exerciseFile.name, isReadOnly)
  }

  private def correctImplementation(
    solTargetDir: File,
    programmingSolution: ProgSolution,
    exerciseContent: ProgrammingExerciseContent,
    resultFile: File,
    solutionSaved: Boolean
  )(implicit ec: ExecutionContext): Future[Try[ProgCompleteResult]] = {

    val programmingSolutionFilesMounts = programmingSolution.files.map { exerciseFile: ExerciseFile =>
      writeExerciseFileAndMount(exerciseFile, solTargetDir)
    }

    exerciseContent.unitTestPart.unitTestType match {
      case UnitTestTypes.Simplified =>
        correctSimplifiedImplementation(
          solTargetDir,
          exerciseContent,
          programmingSolutionFilesMounts,
          resultFile,
          solutionSaved
        )
      case UnitTestTypes.Normal =>
        correctNormalImplementation(
          solTargetDir,
          exerciseContent,
          exerciseContent.sampleSolutions,
          programmingSolutionFilesMounts,
          solutionSaved
        )
    }
  }

  private def correctNormalImplementation(
    solTargetDir: File,
    exerciseContent: ProgrammingExerciseContent,
    sampleSolutions: Seq[SampleSolution[ProgSolution]],
    progSolutionFilesMounts: Seq[DockerBind],
    solutionSaved: Boolean
  )(implicit ec: ExecutionContext): Future[Try[ProgCompleteResult]] = {

    val unitTestFileContent: String = sampleSolutions.headOption match {
      case None => ???
      case Some(SampleSolution(_, ProgSolution(files))) =>
        files
          .find(_.name == exerciseContent.unitTestPart.testFileName)
          .map(_.content)
          .getOrElse(???) // unitTest.content
    }

    val unitTestFileName = s"${exerciseContent.filename}_test.py"
    val unitTestFile     = solTargetDir / unitTestFileName
    createFileAndWrite(unitTestFile, unitTestFileContent)

    DockerConnector
      .runContainer(
        programmingNormalCorrectionDockerImageName.name,
        maybeDockerBinds = progSolutionFilesMounts :+ DockerBind(
          unitTestFile,
          DockerConnector.DefaultWorkingDir / unitTestFileName,
          isReadOnly = true
        ),
        deleteContainerAfterRun = false
      )
      .map {
        case Failure(exception) => Failure(exception)
        case Success(runContainerResult) =>
          val successType = if (runContainerResult.statusCode == 0) SuccessType.COMPLETE else SuccessType.ERROR

          val normalExecutionResult: NormalExecutionResult = NormalExecutionResult(successType, runContainerResult.logs)

          Success(ProgCompleteResult(solutionSaved, normalResult = Some(normalExecutionResult)))
      }
  }

  private def correctSimplifiedImplementation(
    solTargetDir: File,
    exerciseContent: ProgrammingExerciseContent,
    progSolutionFilesMounts: Seq[DockerBind],
    resultFile: File,
    solutionSaved: Boolean
  )(implicit ec: ExecutionContext): Future[Try[ProgCompleteResult]] = {

    val testMainFile = solTargetDir / testMainFileName
    createFileAndWrite(testMainFile, exerciseContent.unitTestPart.simplifiedTestMainFile.map(_.content).getOrElse(???))

    val testDataFile = solTargetDir / testDataFileName
    createFileAndWrite(
      testDataFile,
      Json.prettyPrint(exerciseContent.buildSimpleTestDataFileContent(exerciseContent.sampleTestData))
    )

    DockerConnector
      .runContainer(
        imageName = programmingSimplifiedCorrectionDockerImageName.name,
        maybeDockerBinds = progSolutionFilesMounts ++ Seq(
          DockerBind(testDataFile, DockerConnector.DefaultWorkingDir / testDataFileName, isReadOnly = true),
          DockerBind(testMainFile, DockerConnector.DefaultWorkingDir / testMainFileName, isReadOnly = true),
          DockerBind(solTargetDir / resultFileName, DockerConnector.DefaultWorkingDir / resultFileName)
        )
      )
      .map {
        case Failure(exception) => Failure(exception)
        case Success(_) =>
          ResultsFileJsonFormat.readSimplifiedExecutionResultFile(resultFile).map { results =>
            ProgCompleteResult(solutionSaved, simplifiedResults = results)
          }
      }
  }

  private def correctUnittest(
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
    val testFileNameForTestData =
      exerciseContent.unitTestPart.testFileName.substring(0, exerciseContent.unitTestPart.testFileName.length - 3)
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
        case Failure(exception) => Failure(exception)
        case Success(_) =>
          ResultsFileJsonFormat.readTestCorrectionResultFile(resultFile).map { results =>
            ProgCompleteResult(solutionSaved, unitTestResults = results)
          }
      }
  }

  def correct(
    progSolution: ProgSolution,
    exercise: Exercise[ProgSolution, ProgrammingExerciseContent],
    part: ProgExPart,
    solutionTargetDir: File,
    solutionSaved: Boolean
  )(implicit ec: ExecutionContext): Future[Try[ProgCompleteResult]] = {

    // Create or truncate result file
    val resultFile = solutionTargetDir / resultFileName

    resultFile.createIfNotExists(createParents = true).clear()

    part match {
      case ProgExPart.TestCreation =>
        correctUnittest(solutionTargetDir, progSolution, exercise.content, resultFile, solutionSaved)
      case _ => correctImplementation(solutionTargetDir, progSolution, exercise.content, resultFile, solutionSaved)
    }
  }

}
