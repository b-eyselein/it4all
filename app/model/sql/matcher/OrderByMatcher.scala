package model.sql.matcher

import model.Enums.MatchType
import model.Enums.MatchType.SUCCESSFUL_MATCH
import model.core.matching.{Match, Matcher, MatchingResult}
import net.sf.jsqlparser.statement.select.OrderByElement
import play.api.libs.json.{JsString, JsValue}

case class OrderByMatch(userArg: Option[OrderByElement], sampleArg: Option[OrderByElement]) extends Match[OrderByElement] {

  override def analyze(ua: OrderByElement, sa: OrderByElement): MatchType = SUCCESSFUL_MATCH

  override protected def descArgForJson(arg: OrderByElement): JsValue = JsString(arg.toString)

}

object OrderByMatcher extends Matcher[OrderByElement, OrderByMatch, OrderByMatchingResult] {

  override protected def canMatch: (OrderByElement, OrderByElement) => Boolean = _.getExpression.toString == _.getExpression.toString


  override protected def matchInstantiation: (Option[OrderByElement], Option[OrderByElement]) => OrderByMatch = OrderByMatch


  override protected def resultInstantiation: Seq[OrderByMatch] => OrderByMatchingResult = OrderByMatchingResult

}

case class OrderByMatchingResult(allMatches: Seq[OrderByMatch]) extends MatchingResult[OrderByElement, OrderByMatch]