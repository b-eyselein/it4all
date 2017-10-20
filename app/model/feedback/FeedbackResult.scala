package model.feedback

import model.feedback.Feedback.EvaluatedTool

case class FeedbackResult(tool: EvaluatedTool, allFeedback: List[Feedback]) {

  val feedbackSense = allFeedback.map(_.sense).groupBy(identity).mapValues(_.size)

  val feedbackUsage = allFeedback.map(_.used).groupBy(identity).mapValues(_.size)

  val feedbackUsability = allFeedback.map(_.usability).groupBy(identity).mapValues(_.size)

  val feedbackFeedback = allFeedback.map(_.feedback).groupBy(identity).mapValues(_.size)

  val feedbackFairness = allFeedback.map(_.fairness).groupBy(identity).mapValues(_.size)

  val allComments = allFeedback.map(_.comment).filter(!_.isEmpty)

  def get(evaledAspect: EvaluatedAspect) = evaledAspect match {
    case EvaluatedAspect.SENSE => feedbackSense
    case EvaluatedAspect.USED => feedbackUsage
    case EvaluatedAspect.USABILITY => feedbackUsability
    case EvaluatedAspect.STYLE_OF_FEEDBACK => feedbackFeedback
    case EvaluatedAspect.FAIRNESS_OF_FEEDBACK => feedbackFairness
  }

}

object FeedbackResult {

  def avg(feedback: Map[Mark, Int]) = {
    val marksWithoutNoMark = feedback.filter(_._1 != Mark.NO_MARK)
    if (marksWithoutNoMark.isEmpty) 0
    else marksWithoutNoMark.map { case (f, m) => f.getMark * m }.toList.sum / marksWithoutNoMark.size
  }

  def evaluate(allFeedback: List[Feedback]): List[FeedbackResult] =
    allFeedback.groupBy(_.theTool).map { case (t, f) => new FeedbackResult(t, f) }.toList

}