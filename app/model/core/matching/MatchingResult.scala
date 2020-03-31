package model.core.matching

import model.points._
import sangria.macros.derive.GraphQLDeprecated

final case class MatchingResult[T, M <: Match[T]](
  @GraphQLDeprecated("Will be deleted")
  matchName: String,
  @GraphQLDeprecated("Will be deleted")
  matchSingularName: String,
  allMatches: Seq[M],
  points: Points,
  maxPoints: Points
)
