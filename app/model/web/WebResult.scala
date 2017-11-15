package model.web

import model.Enums.SuccessType
import model.Enums.SuccessType._
import model.core.EvaluationResult
import model.web.ElementResult._
import org.openqa.selenium.WebElement
import play.twirl.api.Html

import scala.util.Try

trait HtmlRenderable {
  def render: Html
}

object ElementResult {

  def analyze(foundElement: Option[WebElement], attributeResults: Seq[AttributeResult], textContentResult: Option[TextContentResult]): SuccessType = foundElement match {
    case None    => NONE
    case Some(_) =>
      if (!EvaluationResult.allResultsSuccessful(attributeResults))
        PARTIALLY
      else textContentResult match {
        case None             => PARTIALLY
        case Some(textResult) => if (textResult.isSuccessful) COMPLETE else PARTIALLY
      }
  }

  val elemNotFoundMsg: String = asMsg(success = false, "Element konnte nicht gefunden werden!")

  def asMsg(success: Boolean, msg: String): String = s"""<p><span class="glyphicon glyphicon-${if (success) "ok" else "remove"}"></span> $msg<p>"""

  def analyzeText(foundValue: String, awaitedValue: String): SuccessType = if (foundValue == null) {
    NONE
  } else {
    if (foundValue contains awaitedValue) COMPLETE else PARTIALLY
  }

}

sealed abstract class WebResult(val task: WebCompleteTask, s: SuccessType, val messages: Seq[String]) extends EvaluationResult(s) with HtmlRenderable

case class ElementResult(t: WebCompleteTask, foundElement: Option[WebElement], attributeResults: Seq[AttributeResult], textContentResult: Option[TextContentResult])
  extends WebResult(t, ElementResult.analyze(foundElement, attributeResults, textContentResult), Seq.empty) {


  override def render: Html = foundElement match {
    case None    => new Html(elemNotFoundMsg)
    case Some(_) => new Html(asMsg(success = true, "Element wurde gefunden.") + textContentResult.fold("")(_.render) + attributeResults.map(_.render).mkString("\n"))
  }
}

case class JsWebResult(t: WebCompleteTask, preResults: Seq[ConditionResult], actionPerformed: Boolean, postResults: Seq[ConditionResult], ms: Seq[String])
  extends WebResult(t, JsWebResult.analyze(postResults, actionPerformed, postResults), ms) {

  override def render = new Html(
    preResults.map(_.render).mkString("\n") +
      s"""<div class="alert alert-${if (actionPerformed) "success" else "danger"}">
         |  Aktion konnte ${if (actionPerformed) "" else "nicht"} erfolgreich ausgeführt werden.
         |</div>""".stripMargin +
      postResults.map(_.render).mkString("\n")
  )

}

abstract class TextResult(name: String, foundContent: String, awaitedContent: String)
  extends EvaluationResult(analyzeText(foundContent, awaitedContent)) {

  def render: String = asMsg(success == COMPLETE, success match {
    case COMPLETE  => s"$name hat den gesuchten Wert."
    case PARTIALLY => s"$name hat nicht den gesuchten Wert '$awaitedContent' sondern '$foundContent'!"
    case NONE      => s"$name konnte nicht gefunden werden!"
    case FAILURE   => s"$name konnte aufgrund eines Fehler nicht ueberprueft werden."
  })

}

case class TextContentResult(f: String, a: String) extends TextResult("Der Textinhalt", f, a)

case class AttributeResult(attribute: Attribute, foundValue: Try[String]) extends TextResult(s"Das Attribut '${attribute.key}'", foundValue getOrElse "", attribute.value)

object JsWebResult {
  def analyze(preconds: Seq[ConditionResult], actionPerf: Boolean, postconds: Seq[ConditionResult]): SuccessType = {
    if (EvaluationResult.allResultsSuccessful(preconds) && actionPerf && EvaluationResult.allResultsSuccessful(postconds))
      COMPLETE
    else NONE
  }
}

case class ConditionResult(s: SuccessType, condition: JsCondition, gottenValue: String)
  extends EvaluationResult(s) with HtmlRenderable {

  override def render = new Html(
    s"""<div class="alert alert-$getBSClass">
       |  <p>Vorbedingung konnte ${if (isSuccessful) "" else "nicht"}verifiziert werden.</p>
       |  <p>${condition.description}</p>
       |  ${if (isSuccessful) "" else s"<p>Element hatte aber folgenden Wert: $gottenValue</p>"}
       |</div>""".stripMargin)

}
