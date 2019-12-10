package model.tools.collectionTools.programming

import better.files.File._
import better.files._
import model.User
import model.core.result.SuccessType
import model.core.{DockerBind, DockerConnector, ScalaDockerImage}
import model.tools.collectionTools.programming.ProgrammingToolJsonProtocol.UnitTestTestData
import model.tools.collectionTools.{Exercise, ExerciseCollection, ExerciseFile, SampleSolution}
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}
import scala.util.matching.Regex
import scala.util.{Failure, Success, Try}

object ProgCorrector {

  val programmingSimplifiedCorrectionDockerImageName: ScalaDockerImage =
    ScalaDockerImage("ls6uniwue", "py_simplified_prog_corrector", "0.2.0")

  val programmingNormalCorrectionDockerImageName: ScalaDockerImage =
    ScalaDockerImage("beyselein", "py_normal_prog_corrector")

  val programmingUnitTestCorrectionDockerImageName: ScalaDockerImage =
    ScalaDockerImage("ls6uniwue", "py_unit_test_corrector", "0.3.1")

  private val resultFileName   = "result.json"
  private val testDataFileName = "test_data.json"
  private val testMainFileName = "test_main.py"

  private val implFileRegex: Regex = """.*_\d*\.py""".r


  private def createFileAndWrite(file: File, content: String): Unit = file
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
    exerciseContent: ProgExerciseContent,
    resultFile: File,
    solutionSaved: Boolean
  )(implicit ec: ExecutionContext): Future[Try[ProgCompleteResult]] = {

    val programmingSolutionFilesMounts = programmingSolution.files.map { exerciseFile: ExerciseFile =>
      writeExerciseFileAndMount(exerciseFile, solTargetDir)
    }

    exerciseContent.unitTestPart.unitTestType match {
      case UnitTestTypes.Simplified => correctSimplifiedImplementation(solTargetDir, exerciseContent, programmingSolutionFilesMounts, resultFile, solutionSaved)
      case UnitTestTypes.Normal     => correctNormalImplementation(solTargetDir, exerciseContent, programmingSolutionFilesMounts, solutionSaved)
    }
  }

  private def correctNormalImplementation(
    solTargetDir: File,
    exercise: ProgExerciseContent,
    progSolutionFilesMounts: Seq[DockerBind],
    solutionSaved: Boolean
  )(implicit ec: ExecutionContext): Future[Try[ProgCompleteResult]] = {

    val unitTestFileContent: String = exercise.sampleSolutions.headOption match {
      case None                                            => ???
      case Some(SampleSolution(_, ProgSolution(files, _))) => files.find(_.name == exercise.unitTestPart.testFileName).map(_.content).getOrElse(???) // unitTest.content
    }

    val unitTestFileName = s"${exercise.filename}_test.py"
    val unitTestFile     = solTargetDir / unitTestFileName
    createFileAndWrite(unitTestFile, unitTestFileContent)

    DockerConnector
      .runContainer(
        programmingNormalCorrectionDockerImageName.name,
        maybeDockerBinds = progSolutionFilesMounts :+ DockerBind(unitTestFile, DockerConnector.DefaultWorkingDir / unitTestFileName, isReadOnly = true),
        deleteContainerAfterRun = false
      )
      .map {
        case Failure(exception)          => Failure(exception)
        case Success(runContainerResult) =>
          val successType = if (runContainerResult.statusCode == 0) SuccessType.COMPLETE else SuccessType.ERROR

          val normalExecutionResult: NormalExecutionResult = NormalExecutionResult(successType, runContainerResult.logs)

          Success(ProgCompleteResult(solutionSaved, normalResult = Some(normalExecutionResult)))
      }
  }

  private def correctSimplifiedImplementation(
    solTargetDir: File,
    exercise: ProgExerciseContent,
    progSolutionFilesMounts: Seq[DockerBind],
    resultFile: File,
    solutionSaved: Boolean,
  )(implicit ec: ExecutionContext): Future[Try[ProgCompleteResult]] = {

    val testMainFile = solTargetDir / testMainFileName
    createFileAndWrite(testMainFile, exercise.unitTestPart.simplifiedTestMainFile.map(_.content).getOrElse(???))

    val testDataFile = solTargetDir / testDataFileName
    createFileAndWrite(testDataFile, Json.prettyPrint(exercise.buildSimpleTestDataFileContent(exercise.sampleTestData)))

    DockerConnector
      .runContainer(
        imageName = programmingSimplifiedCorrectionDockerImageName.name,
        maybeDockerBinds = progSolutionFilesMounts ++ Seq(
          DockerBind(testDataFile, DockerConnector.DefaultWorkingDir / testDataFileName, isReadOnly = true),
          DockerBind(testMainFile, DockerConnector.DefaultWorkingDir / testMainFileName, isReadOnly = true),
          DockerBind(solTargetDir / resultFileName, DockerConnector.DefaultWorkingDir / resultFileName)
        ),
      )
      .map {
        case Failure(exception) => Failure(exception)
        case Success(_)         =>
          ResultsFileJsonFormat.readSimplifiedExecutionResultFile(resultFile).map { results =>
            ProgCompleteResult(solutionSaved, simplifiedResults = results)
          }
      }
  }

  private def correctUnittest(
    solTargetDir: File,
    progSolution: ProgSolution,
    exercise: ProgExerciseContent,
    resultFile: File,
    solutionSaved: Boolean
  )(implicit ec: ExecutionContext): Future[Try[ProgCompleteResult]] = {

    // write unit test file
    val testFileName = exercise.unitTestPart.testFileName
    val testFile     = solTargetDir / testFileName
    createFileAndWrite(testFile, progSolution.files.find(_.name == testFileName).map(_.content).getOrElse(???))

    // write test data file
    val testDataFile            = solTargetDir / testDataFileName
    // remove ending '.py'
    val testFileNameForTestData = exercise.unitTestPart.testFileName.substring(0, exercise.unitTestPart.testFileName.length - 3)
    createFileAndWrite(
      testDataFile,
      Json.prettyPrint(
        ProgrammingToolJsonProtocol.unitTestDataWrites.writes(
          UnitTestTestData(exercise.foldername, exercise.filename, testFileNameForTestData, exercise.unitTestPart.unitTestTestConfigs)
        )
      )
    )

    // find mounts for implementation files
    val unitTestSolFilesDockerBinds: Seq[DockerBind] = exercise.unitTestPart.unitTestTestConfigs
      .filter(tc => implFileRegex.matches(tc.file.name))
      .map { tc =>
        writeExerciseFileAndMount(tc.file, solTargetDir, DockerConnector.DefaultWorkingDir / exercise.foldername)
      }

    // find mounts for exercise files
    val exFilesMounts = exercise.unitTestPart.unitTestFiles
      .filter(f => f.name != testFileName && f.name != exercise.implementationPart.implFileName)
      .map { exFile =>
        writeExerciseFileAndMount(exFile, solTargetDir, DockerConnector.DefaultWorkingDir / exercise.foldername)
      }

    DockerConnector
      .runContainer(
        imageName = programmingUnitTestCorrectionDockerImageName.name,
        maybeDockerBinds = unitTestSolFilesDockerBinds ++ exFilesMounts ++ Seq(
          DockerBind(testFile, DockerConnector.DefaultWorkingDir / exercise.foldername / testFileName, isReadOnly = true),
          DockerBind(testDataFile, DockerConnector.DefaultWorkingDir / testDataFileName, isReadOnly = true),
          DockerBind(resultFile, DockerConnector.DefaultWorkingDir / resultFileName),
        )
      )
      .map {
        case Failure(exception) => Failure(exception)
        case Success(_)         =>
          ResultsFileJsonFormat.readTestCorrectionResultFile(resultFile).map { results =>
            ProgCompleteResult(solutionSaved, unitTestResults = results)
          }
      }
  }

  def correct(
    user: User,
    progSolution: ProgSolution,
    collection: ExerciseCollection,
    exercise: Exercise,
    content: ProgExerciseContent,
    part: ProgExPart,
    solutionSaved: Boolean
  )(implicit ec: ExecutionContext): Future[Try[ProgCompleteResult]] = {

    val solutionTargetDir: File = ProgToolMain.solutionDirForExercise(user.username, exercise.collectionId, exercise.id) / part.urlName

    // Create or truncate result file
    val resultFile = solutionTargetDir / resultFileName

    resultFile.createIfNotExists(createParents = true).clear()

    part match {
      case ProgExParts.TestCreation => correctUnittest(solutionTargetDir, progSolution, content, resultFile, solutionSaved)
      case _                        => correctImplementation(solutionTargetDir, progSolution, content, resultFile, solutionSaved)
    }
  }

}
