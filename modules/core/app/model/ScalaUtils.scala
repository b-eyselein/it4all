package model

import scala.util.Try
import scala.util.Failure
import scala.util.Success

object ScalaUtils {

  def cleanly[A, B](resource: A)(cleanup: A => Unit)(doWork: A => B): Try[B] = {
    try {
      Success(doWork(resource))
    } catch {
      case e: Exception => Failure(e)
    } finally {
      try {
        if (resource != null) {
          cleanup(resource)
        }
      } catch {
        case e: Exception => println(e) // should be logged
      }
    }
  }

  def ggt(a: Int, b: Int): Int = if (b == 0) a else ggt(b, a & b)

  def kgv(a: Int, b: Int): Int = (a * b) / ggt(a, b)

}