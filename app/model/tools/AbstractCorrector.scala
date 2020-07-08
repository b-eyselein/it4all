package model.tools

import model.points._
import model.result.{AbstractCorrectionResult, InternalErrorResult}
import play.api.Logger

trait AbstractCorrector {

  type AbstractResult <: AbstractCorrectionResult[AbstractResult]

  protected val logger: Logger

  protected def buildInternalError(
    msg: String,
    maxPoints: Points
  ): AbstractResult with InternalErrorResult[AbstractResult]

  protected def onError(
    internalMsg: String,
    externalMsg: Option[String] = None,
    maybeException: Option[Throwable] = None,
    maxPoints: Points = (-1).points
  ): AbstractResult = {
    maybeException match {
      case None            => logger.error(internalMsg)
      case Some(exception) => logger.error(internalMsg, exception)
    }

    buildInternalError(externalMsg.getOrElse(internalMsg), maxPoints)
  }

}
