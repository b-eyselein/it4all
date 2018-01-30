package model.docker

trait RunContainerResult

// Exceptions

sealed abstract class RunContainerException(val error: Throwable) extends RunContainerResult

case class WaitContainerException(e: Throwable) extends RunContainerException(e)

case class CreateContainerException(e: Throwable) extends RunContainerException(e)

case class StartContainerException(e: Throwable) extends RunContainerException(e)

// Container run

case object RunContainerSuccess extends RunContainerResult


case object RunContainerTimeOut extends RunContainerResult

case class RunContainerError(statusCode: Int, logs: String) extends RunContainerResult
