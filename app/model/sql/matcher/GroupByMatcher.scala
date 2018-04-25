package model.sql.matcher

import model.core.matching.{Match, MatchType, Matcher, MatchingResult}
import net.sf.jsqlparser.expression.Expression
import net.sf.jsqlparser.schema.Column
import play.api.libs.json.{JsString, JsValue}


case class GroupByMatch(userArg: Option[Expression], sampleArg: Option[Expression]) extends Match[Expression] {

  override def analyze(ua: Expression, sa: Expression): MatchType = MatchType.SUCCESSFUL_MATCH

  override protected def descArgForJson(arg: Expression): JsValue = JsString(arg.toString)

}


object GroupByMatcher extends Matcher[Expression, GroupByMatch, GroupByMatchingResult] {

  override protected def canMatch: (Expression, Expression) => Boolean = (exp1, exp2) => exp1 match {
    case column1: Column => exp2 match {
      case column2: Column => column1.getColumnName == column2.getColumnName
      case _               => false
    }
    case _               => false
  }


  override protected def matchInstantiation: (Option[Expression], Option[Expression]) => GroupByMatch = GroupByMatch


  override def resultInstantiation: Seq[GroupByMatch] => GroupByMatchingResult = GroupByMatchingResult

}

case class GroupByMatchingResult(allMatches: Seq[GroupByMatch]) extends MatchingResult[Expression, GroupByMatch]