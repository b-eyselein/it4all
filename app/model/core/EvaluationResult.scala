package model.core

import model.Enums.SuccessType
import model.core.EvaluationResult._
import play.twirl.api.{Html, HtmlFormat}

trait EvaluationResult {

  def success: SuccessType

  def getBSClass: String = success.color

  def getGlyphicon: String = success.glyphicon

  def getPoints: Int = success.points

  def isSuccessful: Boolean = success == SuccessType.COMPLETE

}

trait CompleteResult[E <: EvaluationResult] extends EvaluationResult {

  type SolType

  def learnerSolution: SolType

  def results: Seq[E]

  def renderLearnerSolution: Html

  override def success: SuccessType = SuccessType.ofBool(allResultsSuccessful(results))

}

case class GenericCompleteResult[E <: EvaluationResult](learnerSolution: String, results: Seq[E]) extends CompleteResult[E] {

  override type SolType = String

  override def renderLearnerSolution = new Html(s"<pre>${HtmlFormat.escape(learnerSolution)}</pre>")

}

object EvaluationResult {

  def notAllResultsSuccessful[T <: EvaluationResult](results: Seq[T]): Boolean = results.exists(!_.isSuccessful)

  def allResultsSuccessful[T <: EvaluationResult](results: Seq[T]): Boolean = results.nonEmpty && results.forall(_.isSuccessful)

  def concatCodeElements(elements: List[String]): String = elements match {
    case Nil => "--"
    case els => els.map(el => s"<code>$el</code>").mkString
  }

  def concatListElements(elements: List[String]): String = elements match {
    case Nil => "<ul/>"
    case els => s"<ul>${els.map(el => s"<li>$el</li>")}</ul>".mkString
  }

  def asMsg(successType: SuccessType, msg: String): String = s"""<p><span class="${successType.glyphicon}"></span> $msg</p>"""

  def asMsg(success: Boolean, msg: String): String = s"""<p><span class="glyphicon glyphicon-${if (success) "ok" else "remove"}"></span> $msg</p>"""

}
