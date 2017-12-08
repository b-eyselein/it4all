package model.questions


import model.Enums.SuccessType
import model.core.EvaluationResult
import model.core.matching.{Match, Matcher, MatchingResult}
import model.questions.QuestionResult._
import play.twirl.api.Html

case class AnswerMatch(ua: Option[Answer], sa: Option[Answer], s: Int) extends Match[Answer](ua, sa, s)

object AnswerMatcher extends Matcher[Answer, Match[Answer], AnswerMatchingResult](List.empty, _.id == _.id, AnswerMatch, AnswerMatchingResult)

case class AnswerMatchingResult(allMatches: Seq[Match[Answer]]) extends MatchingResult[Answer, Match[Answer]] {

  override val matchName: String = "TODO!"

  override val headings: Seq[String] = Seq.empty

  override def describe: Html = ???

}

case class QuestionResult(selectedAnswers: List[Answer], question: Question) extends EvaluationResult {

  override val success: SuccessType = SuccessType.NONE // TODO: implement!

  val (correct, missing, wrong) = analyze(selectedAnswers, question.getCorrectAnswers)

}

object QuestionResult {
  def analyze(selectedAnswers: List[Answer], correctAnswers: List[Answer]): (List[Answer], List[Answer], List[Answer]) = {
    (List.empty, List.empty, List.empty)
  }

  //  private SuccessType analyze (List < Answer > selAns, List < Answer > corrAns) {
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