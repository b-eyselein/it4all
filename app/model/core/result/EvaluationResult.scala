package model.core.result

import play.twirl.api.{Html, HtmlFormat}

class EvaluationResult(val success: SuccessType = SuccessType.NONE) {
  val getBSClass: String = success.getColor

  def getGlyphicon: String = success match {
    case SuccessType.COMPLETE                     => "glyphicon glyphicon-ok"
    case SuccessType.PARTIALLY                    => "glyphicon glyphicon-question-sign"
    case (SuccessType.NONE | SuccessType.FAILURE) => "glyphicon glyphicon-remove"
  }

  def getPoints: Int = success.getPoints

  val isSuccessful: Boolean = success == SuccessType.COMPLETE
}

class CompleteResult[E <: EvaluationResult](val learnerSolution: String, val results: List[E])
  extends EvaluationResult(CompleteResult.analyzeResults(results)) {

  def renderLearnerSolution = new Html(s"<pre>${HtmlFormat.escape(learnerSolution)}</pre>")
}

object EvaluationResult {
  def allResultsSuccessful[T <: EvaluationResult](results: List[T]): Boolean = results.forall(_.isSuccessful)

  def concatCodeElements(elements: List[String]): String = elements.toList match {
    case Nil => "--"
    case els => els.map(el => s"<code>$el</code>").mkString
  }

  def concatListElements(elements: List[String]): String = elements.toList match {
    case Nil => "<ul/>"
    case els => s"<ul>${els.map(el => s"<li>$el</li>")}</ul>".mkString
  }
}

object CompleteResult {
  def analyzeResults[T <: EvaluationResult](results: List[T]): SuccessType =
    if (EvaluationResult.allResultsSuccessful(results)) SuccessType.NONE
    else SuccessType.COMPLETE
}
