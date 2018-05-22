package model.core

import play.api.Logger
import play.api.libs.json.JsValue

import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}

trait JsonWriteable {

  def toJson: JsValue

}

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

  def splitTriesNew[A](ts: List[Try[A]]): (Seq[A], Seq[Failure[A]]) = {

    @annotation.tailrec
    def go(ts: List[Try[A]], successes: Seq[A], failures: Seq[Failure[A]]): (Seq[A], Seq[Failure[A]]) = ts match {
      case Nil          => (successes, failures)
      case head :: tail => head match {
        case Success(a)    => go(tail, successes :+ a, failures)
        case f: Failure[A] => go(tail, successes, failures :+ f)
      }

    }

    go(ts, Seq.empty, Seq.empty)
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