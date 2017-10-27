package model.feedback

import model.Enums.{EvaluatedAspect, Mark}

case class FeedbackResult(tool: EvaluatedTool, allFeedback: List[Feedback]) {

  val feedbackSense: Map[Mark, Int] = allFeedback.map(_.sense).groupBy(identity).mapValues(_.size)

  val feedbackUsage: Map[Mark, Int] = allFeedback.map(_.used).groupBy(identity).mapValues(_.size)

  val feedbackUsability: Map[Mark, Int] = allFeedback.map(_.usability).groupBy(identity).mapValues(_.size)

  val feedbackFeedback: Map[Mark, Int] = allFeedback.map(_.feedback).groupBy(identity).mapValues(_.size)

  val feedbackFairness: Map[Mark, Int] = allFeedback.map(_.fairness).groupBy(identity).mapValues(_.size)

  val allComments: List[String] = allFeedback.map(_.comment).filter(!_.isEmpty)

  def get(evaledAspect: EvaluatedAspect): Map[Mark, Int] = evaledAspect match {
    case EvaluatedAspect.SENSE                => feedbackSense
    case EvaluatedAspect.USED                 => feedbackUsage
    case EvaluatedAspect.USABILITY            => feedbackUsability
    case EvaluatedAspect.STYLE_OF_FEEDBACK    => feedbackFeedback
    case EvaluatedAspect.FAIRNESS_OF_FEEDBACK => feedbackFairness
  }

}

object FeedbackResult {

  def avg(feedback: Map[Mark, Int]): Int = {
    val marksWithoutNoMark = feedback.filter(_._1 != Mark.NO_MARK)
    if (marksWithoutNoMark.isEmpty) 0
    else marksWithoutNoMark.map { case (f, m) => f.value * m }.toList.sum / marksWithoutNoMark.size
  }

  def evaluate(allFeedback: List[Feedback]): List[FeedbackResult] =
    allFeedback.groupBy(_.tool).map { case (t, f) => new FeedbackResult(t, f) }.toList

}