package model.feedback

import model.EvaluatedAspect
import model.EvaluatedAspects._
import model.Mark
import model.Mark.NoMark

final case class FeedbackResult(tool: String, allFeedback: Seq[Feedback]) {

  // FIXME: refactor!

  def feedbackFor(evaluatedAspect: EvaluatedAspect): Map[Mark, Int] =
    allFeedback.map(_.marks.getOrElse(evaluatedAspect, NoMark)).groupBy(identity).mapValues(_.size)

  val allComments: Seq[String] = allFeedback.map(_.comment).filter(!_.isEmpty)

}

object FeedbackResult {

  def avg(feedback: Map[Mark, Int]): Int = {
    val marksWithoutNoMark: Map[Mark, Int] = feedback filter {
      case (mark, _) => mark != Mark.NoMark
    }

    if (marksWithoutNoMark.isEmpty) 0
    else marksWithoutNoMark.map { case (f, m) => f.value * m }.toSeq.sum / marksWithoutNoMark.size
  }

}