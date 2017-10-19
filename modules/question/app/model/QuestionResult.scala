package model


import model.QuestionResult._
import model.matching.{Match, Matcher}
import model.question.{Answer, Question}
import model.result.{EvaluationResult, SuccessType}

import scala.collection.JavaConverters._

case class AnswerMatch(ua: Option[Answer], sa: Option[Answer], s: Int) extends Match[Answer](ua, sa, s)

object AnswerMatcher extends Matcher[Answer, Match[Answer]](
  "Antworten", List.empty, (a1, a2) => a1.key.id == a2.key.id, AnswerMatch) {

}

class QuestionResult(selectedAnswers: List[Answer], question: Question) extends EvaluationResult(SuccessType.NONE) {

  val (correct, missing, wrong) = analyze(selectedAnswers, question.getCorrectAnswers.asScala.toList)

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