package model.blanks

import model.core.matching.{Matcher, MatchingResult}
import model.core.result.CompleteResult
import play.twirl.api.Html

case class BlanksCompleteResult(learnerSolution: Seq[BlanksAnswer], result: MatchingResult[BlanksAnswer, BlanksAnswerMatch]) extends CompleteResult[MatchingResult[BlanksAnswer, BlanksAnswerMatch]] {

  override type SolType = Seq[BlanksAnswer]

  override val solutionSaved = false

  override def results: Seq[MatchingResult[BlanksAnswer, BlanksAnswerMatch]] = Seq(result)

  override def renderLearnerSolution: Html = ???

}

object BlanksCorrector extends Matcher[BlanksAnswer, BlanksAnswerMatch] {

  override protected def canMatch: (BlanksAnswer, BlanksAnswer) => Boolean = _.id == _.id


  override protected def matchInstantiation: (Option[BlanksAnswer], Option[BlanksAnswer]) => BlanksAnswerMatch = BlanksAnswerMatch

}