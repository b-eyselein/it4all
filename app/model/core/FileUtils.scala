package model.core

import java.io.File
import java.nio.file.{Files, Path, Paths, StandardOpenOption}

import com.google.common.io.{Files => GFiles}

import scala.collection.JavaConverters._
import scala.util.Try

trait FileUtils {

  private def fileEnding(file: File): String = GFiles.getFileExtension(file.getName)

  def write(path: Path, toWrite: String): Try[Path] = Try {
    if (!path.getParent.toFile.exists)
      Files.createDirectories(path.getParent)

    Files.write(path, toWrite.getBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
  }

  def write(directory: Path, filename: String, toWrite: String): Try[Path] = Try {
    if (!directory.toFile.exists())
      Files.createDirectories(directory)

    Files.write(Paths.get(directory.toString, filename), toWrite.getBytes,
      StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
  }

  def readAll(path: Path): Try[String] = Try(Files.readAllLines(path).asScala.mkString("\n"))

}
