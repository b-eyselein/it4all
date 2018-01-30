package model.docker

import java.nio.file.Path

import com.github.dockerjava.api.model.{AccessMode, Volume, Bind}

case class DockerBind(path: Path, volume: Volume, accessMode: AccessMode) {

  def this(path: Path, volumeAsString: String, accessMode: AccessMode = AccessMode.rw) = this(path, new Volume(volumeAsString), accessMode)

  def toBind = new Bind(path.toAbsolutePath.toString, volume, accessMode)

}
