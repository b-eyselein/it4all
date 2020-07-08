package model.tools.regex

import model.points.Points
import model.tools.AbstractCorrector

abstract class RegexAbstractCorrector extends AbstractCorrector {

  override type AbstractResult = RegexAbstractResult

  override protected def buildInternalError(msg: String, maxPoints: Points): RegexInternalErrorResult =
    RegexInternalErrorResult(msg, maxPoints)

}
