package model

import java.io.IOException
import java.nio.file.{ CopyOption, Files, OpenOption, Path }

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.util.{ Failure, Success, Try }

import play.Logger

object CommonUtils {

  def createDirectory(directory: Path) = try {
    Files.createDirectories(directory);
    true;
  } catch {
    case e: IOException ⇒
      Logger.error("Error while creating sample file directory \"" + directory.toString() + "\"", e);
      false;
  }

  def readFile(path: Path): Try[String] = try {
    Success(Files.readAllLines(path).asScala.mkString("\n"))
  } catch {
    case e: Throwable ⇒ Failure(e)
  }

  def copyFile(source: Path, target: Path, options: CopyOption): Try[Path] = {
    try {
      Success(Files.copy(source, target, options))
    } catch {
      case e: Throwable ⇒ Failure(e)
    }
  }

  def writeFile(target: Path, content: java.util.List[String], openOption: OpenOption): Try[Path] = {
    try {
      Success(Files.write(target, content, openOption))
    } catch {
      case e: Throwable ⇒ Failure(e)
    }
  }

  def cleanly[A, B](resource: A)(cleanup: A ⇒ Unit)(doWork: A ⇒ B): Try[B] = {
    try {
      Success(doWork(resource))
    } catch {
      case e: Exception ⇒ Failure(e)
    } finally {
      try {
        if (resource != null) {
          cleanup(resource)
        }
      } catch {
        case e: Throwable ⇒ Failure(e)
      }
    }
  }

}