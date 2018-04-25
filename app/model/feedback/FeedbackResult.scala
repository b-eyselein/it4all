package model.feedback

import model.enums.EvaluatedAspect
import model.enums.EvaluatedAspects._
import model.enums.Mark
import model.enums.Marks.NO_MARK

case class FeedbackResult(tool: String, allFeedback: Seq[Feedback]) {

  // FIXME: refactor!

  private val feedbackSense: Map[Mark, Int] = allFeedback.map(_.sense).groupBy(identity).mapValues(_.size)


  private val feedbackUsage: Map[Mark, Int] = allFeedback.map(_.used).groupBy(identity).mapValues(_.size)


  private val feedbackUsability: Map[Mark, Int] = allFeedback.map(_.usability).groupBy(identity).mapValues(_.size)


  private val feedbackFeedback: Map[Mark, Int] = allFeedback.map(_.feedback).groupBy(identity).mapValues(_.size)


  private val feedbackFairness: Map[Mark, Int] = allFeedback.map(_.fairness).groupBy(identity).mapValues(_.size)


  val allComments: Seq[String] = allFeedback.map(_.comment).filter(!_.isEmpty)

  def get(evaledAspect: EvaluatedAspect): Map[Mark, Int] = evaledAspect match {
    case SENSE                => feedbackSense
    case USED                 => feedbackUsage
    case USABILITY            => feedbackUsability
    case STYLE_OF_FEEDBACK    => feedbackFeedback
    case FAIRNESS_OF_FEEDBACK => feedbackFairness
  }

}

object FeedbackResult {

  def avg(feedback: Map[Mark, Int]): Int = {
    val marksWithoutNoMark: Map[Mark, Int] = feedback.filter(_._1 != NO_MARK)
    if (marksWithoutNoMark.isEmpty) 0
    else marksWithoutNoMark.map { case (f, m) => f.value * m }.toSeq.sum / marksWithoutNoMark.size
  }

}