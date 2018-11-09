package model.programming

import better.files.File._
import better.files._
import model.User
import model.docker._
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Try}

object ProgCorrector {

  private val resultFileName  : String = "result.json"
  private val testDataFileName: String = "testdata.json"

  private def solutionFileName(fileEnding: String): String = "solution." + fileEnding

  private def testMainFileName(fileEnding: String): String = "test_main." + fileEnding

  private val dockerImageName = "beyselein/python_prog_tester:0.9"

  def correct(user: User, exercise: ProgCompleteEx, language: ProgLanguage, implementation: String, completeTestData: Seq[TestData],
              toolMain: ProgToolMain)(implicit ec: ExecutionContext): Future[Try[ProgCompleteResult]] = {

    val exerciseResourcesFolder: File = toolMain.exerciseResourcesFolder / (exercise.ex.id + "-" + exercise.ex.folderIdentifier)
    val solutionTargetDir: File = toolMain.solutionDirForExercise(user.username, exercise.ex.id)
    val testDataFileContent: String = Json.prettyPrint(TestDataJsonFormat.dumpTestDataToJson(exercise, completeTestData))

    writeAndCopyFiles(solutionTargetDir, language.fileEnding, implementation, testDataFileContent)

    // FIXME: what if image does not exist?
    val containerResult: Future[RunContainerResult] = DockerConnector.runContainer(dockerImageName,
      dockerBinds = mountProgrammingFiles(exerciseResourcesFolder, solutionTargetDir, fileEnding = "py") //, deleteContainerAfterRun = false
    )

    val futureResults = containerResult map {

      case RunContainerSuccess => ResultsFileJsonFormat.readResultFile((solutionTargetDir / resultFileName).toJava.toPath, completeTestData)

      case failure: RunContainerFailure => Failure(failure)

    }

    futureResults map { resultTry =>
      resultTry map (results => ProgCompleteResult(implementation, results))
    }

  }

  private def writeAndCopyFiles(solTargetDir: File, fileEnding: String, impl: String, testDataFileContent: String): (File, File, File) = {

    // FIXME: evaluate files!

    val solutionWrite = (solTargetDir / solutionFileName(fileEnding)).write(impl)

    val testDataWrite = (solTargetDir / testDataFileName).write(testDataFileContent)

    val resultFileCreate = (solTargetDir / resultFileName).createIfNotExists(createParents = true)

    (solutionWrite, testDataWrite, resultFileCreate)
  }

  private def mountProgrammingFiles(exResFolder: File, solTargetDir: File, fileEnding: String): Seq[DockerBind] = {

    // FIXME: update with files in exerciseResourcesFolder!
    val teMainFileName: String = testMainFileName(fileEnding)
    val testMainMount = DockerBind((exResFolder / teMainFileName).toJava.toPath, (DockerConnector.DefaultWorkingDir / teMainFileName).toJava.toPath)

    val solFileName = solutionFileName(fileEnding)
    val solutionMount = DockerBind((solTargetDir / solFileName).toJava.toPath, (DockerConnector.DefaultWorkingDir / solFileName).toJava.toPath)

    val testDataMount = DockerBind((solTargetDir / testDataFileName).toJava.toPath, (DockerConnector.DefaultWorkingDir / testDataFileName).toJava.toPath)

    val resultFileMount = DockerBind((solTargetDir / resultFileName).toJava.toPath, (DockerConnector.DefaultWorkingDir / resultFileName).toJava.toPath)

    Seq(testMainMount, solutionMount, testDataMount, resultFileMount)

  }
}