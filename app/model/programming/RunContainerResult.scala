package model.programming

trait RunContainerResult

case class RunContainerException(msg: String) extends RunContainerResult

case object RunContainerTimeOut extends RunContainerResult

case object RunContainerSuccess extends RunContainerResult

case class RunContainerError(statusCode: Int, logs: String) extends RunContainerResult
