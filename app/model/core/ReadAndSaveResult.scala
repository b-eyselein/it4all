package model.core

import controllers.exes.MyWrapper

import scala.util.Failure

case class ReadAndSaveSuccess(ex: MyWrapper, saved: Boolean)


case class ReadAndSaveResult(successes: Seq[ReadAndSaveSuccess], failures: Seq[Failure[_]])