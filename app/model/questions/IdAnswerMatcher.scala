package model.questions

import model.Enums
import model.core.{CompleteResult, EvaluationResult}
import model.questions.QuestionEnums.Correctness
import play.twirl.api.Html

import scala.collection.mutable.ListBuffer

case class QuestionResult(learnerSolution: Seq[IdGivenAnswer], question: CompleteQuestion) extends CompleteResult[IdAnswerMatch] {

  override type SolType = Seq[IdGivenAnswer]

  override def renderLearnerSolution: Html = ???

  override val results: Seq[IdAnswerMatch] = IdAnswerMatcher.doMatch(learnerSolution, question.answers)

  def correct: Seq[IdAnswerMatch] = results filter (r => r.userArg.isDefined && r.sampleArg.isDefined && r.correctness != Correctness.WRONG)

  def missing: Seq[IdAnswerMatch] = results filter (r => r.userArg.isEmpty && r.correctness == Correctness.CORRECT)

  def wrong: Seq[IdAnswerMatch] = results filter (r => r.sampleArg.isEmpty || r.correctness == Correctness.WRONG)

}

object IdAnswerMatcher {

  def doMatch(learnerSolution: Seq[IdGivenAnswer], answers: Seq[Answer]): Seq[IdAnswerMatch] = {
    var matches: ListBuffer[IdAnswerMatch] = ListBuffer.empty

    val firstList = ListBuffer.empty ++ learnerSolution
    val secondList = ListBuffer.empty ++ answers

    for (arg1 <- learnerSolution) {
      var matched = false
      for (arg2 <- answers if !matched) {
        matched = arg1.id == arg2.id
        if (matched) {
          matches += IdAnswerMatch(Some(arg1), Some(arg2))
          firstList -= arg1
          secondList -= arg2
        }
      }
    }

    println(firstList.map(_.id) + " :: " + secondList.map(_.id) + " :: " + matches.map(_.id))

    val wrong = firstList map (t => IdAnswerMatch(Some(t), None))
    val missing = secondList map (t => IdAnswerMatch(None, Some(t)))

    matches ++ wrong ++ missing
  }

}

case class IdAnswerMatch(userArg: Option[IdGivenAnswer], sampleArg: Option[Answer]) extends EvaluationResult {

  def id: Int = userArg map (_.id) getOrElse (sampleArg map (_.id) getOrElse (-1))

  def correctness: Correctness = sampleArg map (_.correctness) getOrElse Correctness.WRONG

  override def success: Enums.SuccessType = ???
}
