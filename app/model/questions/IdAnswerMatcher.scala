package model.questions

import model.core.result.{CompleteResult, EvaluationResult, SuccessType}
import play.api.libs.json._
import model.questions.QuestionConsts._

import scala.collection.mutable.ListBuffer

final case class QuestionResult(learnerSolution: Seq[IdGivenAnswer], question: CompleteQuestion) extends CompleteResult[IdAnswerMatch] {

  override type SolType = Seq[IdGivenAnswer]

  override val results: Seq[IdAnswerMatch] = IdAnswerMatcher.doMatch(learnerSolution, question.answers)

  override def toJson(saved: Boolean): JsValue = JsArray(results map { r =>
    Json.obj(
      idName -> JsNumber(r.id),
      "chosen" -> JsBoolean(r.userArg.isDefined),
      correctName -> JsBoolean(r.isCorrect),
      "explanation" -> r.sampleArg.flatMap(_.explanation).map(JsString)
    )
  })

}

object IdAnswerMatcher {

  // FIXME: why does this class not extend Matcher ???

  def doMatch(learnerSolution: Seq[IdGivenAnswer], answers: Seq[Answer]): Seq[IdAnswerMatch] = {
    val matches: ListBuffer[IdAnswerMatch] = ListBuffer.empty

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

    val wrong = firstList map (t => IdAnswerMatch(Some(t), None))
    val missing = secondList map (t => IdAnswerMatch(None, Some(t)))

    matches ++ wrong ++ missing
  }

}

final case class IdAnswerMatch(userArg: Option[IdGivenAnswer], sampleArg: Option[Answer]) extends EvaluationResult {

  def id: Int = userArg map (_.id) getOrElse (sampleArg map (_.id) getOrElse (-1))

  def correctness: Correctness = if (userArg.isDefined) sampleArg map (_.correctness) getOrElse Correctnesses.WRONG
  else sampleArg map (_.correctness) match {
    case Some(Correctnesses.WRONG | Correctnesses.OPTIONAL) => Correctnesses.CORRECT
    case _                                                  => Correctnesses.WRONG
  }

  override def success: SuccessType = ???

  def isCorrect: Boolean = correctness != Correctnesses.WRONG

}
