package model.core

sealed abstract class CorrectionException(msg: String) extends Exception(msg)

case class NoSuchExerciseException(id: Int) extends CorrectionException(s"Es gibt keine Aufgabe mit der ID '$id'")

object SolutionTransferException extends CorrectionException("Es gab einen Fehler bei der Übertragung ihrer Lösung!")

case class OtherCorrectionException(cause: Throwable) extends CorrectionException(cause.getMessage)