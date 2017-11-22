package model.web

import model.Enums.SuccessType
import model.Enums.SuccessType._
import model.core.EvaluationResult
import model.core.EvaluationResult._
import org.openqa.selenium.WebElement
import play.twirl.api.Html

import scala.util.Try

sealed trait WebResult extends EvaluationResult {

  val task: WebCompleteTask

  def render: Html

}

case class ElementResult(task: WebCompleteTask, foundElement: Option[WebElement], attributeResults: Seq[AttributeResult], textContentResult: Option[TextContentResult])
  extends WebResult {

  override val success: SuccessType = foundElement match {
    case None    => NONE
    case Some(_) =>
      if (notAllResultsSuccessful(attributeResults)) PARTIALLY
      else textContentResult match {
        case None             => COMPLETE
        case Some(textResult) => if (textResult.isSuccessful) COMPLETE else PARTIALLY
      }
  }

  override def render: Html = Html(foundElement match {
    case None    => asMsg(success = false, "Element konnte nicht gefunden werden!")
    case Some(_) =>
      asMsg(COMPLETE, "Element wurde gefunden.") +
        (textContentResult map (_.render) getOrElse "") +
        (attributeResults.map(_.render) mkString "\n")
  })
}

case class JsWebResult(task: WebCompleteTask, preResults: Seq[ConditionResult], actionPerformed: Boolean, postResults: Seq[ConditionResult])
  extends WebResult {

  override val success: SuccessType = SuccessType.ofBool(allResultsSuccessful(preResults) && actionPerformed && allResultsSuccessful(postResults))

  override def render: Html = new Html(
    (preResults map (_.render) mkString "\n") +
      asMsg(actionPerformed, s"Aktion konnte ${if (actionPerformed) "" else "nicht"} erfolgreich ausgeführt werden.") +
      (postResults map (_.render) mkString "\n")
  )

}

abstract class TextResult(name: String, foundContent: String, awaitedContent: String) extends EvaluationResult {

  override val success: SuccessType = if (foundContent == null) NONE
  else if (foundContent contains awaitedContent) COMPLETE
  else PARTIALLY

  def render: String = asMsg(success, success match {
    case COMPLETE  => s"$name hat den gesuchten Wert."
    case PARTIALLY => s"$name hat nicht den gesuchten Wert '$awaitedContent' sondern '$foundContent'!"
    case NONE      => s"$name konnte nicht gefunden werden!"
    case FAILURE   => s"$name konnte aufgrund eines Fehler nicht ueberprueft werden."
  })

}

case class TextContentResult(f: String, a: String) extends TextResult("Der Textinhalt", f, a)

case class AttributeResult(attribute: Attribute, foundValue: Try[String]) extends TextResult(s"Das Attribut '${attribute.key}'", foundValue getOrElse "", attribute.value)

case class ConditionResult(override val success: SuccessType, condition: JsCondition, gottenValue: String) extends EvaluationResult {

  def render: String = asMsg(success,
    s"""${if (condition.isPrecondition) "Vor" else "Nach"}bedingung konnte ${if (isSuccessful) "" else "nicht"} verifiziert werden.</p>
       |  <p>${condition.description}</p>
       |  ${if (isSuccessful) "" else s"<p>Element hatte aber folgenden Wert: $gottenValue</p>"}""".stripMargin)

}
