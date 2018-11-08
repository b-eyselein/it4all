package modules

import model.docker.DockerConnector
import play.api.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object DockerPullsStartTask {

  val imagesToPull: Seq[String] = Seq(
    "beyselein/rose:latest",
    "beyselein/python_prog_tester:0.9"
  )

  def pullImages(): Unit = imagesToPull.filterNot(DockerConnector.imageExists).foreach(image => {
    Logger.warn(s"Pulling docker image $image")
    DockerConnector.pullImage(image) map {
      case Success(()) => Logger.warn(s"Image $image was pulled successfully.")
      case Failure(error) => Logger.error(s"Image $image could not be pulled!", error)
    }
  })

}
