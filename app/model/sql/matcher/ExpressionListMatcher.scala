package model.sql.matcher

import model.core.matching.{GenericAnalysisResult, Match, MatchType, Matcher}
import net.sf.jsqlparser.expression.operators.relational.ExpressionList
import play.api.libs.json.{JsString, JsValue}


case class ExpressionListMatch(userArg: Option[ExpressionList], sampleArg: Option[ExpressionList]) extends Match[ExpressionList] {

  override type AR = GenericAnalysisResult

  override protected def analyze(arg1: ExpressionList, arg2: ExpressionList): GenericAnalysisResult = {
    // TODO: implement?
    GenericAnalysisResult(MatchType.SUCCESSFUL_MATCH)
  }

  override protected def descArgForJson(arg: ExpressionList): JsValue = JsString(arg.toString)

}

object ExpressionListMatcher extends Matcher[ExpressionList, GenericAnalysisResult, ExpressionListMatch] {

  override protected def canMatch: (ExpressionList, ExpressionList) => Boolean = _.toString == _.toString

  override protected def matchInstantiation: (Option[ExpressionList], Option[ExpressionList]) => ExpressionListMatch = ExpressionListMatch

}
