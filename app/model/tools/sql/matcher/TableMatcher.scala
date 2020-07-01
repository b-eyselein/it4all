package model.tools.sql.matcher

import model.matching._
import model.points._
import net.sf.jsqlparser.schema.Table

final case class TableMatch(
  matchType: MatchType,
  userArg: Option[Table],
  sampleArg: Option[Table]
) extends Match[Table] {

  override protected def argDescription: Table => String = _.getName

  override def points: Points = if (matchType == MatchType.SUCCESSFUL_MATCH) singleHalfPoint else zeroPoints

  override def maxPoints: Points = sampleArg match {
    case None    => zeroPoints
    case Some(_) => singleHalfPoint
  }

}

object TableMatcher extends Matcher[Table, TableMatch] {

  override protected def canMatch(t1: Table, t2: Table): Boolean = t1.getName == t2.getName

  override protected def instantiateOnlySampleMatch(sa: Table): TableMatch =
    TableMatch(MatchType.ONLY_SAMPLE, None, Some(sa))

  override protected def instantiateOnlyUserMatch(ua: Table): TableMatch =
    TableMatch(MatchType.ONLY_USER, Some(ua), None)

  override protected def instantiateCompleteMatch(ua: Table, sa: Table): TableMatch =
    TableMatch(MatchType.SUCCESSFUL_MATCH, Some(ua), Some(sa))
}
