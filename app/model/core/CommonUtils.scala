package model.core

import play.api.Logger

import scala.util.{Failure, Success, Try}

object CommonUtils {

  private val logger = Logger(CommonUtils.getClass)

  def using[A <: AutoCloseable, B](resource: A)(f: A => B): Try[B] =
    try {
      Success(f(resource))
    } catch {
      case e: Exception => Failure(e)
    } finally {
      try {
        if (resource != null) resource.close()
      } catch {
        case e: Exception => logger.error("There has been an error: ", e)
      }
    }

  def splitTriesNew[A](ts: Seq[Try[A]]): (Seq[A], Seq[Failure[A]]) = {

    @annotation.tailrec
    def go(ts: List[Try[A]], successes: Seq[A], failures: Seq[Failure[A]]): (Seq[A], Seq[Failure[A]]) = ts match {
      case Nil => (successes, failures)
      case head :: tail =>
        head match {
          case Success(a)    => go(tail, successes :+ a, failures)
          case f: Failure[A] => go(tail, successes, failures :+ f)
        }

    }

    go(ts.toList, Seq[A](), Seq[Failure[A]]())
  }

}
