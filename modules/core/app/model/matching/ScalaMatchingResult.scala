package model.matching

import model.exercise.Success
import model.result.EvaluationResult

class ScalaMatchingResult[T, M <: ScalaMatch[T]](val matchName: String, val allMatches: List[M]) extends EvaluationResult(ScalaMatchingResult.analyze(allMatches))

object ScalaMatchingResult {

  def analyze(allMatches: List[ScalaMatch[_]]): Success =
    if (allMatches.exists(_.matchType == MatchType.ONLY_USER) || allMatches.exists(_.matchType == MatchType.ONLY_SAMPLE))
      Success.NONE
    else if (allMatches.exists(_.matchType == MatchType.UNSUCCESSFUL_MATCH))
      Success.PARTIALLY
    else
      Success.COMPLETE

}
