package modules

import model.docker.DockerConnector
import play.api.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object DockerPullsStartTask {

  private val logger = Logger(DockerPullsStartTask.getClass)

  val roseImage             = "beyselein/rose:latest"
  val pythonProgTesterImage = "beyselein/python_prog_tester:extended"
  val pythonUnitTesterImage = "beyselein/py_unit_test_corrector:0.0.1"

  private val imagesToPull: Seq[String] = Seq(roseImage, pythonProgTesterImage, pythonUnitTesterImage)

  def pullImages(): Unit = imagesToPull.filterNot(DockerConnector.imageExists).foreach(image => {
    logger.warn(s"Pulling docker image $image")
    DockerConnector.pullImage(image) map {
      case Success(())    => logger.warn(s"Image $image was pulled successfully.")
      case Failure(error) => logger.error(s"Image $image could not be pulled!", error)
    }
  })

}
