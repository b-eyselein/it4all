package model.sql

import model.Enums.MatchType
import model.Enums.MatchType.SUCCESSFUL_MATCH
import model.core.matching.{Match, Matcher, MatchingResult}
import model.sql.SelectMatcher._
import net.sf.jsqlparser.expression.Expression
import net.sf.jsqlparser.schema.Column
import net.sf.jsqlparser.statement.select.OrderByElement
import play.twirl.api.Html

object SelectMatcher {

  val GroupByName = "Group By-Elemente"

  val GroupByHeadings = Seq("Group By-Statement")

  def matchGroupBy(expression1: Expression, expression: Expression): Boolean = expression1 match {
    case column1: Column => expression match {
      case column2: Column => column1.getColumnName == column2.getColumnName
      case _               => false
    }
    case _               => false
  }

  val OrderByName = "Order By-Elemente"

  val OrderByHeadings = Seq("Order By-Statement")

  def matchOrderBy(orderByElement1: OrderByElement, orderByElement2: OrderByElement): Boolean =
    orderByElement1.getExpression.toString == orderByElement2.getExpression.toString

}

case class GroupByMatch(userArg: Option[Expression], sampleArg: Option[Expression]) extends Match[Expression] {

  override val size: Int = GroupByHeadings.size

  override def analyze(ua: Expression, sa: Expression): MatchType = SUCCESSFUL_MATCH

}

object GroupByMatcher extends Matcher[Expression, GroupByMatch, GroupByMatchingResult](GroupByHeadings, matchGroupBy, GroupByMatch, GroupByMatchingResult)

case class GroupByMatchingResult(allMatches: Seq[GroupByMatch]) extends MatchingResult[Expression, GroupByMatch] {

  override val matchName: String = GroupByName

  override val headings: Seq[String] = GroupByHeadings

}

case class OrderByMatch(userArg: Option[OrderByElement], sampleArg: Option[OrderByElement]) extends Match[OrderByElement] {

  override val size: Int = OrderByHeadings.size

  override def analyze(ua: OrderByElement, sa: OrderByElement): MatchType = SUCCESSFUL_MATCH

}

object OrderByMatcher extends Matcher[OrderByElement, OrderByMatch, OrderByMatchingResult](OrderByHeadings, matchOrderBy, OrderByMatch, OrderByMatchingResult)

case class OrderByMatchingResult(allMatches: Seq[OrderByMatch]) extends MatchingResult[OrderByElement, OrderByMatch] {

  override val matchName: String = OrderByName

  override val headings: Seq[String] = OrderByHeadings

}