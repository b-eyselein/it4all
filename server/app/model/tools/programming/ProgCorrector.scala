package model.tools.programming

import java.io.FileNotFoundException

import better.files.File._
import better.files._
import model.core.result.SuccessType
import model.docker._
import model.tools.programming.ProgrammingToolJsonProtocol.UnitTestTestData
import model.tools.programming.ResultsFileJsonFormat._
import model.{ExerciseCollection, User}
import modules.DockerPullsStartTask
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object ProgCorrector {

  private val resultFileName  : String = "result.json"
  private val testDataFileName: String = "test_data.json"

  private def buildTestMainFileName(fileEnding: String): String = s"test_main.$fileEnding"

  private def correctImplementation(solTargetDir: File, resFolder: File, progSolution: ProgSolution, exercise: ProgExercise)
                                   (implicit ec: ExecutionContext): Future[Try[ProgCompleteResult]] = {

    val solFileName = s"${exercise.filename}.py"
    val solutionFile = solTargetDir / solFileName
    val solutionFileContent = progSolution.files.find(_.name == solFileName).map(_.content).getOrElse(???)
    solutionFile.createFileIfNotExists(createParents = true).write(solutionFileContent)

    exercise.unitTestPart.unitTestType match {
      case UnitTestTypes.Simplified =>

        // mounted from resources folder...
        val testMainFileName = buildTestMainFileName(fileEnding = "py")
        val testMainFile = resFolder / testMainFileName

        if (testMainFile.exists) {

          val testDataFile = solTargetDir / testDataFileName
          val testDataFileContent: JsValue = exercise.buildSimpleTestDataFileContent(exercise.sampleTestData)
          testDataFile.createFileIfNotExists(createParents = true).write(Json.prettyPrint(testDataFileContent))

          val dockerBinds = Seq(
            DockerBind(solutionFile, DockerConnector.DefaultWorkingDir / solFileName, isReadOnly = true),
            DockerBind(testDataFile, DockerConnector.DefaultWorkingDir / testDataFileName, isReadOnly = true),
            DockerBind(testMainFile, DockerConnector.DefaultWorkingDir / testMainFileName, isReadOnly = true),
            // Mount result file
            DockerBind(solTargetDir / resultFileName, DockerConnector.DefaultWorkingDir / resultFileName),
          )

          DockerConnector
            .runContainer(
              imageName = DockerPullsStartTask.pythonSimplifiedProgTesterImage,
              maybeDockerBinds = Some(dockerBinds),
            )
            .map {
              case Failure(exception)          => Failure(exception)
              case Success(runContainerResult) =>
                readSimplifiedExecutionResultFile(solTargetDir / resultFileName).map { results =>
                  ProgCompleteResult(simplifiedResults = results, normalResult = None, unitTestResults = Seq.empty)
                }
            }
        } else {
          Future.successful(Failure(new FileNotFoundException(s"The file $testMainFile does not exist!}")))
        }

      case UnitTestTypes.Normal =>

        val unitTestFileContent: String = exercise.sampleSolutions.headOption match {
          case None                                                => ???
          case Some(ProgSampleSolution(_, ProgSolution(files, _))) => files.find(_.name == exercise.unitTestPart.testFileName).map(_.content).getOrElse(???) // unitTest.content
        }

        val unitTestFileName = s"${exercise.filename}_test.py"
        val unitTestFile = solTargetDir / unitTestFileName
        unitTestFile.createIfNotExists(createParents = true).write(unitTestFileContent)

        val dockerBinds = Seq(
          DockerBind(solutionFile, DockerConnector.DefaultWorkingDir / solFileName, isReadOnly = true),
          DockerBind(unitTestFile, DockerConnector.DefaultWorkingDir / unitTestFileName, isReadOnly = true),
        )

        DockerConnector
          .runContainer(
            DockerPullsStartTask.pythonNormalProgTesterImage,
            maybeDockerBinds = Some(dockerBinds),
            deleteContainerAfterRun = false
          )
          .map {
            case Failure(exception)          => Failure(exception)
            case Success(runContainerResult) =>
              val successType = if (runContainerResult.statusCode == 0) SuccessType.COMPLETE else SuccessType.ERROR

              val normalExecutionResult: NormalExecutionResult = NormalExecutionResult(successType, runContainerResult.logs)

              Success(ProgCompleteResult(simplifiedResults = Seq.empty, normalResult = Some(normalExecutionResult), unitTestResults = Seq.empty))
          }
    }
  }

  private def correctUnittest(solTargetDir: File, resFolder: File, progSolution: ProgSolution, exercise: ProgExercise)
                             (implicit ec: ExecutionContext): Future[Try[ProgCompleteResult]] = {

    // write unit test file
    val testFileName = exercise.unitTestPart.testFileName
    val testFile = solTargetDir / testFileName
    val testFileContent = progSolution.files.find(_.name == testFileName).map(_.content).getOrElse(???)
    testFile.createIfNotExists(createParents = true).write(testFileContent)

    // write test data file
    val testDataFile = solTargetDir / testDataFileName
    // remove ending '.py'
    val testFileNameForTestData = exercise.unitTestPart.testFileName.substring(0, exercise.unitTestPart.testFileName.length - 3)
    val unitTestTestData = UnitTestTestData(exercise.foldername, exercise.filename, testFileNameForTestData, exercise.unitTestPart.unitTestTestConfigs)
    val testDataToWrite = Json.prettyPrint(ProgrammingToolJsonProtocol.unitTestDataWrites.writes(unitTestTestData))
    testDataFile.createIfNotExists(createParents = true).write(testDataToWrite)

    // find mounts for implementation files
    val unitTestSolFilesDockerBinds = (resFolder / "unit_test_sols").children
      .filter(f => f.name.matches(""".*_\d*\.py"""))
      .map(f => DockerBind(f, DockerConnector.DefaultWorkingDir / exercise.foldername / f.name, isReadOnly = true))
      .toSeq

    val exFilesMount = exercise.unitTestPart.unitTestFiles
      .filter(f => f.name != testFileName && f.name != exercise.implementationPart.implFileName)
      .map(f => DockerBind(resFolder / f.name, DockerConnector.DefaultWorkingDir / exercise.foldername / f.name))

    val dockerBinds: Seq[DockerBind] = Seq(
      // Mount file with user defined unittests
      DockerBind(testFile, DockerConnector.DefaultWorkingDir / exercise.foldername / testFileName, isReadOnly = true),
      // Mount test config file
      DockerBind(testDataFile, DockerConnector.DefaultWorkingDir / testDataFileName, isReadOnly = true),
      // Mount results file
      DockerBind(solTargetDir / resultFileName, DockerConnector.DefaultWorkingDir / resultFileName),
    ) ++ unitTestSolFilesDockerBinds ++ exFilesMount

    DockerConnector
      .runContainer(
        imageName = DockerPullsStartTask.pythonUnitTesterImage,
        maybeDockerBinds = Some(dockerBinds)
      )
      .map {
        case Failure(exception)          => Failure(exception)
        case Success(runContainerResult) =>
          readTestCorrectionResultFile(solTargetDir / resultFileName).map { results =>
            ProgCompleteResult(simplifiedResults = Seq.empty, normalResult = None, unitTestResults = results)
          }
      }
  }

  def correct(user: User, progSolution: ProgSolution, collection: ExerciseCollection, exercise: ProgExercise, part: ProgExPart,
              toolMain: ProgToolMain)(implicit ec: ExecutionContext): Future[Try[ProgCompleteResult]] = {

    val solutionTargetDir: File = toolMain.solutionDirForExercise(user.username, collection.id, exercise.id) / part.urlName

    val exerciseResourcesFolder: File = toolMain.exerciseResourcesFolder / s"${collection.id}-${collection.shortName}" / s"${exercise.id}-${exercise.foldername}"

    // Create or truncate result file
    val resultFile = solutionTargetDir / resultFileName
    resultFile.createIfNotExists(createParents = true).clear()

    part match {
      case ProgExParts.TestCreation => correctUnittest(solutionTargetDir, exerciseResourcesFolder, progSolution, exercise)
      case _                        => correctImplementation(solutionTargetDir, exerciseResourcesFolder, progSolution, exercise)
    }
  }

}
