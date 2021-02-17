package model.tools

import better.files.File
import model.core.{DockerBind, DockerConnector, RunContainerResult, ScalaDockerImage}
import play.api.libs.json.{Json, Reads}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

final case class DockerCorrectionExecutionError(logs: String)
    extends Exception(s"There has been an error executing a docker container:\n$logs")

trait DockerExecutionCorrector {

  protected val resultFileName     = "result.json"
  protected val testConfigFileName = "testConfig.json"

  protected val baseBindPath: File = DockerConnector.DefaultWorkingDir

  protected val dockerImage: ScalaDockerImage

  def readDockerExecutionResultFile[A](targetFile: File, jsonFormat: Reads[A]): Try[A] =
    for {
      fileContent <- Try(targetFile.contentAsString)
      jsValue     <- Try(Json.parse(fileContent))
      result <-
        jsonFormat
          .reads(jsValue)
          .fold(
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
  )(
    convertResult: T => R
  )(implicit ec: ExecutionContext): Future[Try[R]] = Future(DockerConnector.imageExists(dockerImage.name)).flatMap {
    case false => Future.successful(Failure(new Exception(s"The docker image does ${dockerImage.name} not exist!")))
    case true =>
      DockerConnector
        .runContainer(
          dockerImage.name,
          maybeDockerBinds = dockerBinds,
          maybeCmd = maybeCmd,
          deleteContainerAfterRun = deleteContainerAfterRun
        )
        .map { case Success(RunContainerResult(statusCode, logs)) =>
          statusCode match {
            case 0 => readDockerExecutionResultFile(resultFile, jsFormat).map(convertResult)
            case _ => Failure(DockerCorrectionExecutionError(logs))
          }
        }
  }

}
