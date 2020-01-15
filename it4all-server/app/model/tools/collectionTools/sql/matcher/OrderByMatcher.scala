package model.tools.collectionTools.sql.matcher

import model.core.matching._
import model.points._
import net.sf.jsqlparser.statement.select.OrderByElement

final case class OrderByMatch(
  userArg: Option[OrderByElement],
  sampleArg: Option[OrderByElement],
  analysisResult: GenericAnalysisResult
) extends Match[OrderByElement, GenericAnalysisResult] {

  override val maybeAnalysisResult: Option[GenericAnalysisResult] = Some(analysisResult)

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

  override protected def instantiateOnlySampleMatch(sa: OrderByElement): OrderByMatch =
    OrderByMatch(None, Some(sa), GenericAnalysisResult(MatchType.ONLY_SAMPLE))

  override protected def instantiateOnlyUserMatch(ua: OrderByElement): OrderByMatch =
    OrderByMatch(Some(ua), None, GenericAnalysisResult(MatchType.ONLY_USER))

  override protected def instantiateCompleteMatch(ua: OrderByElement, sa: OrderByElement): OrderByMatch =
    OrderByMatch(Some(ua), Some(sa), GenericAnalysisResult(MatchType.SUCCESSFUL_MATCH))
}
