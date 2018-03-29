package modules

import model.docker.DockerConnector
import play.api.Logger

import scala.concurrent.ExecutionContext.Implicits.global

object DockerPullsStartTask {

  val imagesToPull: Seq[String] = Seq(
    "beyselein/rose:latest",
    "beyselein/python_prog_tester:latest"
  )

  def pullImages(): Unit = imagesToPull.filterNot(DockerConnector.imageExists).foreach(image => {
    Logger.warn(s"Pulling docker image $image")
    DockerConnector.pullImage(image) map {
      case true  => Logger.warn(s"Image $image was pulled successfully.")
      case false => Logger.error(s"Image $image could not be pulled!")
    }
  })

}
