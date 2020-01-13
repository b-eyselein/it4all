package model.tools.collectionTools.sql.matcher

import model.core.matching._
import model.points._
import net.sf.jsqlparser.statement.select.OrderByElement
import play.api.libs.json.{JsString, JsValue}

final case class OrderByMatch(
  userArg: Option[OrderByElement],
  sampleArg: Option[OrderByElement]
) extends Match[OrderByElement, GenericAnalysisResult] {

  override def analyze(ua: OrderByElement, sa: OrderByElement): GenericAnalysisResult = GenericAnalysisResult(MatchType.SUCCESSFUL_MATCH)

  override protected def descArgForJson(arg: OrderByElement): JsValue = JsString(arg.toString)

  override def points: Points = if (matchType == MatchType.SUCCESSFUL_MATCH) singleHalfPoint else zeroPoints

  override def maxPoints: Points = sampleArg match {
    case None    => zeroPoints
    case Some(_) => singleHalfPoint
  }

}

object OrderByMatcher extends Matcher[OrderByElement, GenericAnalysisResult, OrderByMatch] {

  override protected val matchName: String = "Order Bys"

  override protected val matchSingularName: String = "des Order By-Statement"

  override protected def canMatch(o1: OrderByElement, o2: OrderByElement): Boolean =
    o1.getExpression.toString == o2.getExpression.toString

  override protected def matchInstantiation(ua: Option[OrderByElement], sa: Option[OrderByElement]): OrderByMatch =
    OrderByMatch(ua, sa)

}
