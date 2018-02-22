package model.core

import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}

object CommonUtils {

  implicit class RicherTry[+T](wrapped: Try[T]) {
    def zip[That](that: => Try[That]): Try[(T, That)] =
      for (a <- wrapped; b <- that) yield (a, b)
  }

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
        case e: Exception => Failure(e)
      }
    }
  }

  def using[A <: AutoCloseable, B](resource: A)(f: A => B): Try[B] = try {
    Success(f(resource))
  } catch {
    case e: Exception => Failure(e)
  } finally {
    if (resource != null) resource.close()
  }

  def splitTries[A](tries: Seq[Try[A]]): (List[A], List[Failure[A]]) = {
    var sucs: ListBuffer[A] = ListBuffer.empty
    var fails: ListBuffer[Failure[A]] = ListBuffer.empty

    tries map {
      case Success(a)    => sucs += a
      case f: Failure[A] => fails += f
    }

    (sucs.toList, fails.toList)
  }

}