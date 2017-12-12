package model.questions

import model.Enums.SuccessType
import model.core.EvaluationResult
import model.core.matching.{Match, Matcher, MatchingResult}
import model.questions.QuestionResult._

case class AnswerMatch(userArg: Option[Answer], sampleArg: Option[Answer]) extends Match[Answer] {

  override val size: Int = 1

}


object AnswerMatcher extends Matcher[Answer, Match[Answer], AnswerMatchingResult](Seq.empty, _.id == _.id, AnswerMatch, AnswerMatchingResult)

case class AnswerMatchingResult(allMatches: Seq[Match[Answer]]) extends MatchingResult[Answer, Match[Answer]] {

  override val matchName: String = "TODO!"

  override val headings: Seq[String] = Seq.empty

}

case class QuestionResult(selectedAnswers: Seq[Answer], question: CompleteQuestion) extends EvaluationResult {

  override val success: SuccessType = SuccessType.NONE // TODO: implement!

  val (correct, missing, wrong) = analyze(selectedAnswers, question.getCorrectAnswers)

}

object QuestionResult {
  def analyze(selectedAnswers: Seq[Answer], correctAnswers: Seq[Answer]): (Seq[Answer], Seq[Answer], Seq[Answer]) = {
    (Seq.empty, Seq.empty, Seq.empty)
  }

  //  private SuccessType analyze (Seq < Answer > selAns, Seq < Answer > corrAns) {
  //    for (Iterator < Answer > sel    = selAns.iterator() sel    .hasNext()    )
  //    {
  //      Answer selectedAns = sel.next()
  //      for (Iterator < Answer > corr
  //      = corrAns.iterator() corr
  //      .hasNext()
  //      )
  //      {
  //        Answer correctAns = corr.next()
  //        if (selectedAns.key.id == correctAns.key.id) {
  //          correct.add(selectedAns)
  //          sel.remove()
  //          corr.remove()
  //        }
  //      }
  //    }
  //    missing = corrAns
  //    wrong = selAns
  //    return (missing.isEmpty() && wrong.isEmpty()) ? SuccessType.COMPLETE: SuccessType.NONE
  //  }

}