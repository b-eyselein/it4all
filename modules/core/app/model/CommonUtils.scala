package model

import java.io.IOException
import java.nio.file.{ CopyOption, Files, OpenOption, Path }

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.util.{ Failure, Success, Try }

import play.Logger

object CommonUtils {

  implicit class RicherTry[+T](wrapped: Try[T]) {
    def zip[That](that: => Try[That]): Try[(T, That)] =
      for (a <- wrapped; b <- that) yield (a, b)
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