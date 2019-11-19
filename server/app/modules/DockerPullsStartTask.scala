package modules

import model.DockerConnector
import model.tools.programming.ProgCorrector
import model.tools.rose.RoseCorrector
import play.api.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object DockerPullsStartTask {

  private val logger = Logger(DockerPullsStartTask.getClass)

  private val imagesToPull: Seq[String] = Seq(
    RoseCorrector.roseCorrectionDockerImageName,
    ProgCorrector.programmingSimplifiedCorrectionDockerImageName,
    ProgCorrector.programmingNormalCorrectionDockerImageName,
    ProgCorrector.programmingUnitTestCorrectionDockerImageName
  )

  def pullImages(): Unit = imagesToPull
    .filterNot(DockerConnector.imageExists)
    .foreach(image => {
      logger.warn(s"Pulling docker image $image")

      DockerConnector.pullImage(image).map {
        case Success(_)     => logger.warn(s"Image $image was pulled successfully.")
        case Failure(error) => logger.error(s"Image $image could not be pulled!", error)
      }
    })

}
