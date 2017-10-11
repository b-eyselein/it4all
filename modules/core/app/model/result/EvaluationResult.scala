package model.result

import play.twirl.api.{Html, HtmlFormat}

import scala.collection.JavaConverters.asScalaBufferConverter

class EvaluationResult(val success: SuccessType = SuccessType.NONE) {
  val getBSClass: String = success.getColor

  def getGlyphicon: String = success match {
    case SuccessType.COMPLETE => "glyphicon glyphicon-ok"
    case SuccessType.PARTIALLY => "glyphicon glyphicon-question-sign"
    case (SuccessType.NONE | SuccessType.FAILURE) => "glyphicon glyphicon-remove"
  }

  def getPoints: Int = success.getPoints

  val isSuccessful: Boolean = success == SuccessType.COMPLETE
}

class CompleteResult[E <: EvaluationResult](val learnerSolution: String, val results: java.util.List[E])
  extends EvaluationResult(CompleteResult.analyzeResults(results)) {

  def renderLearnerSolution = new Html(s"<pre>${HtmlFormat.escape(learnerSolution)}</pre>")
}

object EvaluationResult {
  def allResultsSuccessful[T <: EvaluationResult](results: java.util.List[T]): Boolean = results.asScala.forall(_.isSuccessful)

  def concatCodeElements(elements: java.util.List[String]): String = elements.asScala.toList match {
    case Nil => "--"
    case els => els.map(el => s"<code>$el</code>").mkString
  }

  def concatListElements(elements: java.util.List[String]): String = elements.asScala.toList match {
    case Nil => "<ul/>"
    case els => s"<ul>${els.map(el => s"<li>$el</li>")}</ul>".mkString
  }
}

object CompleteResult {
  def analyzeResults[T <: EvaluationResult](results: java.util.List[T]): SuccessType =
    if (EvaluationResult.allResultsSuccessful(results)) SuccessType.NONE
    else SuccessType.COMPLETE
}
