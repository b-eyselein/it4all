package model.tools.sql.matcher

import model.core.matching.{GenericAnalysisResult, Match, MatchType, Matcher}
import net.sf.jsqlparser.expression.operators.relational.ExpressionList
import play.api.libs.json.{JsString, JsValue}


final case class ExpressionListMatch(userArg: Option[ExpressionList], sampleArg: Option[ExpressionList]) extends Match {

  override type T = ExpressionList

  override type AR = GenericAnalysisResult

  override protected def analyze(arg1: ExpressionList, arg2: ExpressionList): GenericAnalysisResult = {
    // TODO: implement?
    GenericAnalysisResult(MatchType.SUCCESSFUL_MATCH)
  }

  override protected def descArgForJson(arg: ExpressionList): JsValue = JsString(arg.toString)

}

object ExpressionListMatcher extends Matcher[ExpressionListMatch] {

  override type T = ExpressionList

  override protected val matchName: String = "Bedingungen"

  override protected val matchSingularName: String = "der Bedingung"

  override protected def canMatch(el1: ExpressionList, el2: ExpressionList): Boolean = el1.toString == el2.toString

  override protected def matchInstantiation(ua: Option[ExpressionList], sa: Option[ExpressionList]): ExpressionListMatch =
    ExpressionListMatch(ua, sa)

}