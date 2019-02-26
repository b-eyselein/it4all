package model.tools.sql.matcher

import model._
import model.core.matching._
import net.sf.jsqlparser.statement.select.OrderByElement
import play.api.libs.json.{JsString, JsValue}

import scala.language.postfixOps

final case class OrderByMatch(userArg: Option[OrderByElement], sampleArg: Option[OrderByElement]) extends Match {

  override type T = OrderByElement

  override type AR = GenericAnalysisResult

  override def analyze(ua: OrderByElement, sa: OrderByElement): GenericAnalysisResult = GenericAnalysisResult(MatchType.SUCCESSFUL_MATCH)

  override protected def descArgForJson(arg: OrderByElement): JsValue = JsString(arg.toString)

  override def points: Points = if (matchType == MatchType.SUCCESSFUL_MATCH) 1 halfPoint else 0 points

  override def maxPoints: Points = sampleArg match {
    case None    => 0 points
    case Some(_) => 1 halfPoint
  }

}

object OrderByMatcher extends Matcher[OrderByMatch] {

  override type T = OrderByElement

  override protected val matchName: String = "Order Bys"

  override protected val matchSingularName: String = "des Order By-Statement"

  override protected def canMatch(o1: OrderByElement, o2: OrderByElement): Boolean =
    o1.getExpression.toString == o2.getExpression.toString

  override protected def matchInstantiation(ua: Option[OrderByElement], sa: Option[OrderByElement]): OrderByMatch =
    OrderByMatch(ua, sa)

}
