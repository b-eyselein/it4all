package model

import scala.collection.JavaConverters.seqAsJavaListConverter

import org.openqa.selenium.WebElement

import model.result.{ EvaluationResult, SuccessType }
import model.task.{ Condition, WebTask }
import play.twirl.api.Html

trait HtmlRenderable {
  def render: Html
}

sealed abstract class WebResult(val task: WebTask, s: SuccessType, val messages: List[String])
  extends EvaluationResult(s) with HtmlRenderable

class ElementResult(
  t:                     WebTask,
  val foundElement:      Option[WebElement],
  val attributeResults:  List[AttributeResult],
  val textContentResult: Option[TextContentResult]
) extends WebResult(t, ElementResult.analyze(foundElement, attributeResults, textContentResult), List.empty) {

  override def render = new Html(s"""${
    foundElement match {
      case Some(_) =>
        val textResRender = textContentResult match {
          case None             => ""
          case Some(textResult) => textResult.render
        }
        val attrResRender = attributeResults.map(_.render).mkString("\n")

        """<div class="alert alert-success">Element wurde gefunden!</div>""" + textResRender + attrResRender

      case None => """<div class="alert alert-danger">Element konnte nicht gefunden werden!</div>"""
    }
  }""")
}

object ElementResult {
  def analyze(
    foundElement:      Option[WebElement],
    attributeResults:  List[AttributeResult],
    textContentResult: Option[TextContentResult]
  ) = foundElement match {
    case None => SuccessType.NONE
    case Some(_) =>
      if (!EvaluationResult.allResultsSuccessful(attributeResults.asJava))
        SuccessType.PARTIALLY
      else textContentResult match {
        case None             => SuccessType.PARTIALLY
        case Some(textResult) => if (textResult.isSuccessful) SuccessType.COMPLETE else SuccessType.PARTIALLY
      }
  }
}

class JsWebResult(
  t:                   WebTask,
  val preResults:      List[ConditionResult],
  val actionPerformed: Boolean,
  val postResults:     List[ConditionResult],
  ms:                  List[String]
) extends WebResult(t, JsWebResult.analyze(postResults, actionPerformed, postResults), ms) {

  override def render = new Html(s"""
${preResults.map(_.render).mkString("\n")}
<div class="alert alert-${if (actionPerformed) "success" else "danger"}">
  Aktion konnte ${if (actionPerformed) "" else "nicht"} erfolgreich ausgeführt werden.
</div>
${postResults.map(_.render).mkString("\n")}""")

}

case class TextContentResult(foundContent: String, awaitedContent: String)
  extends EvaluationResult(TextAnalyzer.analyze(foundContent, awaitedContent)) {

  def render = s"""
<div class="alert alert-$getBSClass">Der Textinhalt 
  ${
    success match {
      case SuccessType.COMPLETE  => "hat den gesuchten Wert."
      case SuccessType.PARTIALLY => s"""hat nicht den gesuchten Wert "$awaitedContent" sondern "$foundContent"!"""
      case SuccessType.NONE      => "konnte nicht gefunden werden!"
      case SuccessType.FAILURE   => "konnte aufgrund eines Fehler nicht ueberprueft werden."
    }
  }
  </div>"""

}

case class AttributeResult(attribute: Attribute, foundValue: String)
  extends EvaluationResult(TextAnalyzer.analyze(foundValue, attribute.value)) {

  def render = s"""
<div class="alert alert-$getBSClass">Attribut "${attribute.key}" 
${
    success match {
      case SuccessType.COMPLETE  => "hat den gesuchten Wert."
      case SuccessType.PARTIALLY => s"""hat nicht den gesuchten Wert "${attribute.value}" sondern "$foundValue}"!"""
      case SuccessType.NONE      => "konnte nicht gefunden werden!"
      case SuccessType.FAILURE   => "konnte aufgrund eines Fehler nicht ueberprueft werden."
    }
  }
</div>"""

}

object TextAnalyzer {
  def analyze(foundValue: String, awaitedValue: String) = if (foundValue == null) {
    SuccessType.NONE
  } else {
    if (foundValue.contains(awaitedValue)) SuccessType.COMPLETE
    else SuccessType.PARTIALLY
  }
}

object JsWebResult {
  def analyze(preconds: List[ConditionResult], actionPerf: Boolean, postconds: List[ConditionResult]): SuccessType = {
    if (EvaluationResult.allResultsSuccessful(preconds.asJava) && actionPerf && EvaluationResult.allResultsSuccessful(postconds.asJava))
      SuccessType.COMPLETE
    else return SuccessType.NONE
  }
}

case class ConditionResult(s: SuccessType, condition: Condition, gottenValue: String)
  extends EvaluationResult(s) with HtmlRenderable {

  override def render = new Html(s"""
<div class="alert alert-$getBSClass">
  <p>Vorbedingung konnte ${if (isSuccessful) "" else "nicht"}verifiziert werden.</p>
  <p>${condition.getDescription}</p>
  ${
    if (!isSuccessful) s"<p>Element hatte aber folgenden Wert: $gottenValue</p>" else ""
  }
</div>""")

}