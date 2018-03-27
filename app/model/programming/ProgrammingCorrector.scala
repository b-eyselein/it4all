package model.programming

import java.nio.file.attribute.{PosixFilePermission, PosixFilePermissions}
import java.nio.file.{Files, Path}

import com.github.dockerjava.api.model.AccessMode
import model.docker._
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Try}

object ProgrammingCorrector extends model.core.FileUtils {

  private val ScriptName     = "script.py"
  private val TestDataFile   = "testconfig.json"
  private val resultFileName = "result.json"

  private val filePermissions: java.util.Set[PosixFilePermission] = PosixFilePermissions.fromString("rwxrwxrwx")

  def correct(exercise: ProgCompleteEx, language: ProgLanguage, implementation: String, solutionSaved: Boolean, completeTestData: Seq[CompleteTestData],
              solutionTargetDir: Path, exerciseResourcesFolder: Path)(implicit ec: ExecutionContext): Try[Future[Try[ProgCompleteResult]]] = {

    val testDataFileContent = Json.prettyPrint(TestDataJsonFormat.dumpTestDataToJson(exercise, completeTestData))

    writeAndCopyFiles(language.fileEnding, implementation, testDataFileContent, solutionTargetDir, exerciseResourcesFolder) map { _ =>

      // FIXME: what if image does not exist?
      val containerResult: Future[RunContainerResult] = DockerConnector.runContainer(
        imageName = language.dockerImageName,
        entryPoint = Seq("timeout", DockerConnector.maxRuntimeInSeconds, DockerConnector.DefaultWorkingDir + "/script." + language.fileEnding),
        dockerBinds = Seq(new DockerBind(solutionTargetDir, DockerConnector.DefaultWorkingDir, AccessMode.rw))
      )

      val futureResults: Future[Try[Seq[ProgEvalResult]]] = containerResult map {

        case RunContainerSuccess => ResultsFileJsonFormat.readResultFile(solutionTargetDir, resultFileName, completeTestData)

        case failure: RunContainerFailure => Failure(failure)

      }

      futureResults map { resultTry: Try[Seq[ProgEvalResult]] =>
        resultTry map (results => ProgCompleteResult(implementation, solutionSaved, results))
      }
    }

  }

  private def writeAndCopyFiles(fileEnding: String, impl: String, testDataFileContent: String, solTargetDir: Path, exerciseResFolder: Path) = for {

    solutionWrite: Path <- write(solTargetDir / s"solution.$fileEnding", impl)

    testDataWrite: Path <- write(solTargetDir / TestDataFile, testDataFileContent)

    scriptCopy: Path <- copy(exerciseResFolder / ScriptName, solTargetDir / ScriptName)

    permissionChange: Path <- Try(Files.setPosixFilePermissions(solTargetDir / ScriptName, filePermissions))

  } yield (solutionWrite, testDataWrite, scriptCopy, permissionChange)

}