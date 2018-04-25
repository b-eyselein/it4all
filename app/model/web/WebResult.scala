package model.web

import model.core.result.EvaluationResult._
import model.core.result.{CompleteResult, EvaluationResult, SuccessType}
import model.web.WebConsts._
import org.openqa.selenium.WebElement
import play.api.libs.json.{JsObject, JsValue, Json}
import play.twirl.api.Html

import scala.language.postfixOps

case class WebCompleteResult(learnerSolution: String, exercise: WebCompleteEx, part: WebExPart, solutionSaved: Boolean, results: Seq[WebResult]) extends CompleteResult[WebResult] {

  override type SolType = String

  override def renderLearnerSolution: Html = Html("<pre>" + learnerSolution + "</pre>")

  def render: Html = {
    // FIXME: convert to template...

    val resultsRender: String = results match {
      case Nil   => s"""<div class="alert alert-success"><span class="glyphicon glyphicon-ok"></span> Es wurden keine Fehler gefunden.</div>"""
      case reses => reses map (res => {
        s"""<div class="alert alert-${res.getBSClass}">
           |  <p data-toggle="collapse" href="#task${res.task.task.id}">${res.task.task.id}. ${res.task.task.text}</p>
           |  <div id="task${res.task.task.id}" cls="collapse ${if (res.isSuccessful) "" else "in"}</div>
           |
           |  <hr>
           |  ${res.render}
          )
        )""".stripMargin
      }) mkString "\n"
    }

    Html(solSavedRender + resultsRender)
  }

  def toJson: JsValue = Json.obj(
    solutionSavedName -> solutionSaved,
    partName -> part.urlName,
    pointsName -> results.map(_.points).sum,
    maxPointsName -> exercise.maxPoints(part),
    successName -> results.forall(_.isSuccessful),
    htmlResultsName -> results.filter(_.isInstanceOf[ElementResult]).map(_.toJson),
    jsResultsName -> results.filter(_.isInstanceOf[JsWebResult]).map(_.toJson)
  )

}

sealed trait WebResult extends EvaluationResult {

  val task: WebCompleteTask

  def render: String

  def toJson: JsObject

  def points: Double

}

// Html & CSS Results

case class ElementResult(task: WebCompleteTask, foundElement: Option[WebElement], attributeResults: Seq[AttributeResult], textContentResult: Option[TextContentResult])
  extends WebResult {

  override val success: SuccessType = foundElement match {
    case None    => SuccessType.NONE
    case Some(_) =>
      if (notAllResultsSuccessful(attributeResults)) SuccessType.PARTIALLY
      else textContentResult match {
        case None             => SuccessType.COMPLETE
        case Some(textResult) => if (textResult.isSuccessful) SuccessType.COMPLETE else SuccessType.PARTIALLY
      }
  }

  override def render: String = foundElement match {
    case None    => asMsg(success = false, "Element konnte nicht gefunden werden!").toString
    case Some(_) => asMsg(SuccessType.COMPLETE, "Element wurde gefunden.").toString + (textContentResult map (_.render) getOrElse "") + (attributeResults.map(_.render) mkString "\n")
  }

  override def toJson: JsObject = Json.obj(
    "id" -> task.task.id,
    "points" -> points, "maxPoints" -> task.maxPoints,
    "success" -> (foundElement.isDefined && textContentResult.forall(_.isSuccessful) && attributeResults.forall(_.isSuccessful)),
    "elementFound" -> foundElement.isDefined,
    "textContent" -> (textContentResult map (_.toJson)),
    "attributeResults" -> (attributeResults map (_.toJson))
  )

  override val points: Double = foundElement match {
    case None    => 0
    case Some(_) => 1 + (textContentResult map (_.points) getOrElse 0d) + (attributeResults map (_.points) sum)
  }

}

abstract class TextResult(name: String, val foundContent: String, val awaitedContent: String) extends EvaluationResult {

  override val success: SuccessType = if (foundContent == null) SuccessType.NONE
  else if (foundContent contains awaitedContent) SuccessType.COMPLETE
  else SuccessType.PARTIALLY

  def render: String = asMsg(success, success match {
    case SuccessType.COMPLETE  => s"$name hat den gesuchten Wert."
    case SuccessType.PARTIALLY => s"$name hat nicht den gesuchten Wert '$awaitedContent' sondern '$foundContent'!"
    case SuccessType.NONE      => s"$name konnte nicht gefunden werden!"
    case SuccessType.ERROR     => s"$name konnte aufgrund eines Fehler nicht ueberprueft werden."
  })

  def toJson: JsObject

}

case class TextContentResult(f: String, a: String) extends TextResult("Der Textinhalt", f, a) {

  def toJson: JsObject = Json.obj(
    successName -> isSuccessful,
    pointsName -> points,
    maxPointsName -> 1,
    awaitedName -> awaitedContent,
    foundName -> foundContent
  )

  val points: Double = if (isSuccessful) 1 else 0

}

case class AttributeResult(attribute: Attribute, foundValue: Option[String]) extends TextResult(s"Das Attribut '${attribute.key}'", foundValue getOrElse "", attribute.value) {

  def toJson: JsObject = Json.obj(
    successName -> isSuccessful,
    pointsName -> points,
    maxPointsName -> 1,
    "attrName" -> attribute.key,
    awaitedName -> awaitedContent,
    foundName -> foundContent
  )

  val points: Double = if (isSuccessful) 1d else foundValue map (_ => 0.5) getOrElse 0d

}

// Javascript Results

case class JsWebResult(task: JsCompleteTask, preResults: Seq[ConditionResult], actionPerformed: Boolean, postResults: Seq[ConditionResult])
  extends WebResult {

  override val success: SuccessType = SuccessType.ofBool(allResultsSuccessful(preResults) && actionPerformed && allResultsSuccessful(postResults))

  override def render: String = (preResults map (_.render) mkString "\n") +
    asMsg(actionPerformed, s"Aktion konnte ${if (actionPerformed) "" else "nicht"} erfolgreich ausgeführt werden.") +
    (postResults map (_.render) mkString "\n")

  override def toJson: JsObject = Json.obj(
    idName -> task.task.id,
    pointsName -> points,
    maxPointsName -> task.maxPoints,
    successName -> (preResults.forall(_.isSuccessful) && actionPerformed && postResults.forall(_.isSuccessful)),
    "preResults" -> preResults.map(_.toJson),
    "actionDescription" -> task.task.actionDescription,
    "actionPerformed" -> actionPerformed,
    "postResults" -> postResults.map(_.toJson)

    // FIXME: implement!
  )

  override val points: Double = preResults.map(_.points).sum + (if (actionPerformed) 1 else 0) + postResults.map(_.points).sum

}

case class ConditionResult(override val success: SuccessType, condition: JsCondition, gottenValue: String) extends EvaluationResult {

  def render: String = asMsg(success,
    s"""${if (condition.isPrecondition) "Vor" else "Nach"}bedingung konnte ${if (isSuccessful) "" else "nicht"} verifiziert werden.</p>
       |  <p>${condition.description}</p>
       |  ${if (isSuccessful) "" else s"<p>Element hatte aber folgenden Wert: $gottenValue</p>"}""".stripMargin)

  def points: Double = if (isSuccessful) 1 else 0

  def toJson: JsObject = Json.obj(
    pointsName -> points,
    maxPointsName -> condition.maxPoints,
    successName -> isSuccessful,
    descriptionName -> condition.description,
    awaitedName -> condition.awaitedValue,
    gottenName -> gottenValue
  )

}
