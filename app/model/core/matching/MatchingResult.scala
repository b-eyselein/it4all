package model.core.matching

import model.points._

final case class MatchingResult[T, M <: Match[T]](
  matchName: String,
  matchSingularName: String,
  allMatches: Seq[M],
  points: Points,
  maxPoints: Points
)
