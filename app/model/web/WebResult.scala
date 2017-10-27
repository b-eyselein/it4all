package model.web

import model.core.result.{EvaluationResult, SuccessType}
import org.openqa.selenium.WebElement
import play.twirl.api.Html

import scala.util.{Failure, Success, Try}

trait HtmlRenderable {
  def render: Html
}

sealed abstract class WebResult(val task: WebTask, s: SuccessType, val messages: List[String])
  extends EvaluationResult(s) with HtmlRenderable

class ElementResult(t: WebTask,
                    val foundElement: Option[WebElement],
                    val attributeResults: List[AttributeResult],
                    val textContentResult: Option[TextContentResult]
                   ) extends WebResult(t, ElementResult.analyze(foundElement, attributeResults, textContentResult), List.empty) {

  override def render = new Html(
    s"""${
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
  def analyze(foundElement: Option[WebElement],
              attributeResults: List[AttributeResult],
              textContentResult: Option[TextContentResult]): SuccessType = foundElement match {
    case None    => SuccessType.NONE
    case Some(_) =>
      if (!EvaluationResult.allResultsSuccessful(attributeResults))
        SuccessType.PARTIALLY
      else textContentResult match {
        case None             => SuccessType.PARTIALLY
        case Some(textResult) => if (textResult.isSuccessful) SuccessType.COMPLETE else SuccessType.PARTIALLY
      }
  }
}

class JsWebResult(t: WebTask,
                  val preResults: List[ConditionResult],
                  val actionPerformed: Boolean,
                  val postResults: List[ConditionResult],
                  ms: List[String]) extends WebResult(t, JsWebResult.analyze(postResults, actionPerformed, postResults), ms) {

  override def render = new Html(
    s"""
${preResults.map(_.render).mkString("\n")}
<div class="alert alert-${if (actionPerformed) "success" else "danger"}">
  Aktion konnte ${if (actionPerformed) "" else "nicht"} erfolgreich ausgeführt werden.
</div>
${postResults.map(_.render).mkString("\n")}""")

}

case class TextContentResult(foundContent: String, awaitedContent: String)
  extends EvaluationResult(TextAnalyzer.analyze(foundContent, awaitedContent)) {

  def render =
    s"""
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

case class AttributeResult(attribute: Attribute, foundValue: Try[String])
  extends EvaluationResult(TextAnalyzer.analyze(attribute.value, foundValue)) {

  def render =
    s"""
<div class="alert alert-$getBSClass">Attribut "${attribute.key}" 
${
      success match {
        case SuccessType.COMPLETE  => "hat den gesuchten Wert."
        case SuccessType.PARTIALLY => s"""hat nicht den gesuchten Wert "${attribute.value}" sondern "${foundValue.getOrElse("")}"!"""
        case SuccessType.NONE      => "konnte nicht gefunden werden!"
        case SuccessType.FAILURE   => "konnte aufgrund eines Fehler nicht ueberprueft werden."
      }
    }
</div>"""

}

object TextAnalyzer {
  def analyze(foundValue: String, awaitedValue: String): SuccessType = if (foundValue == null) {
    SuccessType.NONE
  } else {
    if (foundValue.contains(awaitedValue)) SuccessType.COMPLETE
    else SuccessType.PARTIALLY
  }

  def analyze(awaitedValue: String, foundValue: Try[String]): SuccessType = foundValue match {
    case Success(found) => if (found.contains(awaitedValue)) SuccessType.COMPLETE else SuccessType.PARTIALLY
    case Failure(_)     => SuccessType.NONE
  }
}

object JsWebResult {
  def analyze(preconds: List[ConditionResult], actionPerf: Boolean, postconds: List[ConditionResult]): SuccessType = {
    if (EvaluationResult.allResultsSuccessful(preconds) && actionPerf && EvaluationResult.allResultsSuccessful(postconds))
      SuccessType.COMPLETE
    else SuccessType.NONE
  }
}

case class ConditionResult(s: SuccessType, condition: Condition, gottenValue: String)
  extends EvaluationResult(s) with HtmlRenderable {

  override def render = new Html(
    s"""<div class="alert alert-$getBSClass">
       |  <p>Vorbedingung konnte ${if (isSuccessful) "" else "nicht"}verifiziert werden.</p>
       |  <p>${condition.description}</p>
       |  ${if (isSuccessful) "" else s"<p>Element hatte aber folgenden Wert: $gottenValue</p>"}
       |</div>""".stripMargin)

}
