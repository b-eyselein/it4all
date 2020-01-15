package model.tools.collectionTools.sql.matcher

import model.core.matching._
import model.points._
import net.sf.jsqlparser.schema.Table

final case class TableMatch(
  userArg: Option[Table],
  sampleArg: Option[Table],
  analysisResult: GenericAnalysisResult
) extends Match[Table, GenericAnalysisResult] {

  override val maybeAnalysisResult: Option[GenericAnalysisResult] = Some(analysisResult)

  override def points: Points = if (matchType == MatchType.SUCCESSFUL_MATCH) singleHalfPoint else zeroPoints

  override def maxPoints: Points = sampleArg match {
    case None    => zeroPoints
    case Some(_) => singleHalfPoint
  }

}

object TableMatcher extends Matcher[Table, GenericAnalysisResult, TableMatch] {

  override protected val matchName: String = "Tabellen"

  override protected val matchSingularName: String = "der Tabelle"

  override protected def canMatch(t1: Table, t2: Table): Boolean = t1.getName == t2.getName

  override protected def instantiateOnlySampleMatch(sa: Table): TableMatch =
    TableMatch(None, Some(sa), GenericAnalysisResult(MatchType.ONLY_SAMPLE))

  override protected def instantiateOnlyUserMatch(ua: Table): TableMatch =
    TableMatch(Some(ua), None, GenericAnalysisResult(MatchType.ONLY_USER))

  override protected def instantiateCompleteMatch(ua: Table, sa: Table): TableMatch =
    TableMatch(Some(ua), Some(sa), GenericAnalysisResult(MatchType.SUCCESSFUL_MATCH))
}
