package model.docker

import java.nio.file.Path

import com.spotify.docker.client.messages.HostConfig.Bind
import model.core.FileUtils

object DockerBindUtils extends FileUtils {

//  def mountFileByName(sourceDir: Path, targetDir: Path, fileName: String, accessMode: AccessMode = AccessMode.rw): DockerBind =
//    DockerBind(sourceDir / fileName, new Volume((targetDir / fileName).toString), accessMode)
//
//  def mountFileTo(sourceFile: Path, targetFileString: String, accessMode: AccessMode = AccessMode.rw): DockerBind =
//    DockerBind(sourceFile, new Volume(targetFileString), accessMode)

}

case class DockerBind(fromPath: Path, toPath: Path, isReadOnly: Boolean = false) {

  def toBind: Bind = Bind.from(fromPath.toAbsolutePath.toString).to(toPath.toString).readOnly(isReadOnly).build()

}
