package model.xml

import model.core.matching.MatchingResult
import model.xml.dtd.ElementLine

case class XmlGrammarCompleteResult(learnerSolution: String, solutionSaved: Boolean, matchingResult: MatchingResult[ElementLine, ElementLineMatch])
  extends XmlCompleteResult {

  override val results: Seq[ElementLineMatch] = matchingResult.allMatches

}