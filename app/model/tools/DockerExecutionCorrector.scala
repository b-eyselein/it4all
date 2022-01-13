package model.tools

import better.files.File
import model.core.{DockerBind, DockerConnector, RunContainerResult, ScalaDockerImage}
import play.api.libs.json.{Json, Reads}
import sangria.execution.UserFacingError

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

final case class DockerCorrectionExecutionError(logs: String)
    extends Exception(s"There has been an error executing a docker container:\n$logs")
    with UserFacingError

trait DockerExecutionCorrector {

  protected val resultFileName     = "result.json"
  protected val testConfigFileName = "testConfig.json"

  protected val baseBindPath: File = File("/data")

  protected val dockerImage: ScalaDockerImage

  def readDockerExecutionResultFile[A](targetFile: File, jsonFormat: Reads[A]): Try[A] = for {
    fileContent <- Try { targetFile.contentAsString }
    jsValue     <- Try { Json.parse(fileContent) }

    readJson = jsonFormat.reads(jsValue)

    result <- readJson.fold(
      _ => Failure(new Exception("There has been an error reading a json result file!")),
      result => Success(result)
    )
  } yield result

  protected def runContainer[T, R](
    dockerBinds: Seq[DockerBind],
    jsFormat: Reads[T],
    resultFile: File,
    maybeCmd: Option[Seq[String]] = None,
    deleteContainerAfterRun: Int => Boolean = _ => true
  )(convertResult: T => R)(implicit ec: ExecutionContext): Future[Try[R]] = for {
    imageExists <- Future { DockerConnector.imageExists(dockerImage.name) }

    () <-
      if (imageExists) {
        Future.successful(())
      } else {
        Future.failed(new Exception(s"The docker image does ${dockerImage.name} not exist!"))
      }

    RunContainerResult(statusCode, logs) <- DockerConnector.runContainer(
      dockerImage.name,
      maybeDockerBinds = dockerBinds,
      maybeCmd = maybeCmd,
      deleteContainerAfterRun = deleteContainerAfterRun
    )
  } yield statusCode match {
    case 0 => readDockerExecutionResultFile(resultFile, jsFormat).map(convertResult)
    case _ => Failure(DockerCorrectionExecutionError(logs))
  }

}
