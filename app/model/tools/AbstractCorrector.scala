package model.tools

import model.core.result.{AbstractCorrectionResult, InternalErrorResult}
import model.points._
import play.api.Logger

trait AbstractCorrector {

  type AbstractResult <: AbstractCorrectionResult

  protected val logger: Logger

  protected def buildInternalError(
    msg: String,
    solutionSaved: Boolean,
    maxPoints: Points
  ): AbstractResult with InternalErrorResult

  def onError(
    internalMsg: String,
    solutionSaved: Boolean,
    externalMsg: Option[String] = None,
    maybeException: Option[Throwable] = None,
    maxPoints: Points = (-1).points
  ): AbstractResult = {
    maybeException match {
      case None            => logger.error(internalMsg)
      case Some(exception) => logger.error(internalMsg, exception)
    }

    buildInternalError(externalMsg.getOrElse(internalMsg), solutionSaved, maxPoints)
  }

}
