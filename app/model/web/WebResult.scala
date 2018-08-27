package model.web

import model._
import model.core.result.EvaluationResult._
import model.core.result.{CompleteResult, EvaluationResult, SuccessType}
import model.web.WebConsts._
import org.openqa.selenium.WebElement
import play.api.libs.json.{JsObject, JsValue, Json}

import scala.language.postfixOps

case class WebCompleteResult(learnerSolution: String, exercise: WebCompleteEx, part: WebExPart, results: Seq[WebResult]) extends CompleteResult[WebResult] {

  override type SolType = String

  def toJson(solutionSaved: Boolean): JsValue = Json.obj(
    solutionSavedName -> solutionSaved,
    partName -> part.urlName,
    successName -> results.forall(_.isSuccessful),

    pointsName -> points.asDoubleString,
    maxPointsName -> maxPoints.asDoubleString,

    htmlResultsName -> results.filter(_.isInstanceOf[ElementResult]).map(_.toJson),
    jsResultsName -> results.filter(_.isInstanceOf[JsWebResult]).map(_.toJson)
  )

  override def points: Points = addUp(results.map(_.points))

  override def maxPoints: Points = exercise.maxPoints(part)

}

sealed trait WebResult extends EvaluationResult {

  val task: WebCompleteTask

  def render: String

  def toJson: JsObject

  def points: Points

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
    idName -> task.task.id,
    pointsName -> points.asDoubleString,
    maxPointsName -> task.maxPoints.asDoubleString,
    successName -> (foundElement.isDefined && textContentResult.forall(_.isSuccessful) && attributeResults.forall(_.isSuccessful)),
    "elementFound" -> foundElement.isDefined,
    "textContent" -> (textContentResult map (_.toJson)),
    "attributeResults" -> (attributeResults map (_.toJson))
  )

  override val points: Points = foundElement match {
    case None    => 0 points
    case Some(_) => (1 point) + (textContentResult map (_.points) getOrElse 0.points) + addUp(attributeResults map (_.points))
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
    pointsName -> points.asDoubleString,
    maxPointsName -> 1.point.asDoubleString,
    awaitedName -> awaitedContent,
    foundName -> foundContent
  )

  val points: Points = if (isSuccessful) 1 point else 0 points

}

case class AttributeResult(attribute: Attribute, foundValue: Option[String]) extends TextResult(s"Das Attribut '${attribute.key}'", foundValue getOrElse "", attribute.value) {

  def toJson: JsObject = Json.obj(
    successName -> isSuccessful,
    pointsName -> points.asDoubleString,
    maxPointsName -> 1.point.asDoubleString,
    "attrName" -> attribute.key,
    awaitedName -> awaitedContent,
    foundName -> foundContent
  )

  val points: Points = if (isSuccessful) 1 point else foundValue map (_ => 1 halfPoint) getOrElse 0.points

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
    pointsName -> points.asDoubleString,
    maxPointsName -> task.maxPoints.asDoubleString,
    successName -> (preResults.forall(_.isSuccessful) && actionPerformed && postResults.forall(_.isSuccessful)),
    "preResults" -> preResults.map(_.toJson),
    "actionDescription" -> task.task.actionDescription,
    "actionPerformed" -> actionPerformed,
    "postResults" -> postResults.map(_.toJson)

    // FIXME: implement!
  )

  override val points: Points = addUp(preResults.map(_.points)) + (if (actionPerformed) 1 point else 0 points) + addUp(postResults.map(_.points))

}

case class ConditionResult(override val success: SuccessType, condition: JsCondition, gottenValue: String) extends EvaluationResult {

  def render: String = asMsg(success,
    s"""${if (condition.isPrecondition) "Vor" else "Nach"}bedingung konnte ${if (isSuccessful) "" else "nicht"} verifiziert werden.</p>
       |  <p>${condition.description}</p>
       |  ${if (isSuccessful) "" else s"<p>Element hatte aber folgenden Wert: $gottenValue</p>"}""".stripMargin)

  def points: Points = if (isSuccessful) 1 point else 0 points

  def toJson: JsObject = Json.obj(
    pointsName -> points.asDoubleString,
    maxPointsName -> condition.maxPoints.asDoubleString,
    successName -> isSuccessful,
    descriptionName -> condition.description,
    awaitedName -> condition.awaitedValue,
    gottenName -> gottenValue
  )

}
