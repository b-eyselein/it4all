package model.sql

import model.Enums.MatchType
import model.Enums.MatchType.SUCCESSFUL_MATCH
import model.core.matching.{Match, Matcher, MatchingResult}
import net.sf.jsqlparser.expression.Expression
import net.sf.jsqlparser.schema.Column
import net.sf.jsqlparser.statement.select.OrderByElement

case class GroupByMatch(userArg: Option[Expression], sampleArg: Option[Expression]) extends Match[Expression] {

  override def analyze(ua: Expression, sa: Expression): MatchType = SUCCESSFUL_MATCH

}

object GroupByMatcher extends Matcher[Expression, GroupByMatch, GroupByMatchingResult] {

  override def canMatch: (Expression, Expression) => Boolean = (exp1, exp2) => exp1 match {
    case column1: Column => exp2 match {
      case column2: Column => column1.getColumnName == column2.getColumnName
      case _               => false
    }
    case _               => false
  }

  override def matchInstantiation: (Option[Expression], Option[Expression]) => GroupByMatch = GroupByMatch

  override def resultInstantiation: Seq[GroupByMatch] => GroupByMatchingResult = GroupByMatchingResult

}

case class GroupByMatchingResult(allMatches: Seq[GroupByMatch]) extends MatchingResult[Expression, GroupByMatch] {

  override val matchName: String = "Group By-Elemente"

}

case class OrderByMatch(userArg: Option[OrderByElement], sampleArg: Option[OrderByElement]) extends Match[OrderByElement] {

  override def analyze(ua: OrderByElement, sa: OrderByElement): MatchType = SUCCESSFUL_MATCH

}

object OrderByMatcher extends Matcher[OrderByElement, OrderByMatch, OrderByMatchingResult] {

  override def canMatch: (OrderByElement, OrderByElement) => Boolean = (ob1, ob2) =>
    ob1.getExpression.toString == ob2.getExpression.toString

  override def matchInstantiation: (Option[OrderByElement], Option[OrderByElement]) => OrderByMatch = OrderByMatch

  override def resultInstantiation: Seq[OrderByMatch] => OrderByMatchingResult = OrderByMatchingResult

}

case class OrderByMatchingResult(allMatches: Seq[OrderByMatch]) extends MatchingResult[OrderByElement, OrderByMatch] {

  override val matchName: String = "Order By-Elemente"

}