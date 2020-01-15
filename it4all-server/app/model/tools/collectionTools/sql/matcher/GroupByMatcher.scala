package model.tools.collectionTools.sql.matcher

import model.core.matching._
import model.points._
import net.sf.jsqlparser.expression.Expression
import net.sf.jsqlparser.schema.Column


final case class GroupByMatch(
  userArg: Option[Expression],
  sampleArg: Option[Expression],
  analysisResult: GenericAnalysisResult
) extends Match[Expression, GenericAnalysisResult] {

  override val maybeAnalysisResult: Option[GenericAnalysisResult] = Some(analysisResult)

  override def points: Points = if (matchType == MatchType.SUCCESSFUL_MATCH) singleHalfPoint else zeroPoints

  override def maxPoints: Points = sampleArg match {
    case None    => zeroPoints
    case Some(_) => singleHalfPoint
  }

}


object GroupByMatcher extends Matcher[Expression, GenericAnalysisResult, GroupByMatch] {

  override protected val matchName: String = "Group Bys"

  override protected val matchSingularName: String = "des Group By-Statement"

  override protected def canMatch(e1: Expression, e2: Expression): Boolean = e1 match {
    case column1: Column => e2 match {
      case column2: Column => column1.getColumnName == column2.getColumnName
      case _               => false
    }
    case _               => false
  }


  override protected def instantiateOnlySampleMatch(sa: Expression): GroupByMatch =
    GroupByMatch(None, Some(sa), GenericAnalysisResult(MatchType.ONLY_SAMPLE))

  override protected def instantiateOnlyUserMatch(ua: Expression): GroupByMatch =
    GroupByMatch(Some(ua), None, GenericAnalysisResult(MatchType.ONLY_USER))

  override protected def instantiateCompleteMatch(ua: Expression, sa: Expression): GroupByMatch =
    GroupByMatch(Some(ua), Some(sa), GenericAnalysisResult(MatchType.SUCCESSFUL_MATCH))
}
