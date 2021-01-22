package model.matching

import model.points._

final case class MatchingResult[T, M <: Match[T]](
  allMatches: Seq[M],
  notMatchedForUser: Seq[T],
  notMatchedForSample: Seq[T],
  points: Points,
  maxPoints: Points
)
