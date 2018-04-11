package model.blanks

import model.core.CompleteResult
import model.core.matching.{Matcher, MatchingResult}
import play.twirl.api.Html

case class BlanksCompleteResult(learnerSolution: Seq[BlanksAnswer], result: BlanksAnswerMatchingResult) extends CompleteResult[BlanksAnswerMatchingResult] {

  override type SolType = Seq[BlanksAnswer]

  override val solutionSaved = false

  override def results: Seq[BlanksAnswerMatchingResult] = Seq(result)

  override def renderLearnerSolution: Html = ???

}

object BlanksCorrector extends Matcher[BlanksAnswer, BlanksAnswerMatch, BlanksAnswerMatchingResult] {

  override protected def canMatch: (BlanksAnswer, BlanksAnswer) => Boolean = _.id == _.id


  override protected def matchInstantiation: (Option[BlanksAnswer], Option[BlanksAnswer]) => BlanksAnswerMatch = BlanksAnswerMatch


  override protected def resultInstantiation: Seq[BlanksAnswerMatch] => BlanksAnswerMatchingResult = BlanksAnswerMatchingResult
}

case class BlanksAnswerMatchingResult(allMatches: Seq[BlanksAnswerMatch]) extends MatchingResult[BlanksAnswer, BlanksAnswerMatch] {

  override val matchName: String = "gegebene Antworten"

}