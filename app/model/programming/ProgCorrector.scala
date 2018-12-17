package model.programming

import better.files.File._
import better.files._
import model.User
import model.docker._
import modules.DockerPullsStartTask
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Try}

object ProgCorrector {

  private val resultFileName  : String = "result.json"
  private val testDataFileName: String = "test_data.json"

  private def solutionFileName(fileEnding: String): String = "solution." + fileEnding

  private def buildTestMainFileName(fileEnding: String): String = "test_main." + fileEnding

  def correct(user: User, exercise: ProgCompleteEx, language: ProgLanguage, implementation: String, completeTestData: Seq[TestData],
              toolMain: ProgToolMain)(implicit ec: ExecutionContext): Future[Try[ProgCompleteResult]] = {


    val exerciseResourcesFolder: File = toolMain.exerciseResourcesFolder / (exercise.ex.id + "-" + exercise.ex.folderIdentifier)
    val solutionTargetDir: File = toolMain.solutionDirForExercise(user.username, exercise.ex.id)
    val testDataFileContent: String = Json.prettyPrint(TestDataJsonFormat.dumpTestDataToJson(exercise, completeTestData))

    // Write/Create files
    val solFileName = solutionFileName(language.fileEnding)
    val solutionFile = solutionTargetDir / solFileName
    solutionFile.write(implementation)

    val testDataFile = solutionTargetDir / testDataFileName
    testDataFile.write(testDataFileContent)

    val resultFile = solutionTargetDir / resultFileName
    resultFile.createIfNotExists(createParents = true)


    /*
     * FIXME: what if image does not exist?
     *  --> val imageExists: Boolean = DockerConnector.imageExists(DockerPullsStartTask.pythonProgTesterImage)
     */
    val containerResult: Future[RunContainerResult] = DockerConnector.runContainer(DockerPullsStartTask.pythonProgTesterImage,
      dockerBinds = mountProgrammingFiles(exerciseResourcesFolder, solutionTargetDir, solFileName, fileEnding = "py") //, deleteContainerAfterRun = false
    )

    val futureResults = containerResult map {

      case RunContainerSuccess => ResultsFileJsonFormat.readResultFile(solutionTargetDir / resultFileName, completeTestData)

      case failure: RunContainerFailure => Failure(failure)

    }

    futureResults map { resultTries: Try[Seq[ExecutionResult]] =>
      resultTries map (results => ProgCompleteResult(implementation, results))
    }
  }

  private def mountProgrammingFiles(exResFolder: File, solTargetDir: File, solFileName: String, fileEnding: String): Seq[DockerBind] = {

    // FIXME: update with files in exerciseResourcesFolder!
    val testMainFileName = buildTestMainFileName(fileEnding)
    val testMainMount = DockerBind(exResFolder / testMainFileName, DockerConnector.DefaultWorkingDir / testMainFileName, isReadOnly = true)

    val solutionMount = DockerBind(solTargetDir / solFileName, DockerConnector.DefaultWorkingDir / solFileName, isReadOnly = true)

    val testDataMount = DockerBind(solTargetDir / testDataFileName, DockerConnector.DefaultWorkingDir / testDataFileName, isReadOnly = true)

    val resultFileMount = DockerBind(solTargetDir / resultFileName, DockerConnector.DefaultWorkingDir / resultFileName)

    Seq(testMainMount, solutionMount, testDataMount, resultFileMount)

  }
}