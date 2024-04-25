package model.core

import better.files.File
import org.mandas.docker.client.DockerClient.LogsParam
import org.mandas.docker.client.messages.{ContainerConfig, ContainerCreation, ContainerExit, HostConfig}
import org.mandas.docker.client.{DefaultDockerClient, DockerClient}

import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters._
import org.mandas.docker.client.builder.resteasy.ResteasyDockerClientBuilder

final case class DockerBind(fromPath: File, toPath: File, isReadOnly: Boolean = false) {

  def toBind: HostConfig.Bind = HostConfig.Bind
    .builder()
    .from(fromPath.path.toAbsolutePath.toString)
    .to(toPath.path.toAbsolutePath.toString)
    .readOnly(isReadOnly)
    .build()

}

final case class ScalaDockerImage(repo: String, image: String, version: String = "latest") {

  def name: String = s"$repo/$image:$version"

}

final case class RunContainerResult(statusCode: Int, logs: String)

object DockerConnector {

  private val dockerClient: DockerClient = new ResteasyDockerClientBuilder().fromEnv().build()

  def imageExists(scalaDockerImage: ScalaDockerImage): Boolean = imageExists(scalaDockerImage.name)

  def imageExists(imageName: String): Boolean = dockerClient
    .listImages()
    .asScala
    .map(_.repoTags)
    .filter(_ != null)
    .exists(_.contains(imageName))

  def pullImage(scalaDockerImage: ScalaDockerImage)(implicit ec: ExecutionContext): Future[Unit] = Future { dockerClient.pull(scalaDockerImage.name) }

  private def createContainer(
    imageName: String,
    maybeWorkingDir: Option[String],
    maybeEntryPoint: Option[Seq[String]] /*= None*/,
    maybeCmd: Option[Seq[String]] /*= None*/,
    binds: Seq[DockerBind] /*= Seq.empty*/
  )(implicit ec: ExecutionContext): Future[ContainerCreation] = Future {

    val hostConfig = HostConfig
      .builder()
      .binds(binds.map(_.toBind.toString).asJava)
      .build()

    val containerConfig: ContainerConfig = ContainerConfig
      .builder()
      .image(imageName)
      .hostConfig(hostConfig)
      .workingDir(maybeWorkingDir.orNull)
      .entrypoint(maybeEntryPoint.map(_.asJava).orNull)
      .cmd(maybeCmd.map(_.asJava).orNull)
      .build()

    dockerClient.createContainer(containerConfig)
  }

  private def startContainer(container: String)(implicit ec: ExecutionContext): Future[Unit] = Future { dockerClient.startContainer(container) }

  private def waitForContainer(container: String)(implicit ec: ExecutionContext): Future[ContainerExit] = Future { dockerClient.waitContainer(container) }

  private def deleteContainer(container: String)(implicit ec: ExecutionContext): Future[Unit] = Future { dockerClient.removeContainer(container) }

  private def getContainerLogs(container: String)(implicit ec: ExecutionContext): Future[String] = Future {
    dockerClient
      .logs(container, LogsParam.stdout(), LogsParam.stderr())
      .readFully()
  }

  def runContainer(
    imageName: String,
    maybeWorkingDir: Option[String] = None,
    maybeEntryPoint: Option[Seq[String]] = None,
    maybeCmd: Option[Seq[String]] = None,
    maybeDockerBinds: Seq[DockerBind] = Seq.empty,
    deleteContainerAfterRun: Int => Boolean = _ => true
  )(implicit ec: ExecutionContext): Future[RunContainerResult] = for {
    containerCreation <- createContainer(imageName, maybeWorkingDir, maybeEntryPoint, maybeCmd, maybeDockerBinds)

    containerId = containerCreation.id

    () <- startContainer(containerId)

    containerExit <- waitForContainer(containerId)

    statusCode = containerExit.statusCode.toInt

    logs <- getContainerLogs(containerId)

    () <-
      if (deleteContainerAfterRun(statusCode)) {
        deleteContainer(containerId)
      } else {
        Future.successful(())
      }

  } yield RunContainerResult(statusCode, logs)

}
