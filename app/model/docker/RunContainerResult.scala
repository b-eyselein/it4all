package model.docker

sealed trait RunContainerResult

// Container run

case object RunContainerSuccess extends RunContainerResult

sealed abstract class RunContainerFailure(msg: String, cause: Throwable) extends Exception(msg, cause) with RunContainerResult


// Failure types

case class RunContainerTimeOut(waitTime: Int) extends RunContainerFailure("", null)

case class RunContainerError(statusCode: Int, logs: String) extends RunContainerFailure(logs, null)

sealed abstract class RunContainerException(msg: String) extends RunContainerFailure(msg, null) {

  val error: Throwable

}

// Exceptions

case class WaitContainerException(error: Throwable) extends RunContainerException(error.getMessage)

case class CreateContainerException(error: Throwable) extends RunContainerException(error.getMessage)

case class StartContainerException(error: Throwable) extends RunContainerException(error.getMessage)

