package model.programming

import java.nio.file.Path
import java.util.concurrent.TimeUnit

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.model.{Bind, Frame, Volume}
import com.github.dockerjava.core.DockerClientBuilder
import com.github.dockerjava.core.command.{LogContainerResultCallback, PullImageResultCallback, WaitContainerResultCallback}

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

class LogContainerCallback extends LogContainerResultCallback {

  val log: StringBuilder = StringBuilder.newBuilder

  override def onNext(item: Frame): Unit = log ++= new String(item.getPayload)

  override def toString: String = log.toString

}

object DockerConnector {

  val MaxRuntime = 2

  val MaxWaitTimeInSeconds = 3

  val SuccessStatusCode = 0

  val TimeOutStatusCode = 124

  val WorkingDir = "/data"

  private val DockerClient: DockerClient = DockerClientBuilder.getInstance.build

  def imageExists(imageName: String): Boolean = DockerClient.listImagesCmd.exec.asScala map (_.getRepoTags) exists (_ contains imageName)

  def pullImage(imageName: String): Boolean = try {
    DockerClient.pullImageCmd(imageName).exec(new PullImageResultCallback()).awaitCompletion()
    true
  } catch {
    case e: InterruptedException =>
      Thread.currentThread().interrupt()
      false
  }

  private def createContainer(language: ProgLanguage, mountingDir: Path): String = DockerClient.createContainerCmd(language.dockerImageName)
    .withWorkingDir(WorkingDir)
    .withBinds(new Bind(mountingDir.toAbsolutePath.toString, new Volume(WorkingDir)))
    .withEntrypoint("timeout", MaxRuntime + "s", s"$WorkingDir/script." + language.fileEnding)
    .exec.getId

  private def startContainer(container: String): Unit = DockerClient.startContainerCmd(container).exec

  private def waitForContainer(container: String): Try[Int] = Try {
    DockerClient.waitContainerCmd(container)
      .exec(new WaitContainerResultCallback())
      .awaitStatusCode(MaxWaitTimeInSeconds, TimeUnit.SECONDS)
  }

  private def deleteContainer(container: String): Unit = DockerClient.removeContainerCmd(container).exec

  def runContainer(language: ProgLanguage, mountingDir: Path)(implicit ec: ExecutionContext): Future[RunContainerResult] = Future {

    val containerId = createContainer(language, mountingDir)

    // Start container
    startContainer(containerId)

    // Wait for container
    val result: RunContainerResult = waitForContainer(containerId) match {
      case Failure(exception) => RunContainerException(exception.getMessage)

      case Success(statusCode) => statusCode match {
        case SuccessStatusCode => RunContainerSuccess
        case TimeOutStatusCode => RunContainerTimeOut
        case other             => RunContainerError(statusCode, getContainerLogs(containerId))
      }
    }

    deleteContainer(containerId)

    result
  }

  private def getContainerLogs(containerId: String): String = {
    val logsCallBack: LogContainerCallback = DockerClient.logContainerCmd(containerId)
      .withStdErr(true).withStdOut(true)
      .withFollowStream(true)
      .withTailAll()
      .exec(new LogContainerCallback)

    logsCallBack.awaitCompletion(MaxWaitTimeInSeconds, TimeUnit.SECONDS)

    logsCallBack.toString
  }
}