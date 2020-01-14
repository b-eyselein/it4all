package model.tools.collectionTools.sql.matcher

import model.core.matching._
import model.points._
import net.sf.jsqlparser.schema.Table
import play.api.libs.json.{JsString, JsValue}

final case class TableMatch(
  userArg: Option[Table],
  sampleArg: Option[Table],
  analysisResult: Option[GenericAnalysisResult]
) extends Match[Table, GenericAnalysisResult] {

  override protected def descArgForJson(arg: Table): JsValue = JsString(arg.toString)

  /*
  override protected def analyze(arg1: Table, arg2: Table): GenericAnalysisResult = GenericAnalysisResult(MatchType.SUCCESSFUL_MATCH)
   */

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

  override protected def instantiatePartMatch(ua: Option[Table], sa: Option[Table]): TableMatch =
    TableMatch(ua, sa, None)

  override protected def instantiateCompleteMatch(ua: Table, sa: Table): TableMatch =
    TableMatch(Some(ua), Some(sa), Some(GenericAnalysisResult(MatchType.SUCCESSFUL_MATCH)))
}
