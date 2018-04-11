package model.core

import play.api.Logger

import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}

object CommonUtils {

  def using[A <: AutoCloseable, B](resource: A)(f: A => B): Try[B] = try {
    Success(f(resource))
  } catch {
    case e: Exception => Failure(e)
  } finally {
    try {
      if (resource != null) resource.close()
    } catch {
      case e: Exception => Logger.error("There has been an error: ", e)
    }
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