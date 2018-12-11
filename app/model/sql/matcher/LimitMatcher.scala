package model.sql.matcher

import model.core.matching.{GenericAnalysisResult, Match, MatchType, Matcher}
import net.sf.jsqlparser.statement.select.Limit
import play.api.libs.json.{JsString, JsValue}

final case class LimitMatch(userArg: Option[Limit], sampleArg: Option[Limit]) extends Match {

  override type T = Limit

  override type AR = GenericAnalysisResult

  override protected def analyze(arg1: Limit, arg2: Limit): GenericAnalysisResult = GenericAnalysisResult(
    if (arg1.toString == arg2.toString) MatchType.SUCCESSFUL_MATCH
    else MatchType.PARTIAL_MATCH
  )

  override protected def descArgForJson(arg: Limit): JsValue = JsString(arg.toString)

}

object LimitMatcher extends Matcher[LimitMatch] {

  override type T = Limit

  override protected val matchName: String = "Limits"

  override protected val matchSingularName: String = "des Limits"

  override protected def canMatch(l1: Limit, l2: Limit): Boolean = true

  override protected def matchInstantiation(ua: Option[Limit], sa: Option[Limit]): LimitMatch = LimitMatch(ua, sa)

}
