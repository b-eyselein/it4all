package model.core

import scala.util.Failure

final case class ReadAndSaveSuccess[R](read: R, saved: Boolean)

final case class ReadAndSaveResult[R](successes: Seq[ReadAndSaveSuccess[R]], failures: Seq[Failure[R]])
