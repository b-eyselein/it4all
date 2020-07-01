package model.matching

import model.points._

final case class MatchingResult[T, M <: Match[T]](
  allMatches: Seq[M],
  points: Points,
  maxPoints: Points
)
