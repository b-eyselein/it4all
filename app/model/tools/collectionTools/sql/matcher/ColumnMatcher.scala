package model.tools.collectionTools.sql.matcher

import model.core.matching._
import model.points._
import model.tools.collectionTools.sql.ColumnWrapper

final case class ColumnMatch(
  matchType: MatchType,
  userArg: Option[ColumnWrapper],
  sampleArg: Option[ColumnWrapper]
) extends Match[ColumnWrapper] {


  override def points: Points = if (matchType == MatchType.SUCCESSFUL_MATCH) singleHalfPoint else zeroPoints

  override def maxPoints: Points = sampleArg match {
    case None    => zeroPoints
    case Some(_) => singleHalfPoint
  }

}

object ColumnMatcher extends Matcher[ColumnWrapper, ColumnMatch] {

  override protected val matchName: String = "Spalten"

  override protected val matchSingularName: String = "der Spalte"

  override protected def canMatch(cw1: ColumnWrapper, cw2: ColumnWrapper): Boolean = cw1 canMatch cw2

  override protected def instantiateOnlySampleMatch(sa: ColumnWrapper): ColumnMatch =
    ColumnMatch(MatchType.ONLY_SAMPLE, None, Some(sa))

  override protected def instantiateOnlyUserMatch(ua: ColumnWrapper): ColumnMatch =
    ColumnMatch(MatchType.ONLY_USER, Some(ua), None)

  override protected def instantiateCompleteMatch(ua: ColumnWrapper, sa: ColumnWrapper): ColumnMatch = {
    val mt: MatchType = ua doMatch sa

    ColumnMatch(mt, Some(ua), Some(sa))
  }
}
