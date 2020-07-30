package model.tools

import model.points._
import model.result.{AbstractCorrectionResult, InternalErrorResult}
import play.api.Logger

trait AbstractCorrector {

  type AbstractResult <: AbstractCorrectionResult

  protected val logger: Logger

  protected def buildInternalError(
    msg: String,
    maxPoints: Points
  ): AbstractResult with InternalErrorResult

  protected def onError(
    internalMsg: String,
    maxPoints: Points,
    maybeException: Option[Throwable] = None
  ): AbstractResult = {
    maybeException match {
      case None            => logger.error(internalMsg)
      case Some(exception) => logger.error(internalMsg, exception)
    }

    buildInternalError(internalMsg, maxPoints)
  }

}
