package model.result

import java.util.List

import scala.collection.JavaConverters.asScalaBufferConverter

import play.twirl.api.{ Html, HtmlFormat }

class EvaluationResult(val success: SuccessType = SuccessType.NONE) {
  val getBSClass = success.getColor

  def getGlyphicon = success match {
    case SuccessType.COMPLETE                     => "glyphicon glyphicon-ok"
    case SuccessType.PARTIALLY                    => "glyphicon glyphicon-question-sign"
    case (SuccessType.NONE | SuccessType.FAILURE) => "glyphicon glyphicon-remove"
  }

  def getPoints = success.getPoints

  val isSuccessful = success == SuccessType.COMPLETE
}

class CompleteResult[E <: EvaluationResult](val learnerSolution: String, val results: List[E])
  extends EvaluationResult(CompleteResult.analyzeResults(results)) {

  def renderLearnerSolution = new Html(s"<pre>${HtmlFormat.escape(learnerSolution)}</pre>")
}

object EvaluationResult {
  def allResultsSuccessful[T <: EvaluationResult](results: List[T]) = !results.asScala.exists(!_.isSuccessful)

  def concatCodeElements(elements: List[String]) = elements.asScala.toList match {
    case Nil => "--"
    case els => els.map(el => s"<code>$el</code>").mkString
  }

  def concatListElements(elements: List[String]) = elements.asScala.toList match {
    case Nil => "<ul/>"
    case els => s"<ul>${els.map(el => s"<li>$el</li>")}</ul>".mkString
  }
}

object CompleteResult {
  def analyzeResults[T <: EvaluationResult](results: List[T]) =
    if (EvaluationResult.allResultsSuccessful(results)) SuccessType.NONE
    else SuccessType.COMPLETE
}
