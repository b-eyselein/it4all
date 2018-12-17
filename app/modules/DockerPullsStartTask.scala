package modules

import model.docker.DockerConnector
import play.api.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object DockerPullsStartTask {

  val roseImage             = "beyselein/rose:latest"
  val pythonProgTesterImage = "beyselein/python_prog_tester:extended"

  private val imagesToPull: Seq[String] = Seq(roseImage, pythonProgTesterImage)

  def pullImages(): Unit = imagesToPull.filterNot(DockerConnector.imageExists).foreach(image => {
    Logger.warn(s"Pulling docker image $image")
    DockerConnector.pullImage(image) map {
      case Success(())    => Logger.warn(s"Image $image was pulled successfully.")
      case Failure(error) => Logger.error(s"Image $image could not be pulled!", error)
    }
  })

}
