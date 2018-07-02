package model.programming

import java.nio.file.Path

import model.User
import model.core.FileUtils
import model.docker._
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Try}

object ProgCorrector extends FileUtils {

  private val resultFileName   = "result.json"
  private val testDataFileName = "testdata.json"

  private def solutionFileName(fileEnding: String): String = "solution." + fileEnding

  private def testMainFileName(fileEnding: String): String = "test_main." + fileEnding

  private val dockerImageName = "beyselein/python_prog_tester"

  def correct(user: User, exercise: ProgCompleteEx, language: ProgLanguage, implementation: String, completeTestData: Seq[TestData],
              toolMain: ProgToolMain)(implicit ec: ExecutionContext): Try[Future[Try[ProgCompleteResult]]] = {

    val exerciseResourcesFolder = toolMain.exerciseResourcesFolder / (exercise.id + "-" + exercise.ex.folderIdentifier)
    val solutionTargetDir = toolMain.solutionDirForExercise(user.username, exercise.ex.id)
    val testDataFileContent = Json.prettyPrint(TestDataJsonFormat.dumpTestDataToJson(exercise, completeTestData))

    writeAndCopyFiles(solutionTargetDir, language.fileEnding, implementation, testDataFileContent) map { _ =>

      // FIXME: what if image does not exist?
      val containerResult: Future[RunContainerResult] = DockerConnector.runContainer(dockerImageName,
        dockerBinds = mountProgrammingFiles(exerciseResourcesFolder, solutionTargetDir, fileEnding = "py") //, deleteContainerAfterRun = false
      )

      val futureResults = containerResult map {

        case RunContainerSuccess => ResultsFileJsonFormat.readResultFile(solutionTargetDir / resultFileName, completeTestData)

        case failure: RunContainerFailure => Failure(failure)

      }

      futureResults map { resultTry =>
        resultTry map (results => ProgCompleteResult(implementation, results))
      }
    }

  }

  private def writeAndCopyFiles(solTargetDir: Path, fileEnding: String, impl: String, testDataFileContent: String): Try[(Path, Path, Path)] = for {

    // FIXME: evaluate files!

    solutionWrite: Path <- write(solTargetDir / solutionFileName(fileEnding), impl)

    testDataWrite: Path <- write(solTargetDir / testDataFileName, testDataFileContent)

    resultFileCreate: Path <- createEmptyFile(solTargetDir / resultFileName)

  } yield (solutionWrite, testDataWrite, resultFileCreate)

  private def mountProgrammingFiles(exResFolder: Path, solTargetDir: Path, fileEnding: String): Seq[DockerBind] = {

    // FIXME: update with files in exerciseResourcesFolder!
    val teMainFileName: String = testMainFileName(fileEnding)
    val testMainMount = DockerBind(exResFolder / teMainFileName, DockerConnector.DefaultWorkingDir / teMainFileName)

    val solFileName = solutionFileName(fileEnding)
    val solutionMount = DockerBind(solTargetDir / solFileName, DockerConnector.DefaultWorkingDir / solFileName)

    val testDataMount = DockerBind(solTargetDir / testDataFileName, DockerConnector.DefaultWorkingDir / testDataFileName)

    val resultFileMount = DockerBind(solTargetDir / resultFileName, DockerConnector.DefaultWorkingDir / resultFileName)

    Seq(testMainMount, solutionMount, testDataMount, resultFileMount)

  }
}