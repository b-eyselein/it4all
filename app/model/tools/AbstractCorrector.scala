package model.tools

import better.files.File
import model.core.DockerConnector
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

trait DockerExecutionCorrector extends AbstractCorrector {

  protected val resultFileName     = "result.json"
  protected val testConfigFileName = "testConfig.json"

  protected val baseBindPath: File = DockerConnector.DefaultWorkingDir

}
