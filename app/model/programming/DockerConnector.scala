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

class LogContainerCallback extends LogContainerResultCallback {

  val log: StringBuilder = StringBuilder.newBuilder

  override def onNext(item: Frame): Unit = log ++= new String(item.getPayload)

  override def toString: String = log.toString

}

object DockerConnector {

  val MaxWaitTimeInSeconds = 2

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
    //    .withName(language.aceName)
    .withWorkingDir(WorkingDir)
    .withBinds(new Bind(mountingDir.toAbsolutePath.toString, new Volume(WorkingDir)))
    .withCmd("./script." + language.fileEnding)
    .exec.getId


  private def startContainer(container: String): Unit = DockerClient.startContainerCmd(container).exec

  private def waitForContainer(container: String): Int = DockerClient.waitContainerCmd(container)
    .exec(new WaitContainerResultCallback()).awaitStatusCode(MaxWaitTimeInSeconds, TimeUnit.SECONDS)

  private def deleteContainer(container: String): Unit = DockerClient.removeContainerCmd(container).exec

  def runContainer(language: ProgLanguage, mountingDir: Path)(implicit ec: ExecutionContext): Future[Either[String, Int]] = Future {

    val containerId = createContainer(language, mountingDir)

    // Start container
    startContainer(containerId)

    // Wait for container
    val statusCode: Int = waitForContainer(containerId)

    if (statusCode == 0) Right(statusCode)
    else {
      val logs: LogContainerCallback = DockerClient.logContainerCmd(containerId)
        .withStdErr(true).withStdOut(true)
        .withFollowStream(true)
        .withTailAll()
        .exec(new LogContainerCallback)

      logs.awaitCompletion(MaxWaitTimeInSeconds, TimeUnit.SECONDS)


      Left(logs.toString)
    }
  }

}
