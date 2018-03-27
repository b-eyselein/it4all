package model.docker

sealed trait RunContainerResult

// Container run

case object RunContainerSuccess extends RunContainerResult

sealed trait RunContainerFailure extends Exception with RunContainerResult

// Failure types

case class RunContainerTimeOut(waitTime: Int) extends RunContainerFailure

case class RunContainerError(statusCode: Int, logs: String) extends RunContainerFailure

sealed trait RunContainerException extends RunContainerFailure {

  val error: Throwable

}

// Exceptions

case class WaitContainerException(error: Throwable) extends RunContainerException

case class CreateContainerException(error: Throwable) extends RunContainerException

case class StartContainerException(error: Throwable) extends RunContainerException

