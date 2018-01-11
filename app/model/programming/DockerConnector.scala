package model.programming

import java.nio.file.Path
import java.util.concurrent.TimeUnit

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.model.{Bind, Volume}
import com.github.dockerjava.core.DockerClientBuilder
import com.github.dockerjava.core.command.{PullImageResultCallback, WaitContainerResultCallback}

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

object DockerConnector {

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
    .withCmd("./" + language.aceName + ".sh")
    .exec.getId

  private def startContainer(container: String): Unit = DockerClient.startContainerCmd(container).exec

  private def waitForContainer(container: String): Int = DockerClient.waitContainerCmd(container)
    .exec(new WaitContainerResultCallback()).awaitStatusCode(2, TimeUnit.SECONDS)

  private def deleteContainer(container: String): Unit = DockerClient.removeContainerCmd(container).exec

  def runContainer(language: ProgLanguage, mountingDir: Path)(implicit ec: ExecutionContext): Future[Int] = Future {

    val containerId = createContainer(language, mountingDir)

    // Start container
    startContainer(containerId)

    // Wait for container
    val statusCode: Int = waitForContainer(containerId)

    //  remove container
//    deleteContainer(containerId)

    statusCode
  }

}
