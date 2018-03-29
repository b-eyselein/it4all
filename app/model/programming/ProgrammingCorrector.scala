package model.programming

import java.nio.file.Path

import model.User
import model.docker._
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Try}

object ProgrammingCorrector extends model.core.FileUtils {

  private val resultFileName   = "result.json"
  private val testDataFileName = "testdata.json"

  private def solutionFileName(fileEnding: String): String = "solution." + fileEnding

  private def testMainFileName(fileEnding: String): String = "test_main." + fileEnding

  private val dockerImageName = "beyselein/python_prog_tester"

  def correct(user: User, exercise: ProgCompleteEx, language: ProgLanguage, implementation: String, solutionSaved: Boolean, completeTestData: Seq[CompleteTestData],
              toolMain: ProgrammingToolMain)(implicit ec: ExecutionContext): Try[Future[Try[ProgCompleteResult]]] = {

    val exerciseResourcesFolder = toolMain.exerciseResourcesFolder / (exercise.id + "-" + exercise.ex.folderIdentifier)
    val solutionTargetDir = toolMain.solutionDirForExercise(user.username, exercise.ex.id)
    val testDataFileContent = Json.prettyPrint(TestDataJsonFormat.dumpTestDataToJson(exercise, completeTestData))

    writeAndCopyFiles(solutionTargetDir, language.fileEnding, implementation, testDataFileContent) map { _ =>

      // FIXME: what if image does not exist?
      val containerResult: Future[RunContainerResult] = DockerConnector.runContainer(
        imageName = dockerImageName,
        maybeEntryPoint = None,
        dockerBinds = mountFiles(exerciseResourcesFolder, solutionTargetDir, fileEnding = "py")
//        , deleteContainerAfterRun = false
      )

      val futureResults: Future[Try[Seq[ProgEvalResult]]] = containerResult map {

        case RunContainerSuccess => ResultsFileJsonFormat.readResultFile(solutionTargetDir / resultFileName, completeTestData)

        case failure: RunContainerFailure => Failure(failure)

      }

      futureResults map { resultTry: Try[Seq[ProgEvalResult]] =>
        resultTry map (results => ProgCompleteResult(implementation, solutionSaved, results))
      }
    }

  }

  private def writeAndCopyFiles(solTargetDir: Path, fileEnding: String, impl: String, testDataFileContent: String) = for {

    // FIXME: evaluate files!

    solutionWrite: Path <- write(solTargetDir / solutionFileName(fileEnding), impl)

    testDataWrite: Path <- write(solTargetDir / testDataFileName, testDataFileContent)

    resultFileCreate: Path <- createEmptyFile(solTargetDir / resultFileName)

  } yield (solutionWrite, testDataWrite, resultFileCreate)

  private def mountFiles(exResFolder: Path, solTargetDir: Path, fileEnding: String): Seq[DockerBind] = {

    // FIXME: update with files in exerciseResourcesFolder!
    val testMainMount = DockerBindUtils.mountFileByName(exResFolder, DockerConnector.DefaultWorkingDir, testMainFileName(fileEnding))

    val solutionMount = DockerBindUtils.mountFileByName(solTargetDir, DockerConnector.DefaultWorkingDir, solutionFileName(fileEnding))

    val testDataMount = DockerBindUtils.mountFileByName(solTargetDir, DockerConnector.DefaultWorkingDir, testDataFileName)

    val resultFileMount = DockerBindUtils.mountFileByName(solTargetDir, DockerConnector.DefaultWorkingDir, resultFileName)

    Seq(testMainMount, solutionMount, testDataMount, resultFileMount)

  }
}