package model

import com.google.inject.AbstractModule
import model.core.{DockerConnector, ScalaDockerImage}
import model.tools.flask.FlaskCorrector
import model.tools.programming.ProgrammingCorrector
import model.tools.rose.RoseCorrector
import play.api.Logger

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class DockerPullModule extends AbstractModule {

  private val logger: Logger                = Logger(classOf[DockerPullModule])
  private implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

  private val imagesToPull: Seq[ScalaDockerImage] = Seq(
    FlaskCorrector.flaskCorrectionDockerImage,
    ProgrammingCorrector.programmingCorrectionDockerImage,
    RoseCorrector.roseCorrectionDockerImageName
  )

  override def configure(): Unit = {
    imagesToPull
      .filterNot(DockerConnector.imageExists)
      .foreach(image => {
        logger.warn(s"Pulling docker image $image")

        DockerConnector.pullImage(image).map {
          case Success(_)     => logger.warn(s"Image $image was pulled successfully.")
          case Failure(error) => logger.error(s"Image $image could not be pulled!", error)
        }
      })
  }

}
