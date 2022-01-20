package model.tools.sql.matcher

import model.matching._
import model.points._
import model.tools.sql.ColumnWrapper

final case class ColumnMatch(matchType: MatchType, userArg: ColumnWrapper, sampleArg: ColumnWrapper) extends Match[ColumnWrapper] {

  override protected def argDescription: ColumnWrapper => String = _.columnName

  override def points: Points = if (matchType == MatchType.SUCCESSFUL_MATCH) singleHalfPoint else zeroPoints

  override val maxPoints: Points = singleHalfPoint

}

object ColumnMatcher extends Matcher[ColumnWrapper, ColumnMatch] {

  override protected def canMatch(cw1: ColumnWrapper, cw2: ColumnWrapper): Boolean = cw1 canMatch cw2

  override protected def instantiateMatch(ua: ColumnWrapper, sa: ColumnWrapper): ColumnMatch = ColumnMatch(ua doMatch sa, ua, sa)

}
