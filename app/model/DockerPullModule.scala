package model

import com.google.inject.AbstractModule
import model.core.{DockerConnector, ScalaDockerImage}
import model.tools.collectionTools.programming.ProgCorrector
import model.tools.collectionTools.rose.RoseCorrector
import play.api.Logger

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class DockerPullModule extends AbstractModule {

  private val logger: Logger                = Logger(classOf[DockerPullModule])
  private implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

  private val imagesToPull: Seq[ScalaDockerImage] = Seq(
    RoseCorrector.roseCorrectionDockerImageName,
    ProgCorrector.programmingSimplifiedCorrectionDockerImageName,
    ProgCorrector.programmingNormalCorrectionDockerImageName,
    ProgCorrector.programmingUnitTestCorrectionDockerImageName
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
