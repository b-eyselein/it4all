package model.core

import model.Enums.SuccessType
import play.twirl.api.{Html, HtmlFormat}

class EvaluationResult(val success: SuccessType = SuccessType.NONE) {
  val getBSClass: String = success.color

  def getGlyphicon: String = success match {
    case SuccessType.COMPLETE                     => "glyphicon glyphicon-ok"
    case SuccessType.PARTIALLY                    => "glyphicon glyphicon-question-sign"
    case (SuccessType.NONE | SuccessType.FAILURE) => "glyphicon glyphicon-remove"
  }

  def getPoints: Int = success.points

  val isSuccessful: Boolean = success == SuccessType.COMPLETE
}

class CompleteResult[E <: EvaluationResult](val learnerSolution: String, val results: Seq[E])
  extends EvaluationResult(CompleteResult.analyzeResults(results)) {

  def renderLearnerSolution = new Html(s"<pre>${HtmlFormat.escape(learnerSolution)}</pre>")
}

object EvaluationResult {
  def allResultsSuccessful[T <: EvaluationResult](results: Seq[T]): Boolean = results.forall(_.isSuccessful)

  def concatCodeElements(elements: List[String]): String = elements match {
    case Nil => "--"
    case els => els.map(el => s"<code>$el</code>").mkString
  }

  def concatListElements(elements: List[String]): String = elements match {
    case Nil => "<ul/>"
    case els => s"<ul>${els.map(el => s"<li>$el</li>")}</ul>".mkString
  }
}

object CompleteResult {
  def analyzeResults[T <: EvaluationResult](results: Seq[T]): SuccessType =
    if (EvaluationResult.allResultsSuccessful(results)) SuccessType.NONE
    else SuccessType.COMPLETE
}
