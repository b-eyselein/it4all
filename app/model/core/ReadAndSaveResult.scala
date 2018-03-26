package model.core

import scala.util.Failure

case class ReadAndSaveSuccess[R](ex: R, saved: Boolean)


case class ReadAndSaveResult[R](successes: Seq[ReadAndSaveSuccess[R]], failures: Seq[Failure[R]])