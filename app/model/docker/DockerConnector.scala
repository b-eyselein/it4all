package model.docker

import java.util.concurrent.TimeUnit

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.model.{Bind, Frame, Image}
import com.github.dockerjava.core.DockerClientBuilder
import com.github.dockerjava.core.command.{LogContainerResultCallback, PullImageResultCallback, WaitContainerResultCallback}
import play.api.Logger

import scala.collection.JavaConverters._
import scala.collection.mutable
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

  val DefaultWorkingDir = "/data"

  private val DockerClient: DockerClient = DockerClientBuilder.getInstance.build

  def imageExists(imageName: String): Boolean = {
    val images: mutable.Seq[Image] = DockerClient.listImagesCmd.exec.asScala

    images map (_.getRepoTags) filter (_ != null) exists (_ contains imageName)
  }

  def pullImage(imageName: String)(implicit ec: ExecutionContext): Future[Boolean] =
    Future(Try(DockerClient.pullImageCmd(imageName).exec(new PullImageResultCallback()).awaitCompletion()) match {
      case Success(_) => true
      case Failure(_) => false
    })

  private def createContainer(imageName: String, workingDir: String, entrypoint: Seq[String], binds: Seq[Bind] = Seq.empty): Try[String] = Try {
    DockerClient.createContainerCmd(imageName)
      .withWorkingDir(workingDir)
      .withBinds(binds.asJava)
      .withEntrypoint(entrypoint.asJava)
      .exec.getId
  }

  private def startContainer(container: String): Try[Unit] = Try(DockerClient.startContainerCmd(container).exec)

  private def waitForContainer(container: String, waitTimeInSeconds: Int): Try[Int] = Try {
    DockerClient.waitContainerCmd(container)
      .exec(new WaitContainerResultCallback())
      .awaitStatusCode(waitTimeInSeconds, TimeUnit.SECONDS)
  }

  private def deleteContainer(container: String): Try[Unit] = Try(DockerClient.removeContainerCmd(container).exec)

  def runContainer(imageName: String, entryPoint: Seq[String], dockerBinds: Seq[DockerBind] = Seq.empty, workingDir: String = DefaultWorkingDir,
                   maxWaitTimeInSeconds: Int = MaxWaitTimeInSeconds, deleteContainerAfterRun: Boolean = true)
                  (implicit ec: ExecutionContext): Future[RunContainerResult] = Future {

    createContainer(imageName, workingDir, entryPoint, dockerBinds map (_.toBind)) match {
      case Failure(e)           => CreateContainerException(e)
      case Success(containerId) =>

        startContainer(containerId) match {
          case Failure(e) => StartContainerException(e)
          case Success(_) =>

            waitForContainer(containerId, maxWaitTimeInSeconds) match {
              case Failure(e)               => WaitContainerException(e)
              case Success(statusCode: Int) =>

                val result: RunContainerResult = statusCode match {
                  case SuccessStatusCode => RunContainerSuccess
                  case TimeOutStatusCode => RunContainerTimeOut
                  case _                 => RunContainerError(statusCode, getContainerLogs(containerId, maxWaitTimeInSeconds))
                }

                if (deleteContainerAfterRun) {
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

  private def getContainerLogs(containerId: String, maxWaitTimeInSeconds: Int): String = {
    val logsCallBack: LogContainerCallback =
      DockerClient.logContainerCmd(containerId)
        .withStdErr(true).withStdOut(true)
        .withFollowStream(true)
        .withTailAll()
        .exec(new LogContainerCallback)

    logsCallBack.awaitCompletion(maxWaitTimeInSeconds, TimeUnit.SECONDS)

    logsCallBack.toString
  }
}
