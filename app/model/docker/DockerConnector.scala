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
import scala.util.{Failure, Success, Try}

final case class DockerBind(fromPath: File, toPath: File, isReadOnly: Boolean = false) {

  def toBind: Bind = Bind
    .from(fromPath.path.toAbsolutePath.toString)
    .to(toPath.path.toAbsolutePath.toString)
    .readOnly(isReadOnly).build()

}

object DockerConnector {

  private val logger: Logger = Logger("model.docker.DockerConnector")

  private val MaxWaitTimeInSeconds: Int = 3

  private val SuccessStatusCode: Int = 0
  private val TimeOutStatusCode: Int = 124

  val DefaultWorkingDir: Path = Paths.get("/data")

  private val dockerClient: DockerClient = DefaultDockerClient.fromEnv().build()

  def imageExists(imageName: String): Boolean = {
    // FIXME: run in future?
    dockerClient.listImages().asScala map (_.repoTags) filter (_ != null) exists (_ contains imageName)
  }

  def pullImage(imageName: String)(implicit ec: ExecutionContext): Future[Try[Unit]] =
    Future(Try(dockerClient.pull(imageName)))

  private def createHostConfig(binds: Seq[DockerBind]): HostConfig =
    HostConfig.builder().binds(binds.map(_.toBind.toString).asJava).build()

  private def createContainer(
    imageName: String,
    maybeWorkingDir: Option[String],
    maybeEntryPoint: Option[Seq[String]] = None,
    maybeCmd: Option[Seq[String]] = None,
    maybeBinds: Option[Seq[DockerBind]] = None
  ): Try[ContainerCreation] = Try {

    // Build container config
    val ccb: ContainerConfig.Builder = ContainerConfig.builder().image(imageName)

    val ccbWithHostConfig = maybeBinds match {
      case None        => ccb
      case Some(binds) => ccb.hostConfig(createHostConfig(binds))
    }

    val ccbWithHostConfigWorkDir = maybeWorkingDir match {
      case None          => ccbWithHostConfig
      case Some(workDir) => ccbWithHostConfig.workingDir(workDir)
    }

    val ccbWithHostConfigWorkDirEntryPoint = maybeEntryPoint match {
      case None             => ccbWithHostConfigWorkDir
      case Some(entryPoint) => ccbWithHostConfigWorkDir.entrypoint(entryPoint asJava)
    }

    val ccbWithHostConfigWorkDirEntryPointCmd = maybeCmd match {
      case None      => ccbWithHostConfigWorkDirEntryPoint
      case Some(cmd) => ccbWithHostConfigWorkDirEntryPoint.cmd(cmd asJava)
    }

    val containerConfig: ContainerConfig = ccbWithHostConfigWorkDirEntryPointCmd.build()

    dockerClient.createContainer(containerConfig)
  }

  private def startContainer(container: String): Try[Unit] = Try(dockerClient.startContainer(container))

  private def waitForContainer(container: String, waitTimeInSeconds: Int): Try[ContainerExit] = Try(dockerClient.waitContainer(container))

  private def deleteContainer(container: String): Try[Unit] = Try(dockerClient.removeContainer(container))

  def runContainer(imageName: String, maybeWorkingDir: Option[Path] = Some(DefaultWorkingDir), maybeEntryPoint: Option[Seq[String]] = None,
                   maybeCmd: Option[Seq[String]] = None, maybeDockerBinds: Option[Seq[DockerBind]] = None,
                   maxWaitTimeInSeconds: Int = MaxWaitTimeInSeconds, deleteContainerAfterRun: Boolean = true
                  )
                  (implicit ec: ExecutionContext): Future[RunContainerResult] = Future {

    createContainer(imageName, maybeWorkingDir map (_.toString), maybeEntryPoint, maybeCmd, maybeDockerBinds) match {
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
                    logger.info("Container statusCode: " + statusCode.toString)
                    RunContainerError(statusCode, getContainerLogs(containerId, maxWaitTimeInSeconds))
                }

                if (deleteContainerAfterRun && statusCode == SuccessStatusCode) {
                  // Do not delete failed containers for now
                  val containerDeleted = deleteContainer(containerId)
                  if (containerDeleted.isFailure) logger.error("Could not delete container!")
                } else {
                  logger.debug("NOT Deleting container...")
                }

                result
            }
        }
    }
  }

  private def getContainerLogs(containerId: String, maxWaitTimeInSeconds: Int): String =
    dockerClient.logs(containerId, LogsParam.stdout(), LogsParam.stderr()).readFully()
}
