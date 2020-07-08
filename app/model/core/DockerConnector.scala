package model.core

import better.files.File
import com.spotify.docker.client.DockerClient.LogsParam
import com.spotify.docker.client.messages.HostConfig.Bind
import com.spotify.docker.client.messages.{ContainerConfig, ContainerCreation, ContainerExit, HostConfig}
import com.spotify.docker.client.{DefaultDockerClient, DockerClient}
import play.api.Logger

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters._
import scala.util.Try

final case class DockerBind(fromPath: File, toPath: String, isReadOnly: Boolean = false) {

  def toBind: Bind =
    Bind
      .from(fromPath.path.toAbsolutePath.toString)
      .to(toPath)
      .readOnly(isReadOnly)
      .build()

}

final case class ScalaDockerImage(repo: String, image: String, version: String = "latest") {

  def name: String = s"$repo/$image:$version"

}

final case class RunContainerResult(statusCode: Int, logs: String)

object DockerConnector {

  private val logger: Logger = Logger(DockerConnector.getClass)

  private val SuccessStatusCode: Int = 0

  val DefaultWorkingDir = "/data"

  private val dockerClient: DockerClient = DefaultDockerClient.fromEnv().build()

  def imageExists(scalaDockerImage: ScalaDockerImage): Boolean =
    imageExists(scalaDockerImage.name)

  def imageExists(imageName: String): Boolean = {
    // FIXME: run in future?
    dockerClient
      .listImages()
      .asScala
      .map(_.repoTags)
      .filter(_ != null)
      .exists(_.contains(imageName))
  }

  def pullImage(scalaDockerImage: ScalaDockerImage)(implicit ec: ExecutionContext): Future[Try[Unit]] =
    Future(Try(dockerClient.pull(scalaDockerImage.name)))

  def pullImage(imageName: String)(implicit ec: ExecutionContext): Future[Try[Unit]] =
    Future(Try(dockerClient.pull(imageName)))

  private def createHostConfig(binds: Seq[DockerBind]): HostConfig =
    HostConfig.builder().binds(binds.map(_.toBind.toString).asJava).build()

  private def createContainer(
    imageName: String,
    maybeWorkingDir: Option[String],
    maybeEntryPoint: Option[Seq[String]] = None,
    maybeCmd: Option[Seq[String]] = None,
    maybeBinds: Seq[DockerBind] = Seq.empty
  ): Try[ContainerCreation] = {
    val ccb: ContainerConfig.Builder = ContainerConfig.builder().image(imageName)

    val ccbWithHostConfig = maybeBinds.toList match {
      case Nil   => ccb
      case binds => ccb.hostConfig(createHostConfig(binds))
    }

    val ccbWithHostConfigWorkDir = maybeWorkingDir match {
      case None          => ccbWithHostConfig
      case Some(workDir) => ccbWithHostConfig.workingDir(workDir)
    }

    val ccbWithHostConfigWorkDirEntryPoint = maybeEntryPoint match {
      case None             => ccbWithHostConfigWorkDir
      case Some(entryPoint) => ccbWithHostConfigWorkDir.entrypoint(entryPoint.asJava)
    }

    val ccbWithHostConfigWorkDirEntryPointCmd = maybeCmd match {
      case None      => ccbWithHostConfigWorkDirEntryPoint
      case Some(cmd) => ccbWithHostConfigWorkDirEntryPoint.cmd(cmd.asJava)
    }

    Try {
      val containerConfig: ContainerConfig = ccbWithHostConfigWorkDirEntryPointCmd.build()

      dockerClient.createContainer(containerConfig)
    }
  }

  private def startContainer(container: String): Try[Unit] = Try(dockerClient.startContainer(container))

  private def waitForContainer(container: String): Try[ContainerExit] = Try(dockerClient.waitContainer(container))

  private def deleteContainer(container: String): Try[Unit] = Try(dockerClient.removeContainer(container))

  def runContainer(
    imageName: String,
    maybeWorkingDir: Option[String] = Some(DefaultWorkingDir),
    maybeEntryPoint: Option[Seq[String]] = None,
    maybeCmd: Option[Seq[String]] = None,
    maybeDockerBinds: Seq[DockerBind] = Seq.empty,
    deleteContainerAfterRun: Boolean = true
  )(implicit ec: ExecutionContext): Future[Try[RunContainerResult]] =
    Future {

      createContainer(imageName, maybeWorkingDir, maybeEntryPoint, maybeCmd, maybeDockerBinds).flatMap {
        containerCreation: ContainerCreation =>
          val containerId = containerCreation.id

          startContainer(containerId).flatMap { _ =>
            waitForContainer(containerId).map { containerExit =>
              val statusCode = containerExit.statusCode.toInt

              val result: RunContainerResult =
                RunContainerResult(statusCode, getContainerLogs(containerId))

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

  private def getContainerLogs(containerId: String): String =
    dockerClient
      .logs(
        containerId,
        LogsParam.stdout(),
        LogsParam.stderr()
      )
      .readFully()

}
