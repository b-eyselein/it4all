package model.core

import java.io.File
import java.nio.file.{Files, Path}

import com.google.common.io.{Files => GFiles}

import scala.collection.JavaConverters._
import scala.util.Try

object FileUtils {


  def fileEnding(file: File): String = GFiles.getFileExtension(file.getName)

  def write(path: Path, toWrite: String): Try[Unit] = Try {
    println("Writing file " + path.toAbsolutePath)

    val parentDir = path.getParent

    if (!parentDir.toFile.exists)
      Files.createDirectories(path.getParent)

    val toWriteJL = toWrite.split("\n").toList.asJava

    Files.write(path, toWriteJL)
  }


}
