package model.matching

import model.exercise.Success
import model.result.EvaluationResult
import play.twirl.api.Html
import scala.xml.Elem

class ScalaMatchingResult[T, M <: ScalaMatch[T]](val matchName: String, val allMatches: List[M]) extends EvaluationResult(ScalaMatchingResult.analyze(allMatches)) {

  def describe = new Html(
    (<tr class="active">
  		<th colspan="4" class={ "text-" + getBSClass + " text-center" }><span class={ getGlyphicon }></span> Vergleich { matchName }</th>
		</tr> ++ allMatches.map(_.describe))
      .toString)

}

object ScalaMatchingResult {

  def analyze(allMatches: List[ScalaMatch[_]]): Success =
    if (allMatches.exists(_.matchType == MatchType.ONLY_USER) || allMatches.exists(_.matchType == MatchType.ONLY_SAMPLE))
      Success.NONE
    else if (allMatches.exists(_.matchType == MatchType.UNSUCCESSFUL_MATCH))
      Success.PARTIALLY
    else
      Success.COMPLETE

}
