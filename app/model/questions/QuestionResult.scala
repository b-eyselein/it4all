package model.questions

import model.core.CompleteResult
import play.twirl.api.Html

case class QuestionResult(learnerSolution: Seq[IdGivenAnswer], question: CompleteQuestion) extends CompleteResult[IdAnswerMatchingResult] {

  override type SolType = Seq[IdGivenAnswer]

  override def renderLearnerSolution: Html = ???

  val matchingResult: IdAnswerMatchingResult = IdAnswerMatcher.doMatch(learnerSolution, question.answers)

  override def results: Seq[IdAnswerMatchingResult] = Seq(matchingResult)

}