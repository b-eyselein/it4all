package model.core

sealed abstract class CorrectionException extends Exception

case class NoSuchExerciseException(id: Int) extends CorrectionException

case class SolutionTransferException() extends CorrectionException

case class OtherCorrectionException(cause: Throwable) extends CorrectionException