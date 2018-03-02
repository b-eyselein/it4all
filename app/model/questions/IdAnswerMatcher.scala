package model.questions

import model.Enums
import model.core.{CompleteResult, EvaluationResult}
import model.questions.QuestionEnums.Correctness
import play.api.libs.json._
import play.twirl.api.Html

import scala.collection.mutable.ListBuffer

case class QuestionResult(learnerSolution: Seq[IdGivenAnswer], question: CompleteQuestion) extends CompleteResult[IdAnswerMatch] {

  override type SolType = Seq[IdGivenAnswer]

  override val solutionSaved = false

  override def renderLearnerSolution: Html = ???

  override val results: Seq[IdAnswerMatch] = IdAnswerMatcher.doMatch(learnerSolution, question.answers)

  def forJson: JsValue = JsArray(results map { r =>
    JsObject(Seq("id" -> JsNumber(r.id), "chosen" -> JsBoolean(r.userArg.isDefined), "correct" -> JsBoolean(r.isCorrect)) ++ r.sampleArg.flatMap(_.explanation).map(expl => "explanation" -> JsString(expl)))
  })

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

    val wrong = firstList map (t => IdAnswerMatch(Some(t), None))
    val missing = secondList map (t => IdAnswerMatch(None, Some(t)))

    matches ++ wrong ++ missing
  }

}

case class IdAnswerMatch(userArg: Option[IdGivenAnswer], sampleArg: Option[Answer]) extends EvaluationResult {

  def id: Int = userArg map (_.id) getOrElse (sampleArg map (_.id) getOrElse (-1))

  def correctness: Correctness = if (userArg.isDefined) sampleArg map (_.correctness) getOrElse Correctness.WRONG
  else sampleArg map (_.correctness) match {
    case Some(Correctness.WRONG | Correctness.OPTIONAL) => Correctness.CORRECT
    case _                                              => Correctness.WRONG
  }

  override def success: Enums.SuccessType = ???

  def isCorrect: Boolean = correctness != Correctness.WRONG

}
