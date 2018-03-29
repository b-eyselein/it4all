package model.docker

import java.nio.file.Path

import com.github.dockerjava.api.model.{AccessMode, Bind, Volume}
import model.core.FileUtils

object DockerBindUtils extends FileUtils {

  def mountFileByName(sourceDir: Path, targetDir: Path, fileName: String, accessMode: AccessMode = AccessMode.rw): DockerBind =
    DockerBind(sourceDir / fileName, new Volume((targetDir / fileName).toString), accessMode)

  def mountFileTo(sourceFile: Path, targetFileString: String, accessMode: AccessMode = AccessMode.rw): DockerBind =
    DockerBind(sourceFile, new Volume(targetFileString), accessMode)

}

case class DockerBind(path: Path, volume: Volume, accessMode: AccessMode) {

  def toBind = new Bind(path.toAbsolutePath.toString, volume, accessMode)

}
