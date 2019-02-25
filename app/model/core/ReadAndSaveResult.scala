package model.core

import scala.util.Failure

final case class ReadAndSaveSuccess[R](ex: R, saved: Boolean)

final case class ReadAndSaveResult[R](successes: Seq[ReadAndSaveSuccess[R]], failures: Seq[Failure[R]])
