package model.docker

import java.nio.file.{Path, Paths}

import better.files.File
import com.spotify.docker.client.DockerClient.LogsParam
import com.spotify.docker.client.messages.HostConfig.Bind
import com.spotify.docker.client.messages.{ContainerConfig, ContainerCreation, ContainerExit, HostConfig}
import com.spotify.docker.client.{DefaultDockerClient, DockerClient}
import play.api.Logger

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

final case class DockerBind(fromPath: File, toPath: File, isReadOnly: Boolean = false) {

  def toBind: Bind = Bind
    .from(fromPath.path.toAbsolutePath.toString)
    .to(toPath.path.toAbsolutePath.toString)
    .readOnly(isReadOnly).build()

}

object DockerConnector {

  val MaxWaitTimeInSeconds: Int = 3

  val SuccessStatusCode: Int = 0
  val TimeOutStatusCode: Int = 124

  val DefaultWorkingDir: Path = Paths.get("/data")

  private val dockerClient: DockerClient = DefaultDockerClient.fromEnv().build()

  def imageExists(imageName: String): Boolean = {
    // FIXME: run in future?
    dockerClient.listImages().asScala map (_.repoTags) filter (_ != null) exists (_ contains imageName)
  }

  def pullImage(imageName: String)(implicit ec: ExecutionContext): Future[Try[Unit]] =
    Future(Try(dockerClient.pull(imageName)))

  private def createContainer(imageName: String, workingDir: String, entryPoint: Seq[String], binds: Seq[DockerBind]): Try[ContainerCreation] = Try {

    val hostConfig: HostConfig = HostConfig.builder().binds(binds map (_.toBind.toString) asJava).build()

    val containerConfig: ContainerConfig = ContainerConfig.builder()
      .image(imageName)
      .hostConfig(hostConfig)
      .workingDir(workingDir)
      .entrypoint(entryPoint asJava)
      .build()

    dockerClient.createContainer(containerConfig)
  }

  private def createContainer(imageName: String, workingDir: String, binds: Seq[DockerBind]): Try[ContainerCreation] = Try {

    val hostConfig: HostConfig = HostConfig.builder().binds(binds map (_.toBind.toString) asJava).build()

    val containerConfig: ContainerConfig = ContainerConfig.builder()
      .image(imageName)
      .workingDir(workingDir)
      .hostConfig(hostConfig)
      .build()

    dockerClient.createContainer(containerConfig)
  }

  private def startContainer(container: String): Try[Unit] = Try(dockerClient.startContainer(container))

  private def waitForContainer(container: String, waitTimeInSeconds: Int): Try[ContainerExit] = Try(dockerClient.waitContainer(container))

  private def deleteContainer(container: String): Try[Unit] = Try(dockerClient.removeContainer(container))

  def runContainer(imageName: String, maybeEntryPoint: Option[Seq[String]] = None, dockerBinds: Seq[DockerBind] = Seq[DockerBind](), workingDir: Path = DefaultWorkingDir,
                   maxWaitTimeInSeconds: Int = MaxWaitTimeInSeconds, deleteContainerAfterRun: Boolean = true)
                  (implicit ec: ExecutionContext): Future[RunContainerResult] = Future {

    val createdContainer = maybeEntryPoint match {
      case None             => createContainer(imageName, workingDir.toString, dockerBinds)
      case Some(entryPoint) => createContainer(imageName, workingDir.toString, entryPoint, dockerBinds)
    }

    createdContainer match {
      case Failure(e)                 => CreateContainerException(e)
      case Success(containerCreation) =>

        val containerId = containerCreation.id

        startContainer(containerId) match {
          case Failure(e) => StartContainerException(e)
          case Success(_) =>

            waitForContainer(containerId, maxWaitTimeInSeconds) match {
              case Failure(e)             => WaitContainerException(e)
              case Success(containerExit) =>

                val statusCode = containerExit.statusCode toInt

                val result: RunContainerResult = statusCode match {
                  case SuccessStatusCode => RunContainerSuccess
                  case TimeOutStatusCode => RunContainerTimeOut(MaxWaitTimeInSeconds)
                  case _                 =>
                    Logger.info("Container statusCode: " + statusCode.toString)
                    RunContainerError(statusCode, getContainerLogs(containerId, maxWaitTimeInSeconds))
                }

                if (deleteContainerAfterRun && statusCode == SuccessStatusCode) {
                  // Do not delete failed containers for now
                  val containerDeleted = deleteContainer(containerId)
                  if (containerDeleted.isFailure)
                    Logger.error("Could not delete container!")
                } else {
                  Logger.debug("NOT Deleting container...")
                }

                result
            }
        }
    }
  }

  private def getContainerLogs(containerId: String, maxWaitTimeInSeconds: Int): String =
    dockerClient.logs(containerId, LogsParam.stdout(), LogsParam.stderr()).readFully()
}
