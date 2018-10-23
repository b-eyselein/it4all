package model.feedback

import model.{EvaluatedAspect, Mark}
import model.Mark.NoMark

import scala.language.postfixOps

final case class FeedbackResult(tool: String, allFeedback: Seq[Feedback]) {

  // FIXME: refactor!

  def feedbackFor(evaluatedAspect: EvaluatedAspect): Map[Mark, Int] =
    allFeedback.map(_.marks.getOrElse(evaluatedAspect, NoMark)).groupBy(identity).mapValues(_.size)

  val allComments: Seq[String] = allFeedback.map(_.comment).filter(!_.isEmpty)

}

object FeedbackResult {

  def avgAndCount(feedback: Map[Mark, Int]): (Double, Int) = {
    val marksWithoutNoMark: Seq[(Mark, Int)] = feedback filter {
      case (mark, _) => mark != Mark.NoMark
    } toSeq

    marksWithoutNoMark match {
      case Seq() => (0d, 0)
      case marks =>
        val markCount: Int = marks.map { case (_, count) => count }.sum
        val avgMark: Double = math.rint(marks.map { case (mark, count) => mark.value * count }.sum.toDouble / markCount * 100) / 100d
        (avgMark, markCount)
    }
  }

}