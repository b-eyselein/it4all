package model.programming

import java.nio.file.attribute.{PosixFilePermission, PosixFilePermissions}
import java.nio.file.{Files, Path}

import com.github.dockerjava.api.model.AccessMode
import model.core.FileUtils
import model.docker.DockerConnector.MaxRuntime
import model.docker._
import model.{JsonFormat, User}
import play.api.Logger
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object ProgrammingCorrector extends FileUtils with JsonFormat {

  val ScriptName = "script.py"

  val ImplementationsFolder = "implementation"

  val ValidationFolder = "validation"

  val TestDataFile = "testconfig.json"

  val resultFileName = "result.json"

  val FilePermissions: java.util.Set[PosixFilePermission] = PosixFilePermissions.fromString("rwxrwxrwx")


  def validateTestdata(user: User, exercise: ProgCompleteEx, implementation: String, solSaved: Boolean, language: ProgLanguage,
                       testData: Seq[CompleteTestData], solutionTargetDir: Path, exerciseResourcesFolder: Path)
                      (implicit ec: ExecutionContext): Future[ProgValidationCompleteResult] = {

    correct(exercise, language, solutionTargetDir, implementation, testData, exerciseResourcesFolder) map {
      res => ProgValidationCompleteResult(implementation, solSaved, res)
    }

  }

  def correctImplementation(user: User, exercise: ProgCompleteEx, implementation: String, solSaved: Boolean, language: ProgLanguage,
                            testData: Seq[CompleteTestData], solutionTargetDir: Path, exerciseResourcesFolder: Path)
                           (implicit ec: ExecutionContext): Future[ProgCompleteResult] = {

    correct(exercise, language, solutionTargetDir, implementation, testData, exerciseResourcesFolder) map {
      res => ProgImplementationCompleteResult(implementation, solSaved, res)
    }

  }


  private def correct(exercise: ProgCompleteEx, language: ProgLanguage, targetDir: Path, script: String, completeTestData: Seq[CompleteTestData], exerciseResourcesFolder: Path)
                     (implicit ec: ExecutionContext): Future[Seq[ProgEvalResult]] = {

    val scriptTargetPath = targetDir / ScriptName

    val futureImageExists: Future[Boolean] = Future(DockerConnector.imageExists(language.dockerImageName))

    val workingDir = DockerConnector.DefaultWorkingDir

    val fileTries: Try[(Path, Path, Path, Path)] = for {
      solutionWrite: Path <- write(targetDir / s"solution.${language.fileEnding}", script)

      testDataWrite: Path <- write(targetDir / TestDataFile, Json.prettyPrint(TestDataJsonFormat.dumpTestDataToJson(exercise.ex, exercise.inputTypes, completeTestData)))

      scriptCopy: Path <- copy(exerciseResourcesFolder / ScriptName, scriptTargetPath)

      permissionChange: Path <- Try(Files.setPosixFilePermissions(scriptTargetPath, FilePermissions))
    } yield (solutionWrite, testDataWrite, scriptCopy, permissionChange)

    fileTries match {
      case Failure(error) =>
        Logger.error("There has been an error writing a file: ", error)
        Future(Seq.empty)
      case Success(_)     =>
        // Check if image exists
        futureImageExists flatMap {
          _ =>
            DockerConnector.runContainer(
              imageName = language.dockerImageName,
              entryPoint = Seq("timeout", MaxRuntime + "s", s"$workingDir/script." + language.fileEnding),
              dockerBinds = Seq(new DockerBind(targetDir, workingDir, AccessMode.rw))
            )
        } map {
          // Error while waiting for container
          case exc: RunContainerException =>
            Logger.error("Error while running container:", exc.error)
            Seq(ProgEvalFailed)

          case RunContainerTimeOut => Seq(TimeOut)

          // Error while running script with status code other than 0 or 124 (from timeout!)
          case RunContainerError(_, msg) => Seq(SyntaxError(reason = msg))

          case RunContainerSuccess => ResultsFileJsonFormat.readResultFile(targetDir, resultFileName, completeTestData)
        }
    }
  }


}