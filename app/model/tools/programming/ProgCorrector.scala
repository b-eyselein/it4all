package model.tools.programming

import java.io.FileNotFoundException

import better.files.File._
import better.files._
import model.User
import model.core.result.SuccessType
import model.docker._
import model.tools.programming.ResultsFileJsonFormat._
import modules.DockerPullsStartTask
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object ProgCorrector {

  private val resultFileName  : String = "result.json"
  private val testDataFileName: String = "test_data.json"

  private def buildSolutionFileName(fileEnding: String): String = s"solution.$fileEnding"

  private def buildTestMainFileName(fileEnding: String): String = s"test_main.$fileEnding"

  private def correctImplementation(solTargetDir: File, resFolder: File, progSolution: ProgSolution, exercise: ProgExercise)
                                   (implicit ec: ExecutionContext): Future[Try[ProgCompleteResult]] = {

    exercise.unitTestType match {
      case UnitTestTypes.Simplified =>

        // mounted from resources folder...
        val testMainFileName = buildTestMainFileName(fileEnding = "py")
        val testMainFile = resFolder / testMainFileName

        if (testMainFile.exists) {

          val solFileName = buildSolutionFileName(progSolution.language.fileEnding)
          val solutionFile = solTargetDir / solFileName
          solutionFile.createFileIfNotExists(createParents = true).write(progSolution.implementation)

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

        val solFileName = s"${exercise.filename}.py"
        val solutionFile = solTargetDir / solFileName
        solutionFile.createFileIfNotExists(createParents = true).write(progSolution.implementation)

        val unitTestFileContent = exercise.sampleSolutions.headOption match {
          case None                                        => ???
          case Some(ProgSampleSolution(_, _, _, unitTest)) => unitTest.content
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
              println("------------------------")
              println(runContainerResult)
              println("------------------------")

              val successType = if (runContainerResult.statusCode == 0) SuccessType.COMPLETE else SuccessType.ERROR

              val normalExecutionResult: NormalExecutionResult = NormalExecutionResult(successType, runContainerResult.logs)

              Success(ProgCompleteResult(simplifiedResults = Seq.empty, normalResult = Some(normalExecutionResult), unitTestResults = Seq.empty))
          }
    }
  }

  private def correctUnittest(solTargetDir: File, resFolder: File, progSolution: ProgSolution, exercise: ProgExercise)
                             (implicit ec: ExecutionContext): Future[Try[ProgCompleteResult]] = {

    // write unit test file
    val testFileName = s"${exercise.filename}_test.py"
    val testFile = solTargetDir / testFileName
    testFile.createIfNotExists(createParents = true).write(progSolution.unitTest.content)

    // write test data file
    val testDataFile = solTargetDir / testDataFileName
    val testDataToWrite = Json.prettyPrint(
      ProgJsonProtocols.unitTestDataWrites.writes(ProgJsonProtocols.UnitTestTestData(exercise.foldername, exercise.filename, exercise.unitTestTestConfigs))
    )
    testDataFile.createIfNotExists(createParents = true).write(testDataToWrite)

    // find mounts for implementation files
    val unitTestSolFilesDockerBinds = (resFolder / "unit_test_sols").children
      .filter(f => f.name.matches(""".*_\d\.py"""))
      .map(f => DockerBind(f, DockerConnector.DefaultWorkingDir / exercise.foldername / f.name, isReadOnly = true))
      .toSeq

    val exFilesMount = exercise.unitTestFiles
      .filter(f => f.name != "test.py" && f.name != s"${exercise.filename}.py")
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

  def correct(user: User, progSolution: ProgSolution, collection: ProgCollection, exercise: ProgExercise, part: ProgExPart,
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
