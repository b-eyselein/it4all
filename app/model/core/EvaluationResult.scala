package model.core

import model.Enums.SuccessType
import model.core.EvaluationResult._
import play.twirl.api.{Html, HtmlFormat}

import scalatags.Text
import scalatags.Text.all._

object EvaluationResult {

  implicit class PimpedHtmlString(string: String) {

    def asCode: String = s"<code>$string</code>"

    def asListElem: String = s"<li>$string</li>"

    def asDiv: String = s"<div>$string</div>"

    def asDivWithClass(clazz: String): String = s"""<div class="$clazz">$string</div>"""

  }

  def notAllResultsSuccessful[T <: EvaluationResult](results: Seq[T]): Boolean = results.exists(!_.isSuccessful)

  def allResultsSuccessful[T <: EvaluationResult](results: Seq[T]): Boolean = results.nonEmpty && results.forall(_.isSuccessful)

  def concatCodeElements(elements: List[String]): String = elements match {
    case Nil => "--"
    case els => els.map(el => s"<code>$el</code>").mkString
  }

  def concatListElements(elements: List[String]): Text.TypedTag[String] = elements match {
    case Nil => ul()
    case els => ul(els map (li(_)))
  }

  def asMsg(successType: SuccessType, msg: String): Text.TypedTag[String] = p(span(cls := successType.glyphicon, msg))

  def asMsg(success: Boolean, msg: String): Text.TypedTag[String] = p(span(cls := "glyphicon glyphicon-" + (if (success) "ok" else "remove"), msg))

}

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

