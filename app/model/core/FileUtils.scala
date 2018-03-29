package model.core

import java.nio.file._

import scala.collection.JavaConverters._
import scala.util.Try

trait FileUtils {

  implicit class PimpedPath(path: Path) {

    def /(that: Path): Path = Paths.get(path.toString, that.toString)

    def /(that: String): Path = Paths.get(path.toString, that)

  }

  def filesInDirectory(directoryPath: Path): Seq[Path] = Files.newDirectoryStream(directoryPath).asScala.toList

  def subDirectoriesOf(directoryPath: Path): Seq[Path] = filesInDirectory(directoryPath).filter(_.toFile.isDirectory)

  def createEmptyFile(filePath: Path): Try[Path] = Try {
    if (!filePath.toFile.exists()) Files.createFile(filePath)
    else filePath
  }

  def copy(filename: String, sourceDir: Path, targetDir: Path): Try[Path] = copy(sourceDir / filename, targetDir / filename)

  def copy(source: Path, target: Path): Try[Path] = Try {
    if (!target.getParent.toFile.exists)
      Files.createDirectories(target.getParent)

    Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING)
  }

  def move(source: Path, target: Path): Try[Path] = Try {
    if (!target.getParent.toFile.exists)
      Files.createDirectories(target.getParent)

    Files.move(source, target, StandardCopyOption.REPLACE_EXISTING)
  }

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

  def readAllLines(path: Path): Try[Seq[String]] = Try(Files.readAllLines(path).asScala)

  def readAll(path: Path): Try[String] = Try(Files.readAllLines(path).asScala mkString "\n")

}
