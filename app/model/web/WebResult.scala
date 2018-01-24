package model.web

import model.Enums.SuccessType
import model.Enums.SuccessType._
import model.core.EvaluationResult._
import model.core.{CompleteResult, EvaluationResult}
import org.openqa.selenium.WebElement
import play.twirl.api.Html

import scala.util.Try
import scalatags.Text
import scalatags.Text.all._

case class WebCompleteResult(learnerSolution: String, solutionSaved: Boolean, results: Seq[WebResult]) extends CompleteResult[WebResult] {

  override type SolType = String

  override def renderLearnerSolution: Html = Html(pre(learnerSolution).toString)

  def render: Html = {

    val solSaved: String = if (solutionSaved)
      div(cls := "alert alert-success")(span(cls := "glyphicon glyphicon-ok"), " Ihre Lösung wurde gespeichert.").toString
    else
      div(cls := "alert alert-danger")(span(cls := "glyphicon glyphicon-remove"), " Ihre L&ouml;sung konnte nicht gespeichert werden!").toString

    val resultsRender: String = results match {
      case Nil   => div(cls := "alert alert-success")(span(cls := "glyphicon glyphicon-ok"), "Es wurden keine Fehler gefunden.").toString
      case reses => reses map (res => {
        div(cls := s"alert alert-${res.getBSClass}")(
          p(data("toggle") := "collapse", href := "#task" + res.task.task.id)(res.task.task.id + ". " + res.task.task.text),
          div(id := "task" + res.task.task.id, cls := ("collapse " + (if (res.isSuccessful) "" else "in")))(
            hr, res.render
          )
        )
      }) mkString "\n"
    }

    Html(solSaved + resultsRender)
  }

}

sealed trait WebResult extends EvaluationResult {

  val task: WebCompleteTask

  def render: String

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

  override def render: String = foundElement match {
    case None    => asMsg(success = false, "Element konnte nicht gefunden werden!").toString
    case Some(_) => asMsg(COMPLETE, "Element wurde gefunden.").toString + (textContentResult map (_.render) getOrElse "") + (attributeResults.map(_.render) mkString "\n")
  }
}

case class JsWebResult(task: WebCompleteTask, preResults: Seq[ConditionResult], actionPerformed: Boolean, postResults: Seq[ConditionResult])
  extends WebResult {

  override val success: SuccessType = SuccessType.ofBool(allResultsSuccessful(preResults) && actionPerformed && allResultsSuccessful(postResults))

  override def render: String = (preResults map (_.render) mkString "\n") +
    asMsg(actionPerformed, s"Aktion konnte ${if (actionPerformed) "" else "nicht"} erfolgreich ausgeführt werden.") +
    (postResults map (_.render) mkString "\n")


}

abstract class TextResult(name: String, foundContent: String, awaitedContent: String) extends EvaluationResult {

  override val success: SuccessType = if (foundContent == null) NONE
  else if (foundContent contains awaitedContent) COMPLETE
  else PARTIALLY

  def render: Text.TypedTag[String] = asMsg(success, success match {
    case COMPLETE  => s"$name hat den gesuchten Wert."
    case PARTIALLY => s"$name hat nicht den gesuchten Wert '$awaitedContent' sondern '$foundContent'!"
    case NONE      => s"$name konnte nicht gefunden werden!"
    case ERROR     => s"$name konnte aufgrund eines Fehler nicht ueberprueft werden."
  })

}

case class TextContentResult(f: String, a: String) extends TextResult("Der Textinhalt", f, a)

case class AttributeResult(attribute: Attribute, foundValue: Try[String]) extends TextResult(s"Das Attribut '${attribute.key}'", foundValue getOrElse "", attribute.value)

case class ConditionResult(override val success: SuccessType, condition: JsCondition, gottenValue: String) extends EvaluationResult {

  def render: Text.TypedTag[String] = asMsg(success,
    s"""${if (condition.isPrecondition) "Vor" else "Nach"}bedingung konnte ${if (isSuccessful) "" else "nicht"} verifiziert werden.</p>
       |  <p>${condition.description}</p>
       |  ${if (isSuccessful) "" else s"<p>Element hatte aber folgenden Wert: $gottenValue</p>"}""".stripMargin)

}
