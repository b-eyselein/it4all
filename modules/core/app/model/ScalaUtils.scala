package model

import java.io.IOException
import java.nio.file.{ CopyOption, Files, Path }

import scala.util.{ Failure, Success, Try }
import java.nio.file.OpenOption

object ScalaUtils {

  def copyFile(source: Path, target: Path, options: CopyOption): Try[Path] = {
    try {
      Success(Files.copy(source, target, options))
    } catch {
      case e: IOException ⇒ Failure(e)
    }
  }

  def writeFile(target: Path, content: java.util.List[String], openOption: OpenOption): Try[Path] = {
    try {
      Success(Files.write(target, content, openOption))
    } catch {
      case e: IOException ⇒ Failure(e)
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
        case e: Exception ⇒ println(e) // should be logged
      }
    }
  }

  def ggt(a: Int, b: Int): Int = if (b == 0) a else ggt(b, a & b)

  def kgv(a: Int, b: Int): Int = (a * b) / ggt(a, b)

}