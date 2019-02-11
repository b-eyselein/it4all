package model.web

import model._
import model.core.result.EvaluationResult._
import model.core.result.{CompleteResult, EvaluationResult, SuccessType}
import org.openqa.selenium.WebElement
import play.api.libs.json.JsValue

import scala.language.postfixOps

final case class WebCompleteResult(learnerSolution: String, exercise: WebCompleteEx, part: WebExPart,
                                   elementResults: Seq[ElementResult], jsWebResults: Seq[JsWebResult]) extends CompleteResult[WebResult] {

  override type SolType = String

  override def results: Seq[WebResult] = elementResults ++ jsWebResults

  override def points: Points = addUp(results.map(_.points))

  override def maxPoints: Points = exercise.maxPoints(part)

}

sealed trait WebResult extends EvaluationResult {

  val task: WebCompleteTask

  def isCompletelySuccessful: Boolean

  def points: Points

}

// Html & CSS Results

final case class ElementResult(task: WebCompleteTask, foundElement: Option[WebElement], attributeResults: Seq[AttributeResult], textContentResult: Option[TextContentResult])
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

  override def isCompletelySuccessful: Boolean = foundElement.isDefined && textContentResult.forall(_.isSuccessful) && attributeResults.forall(_.isSuccessful)

  override val points: Points = foundElement match {
    case None    => 0 points
    case Some(_) => (1 point) + (textContentResult map (_.points) getOrElse 0.points) + addUp(attributeResults map (_.points))
  }

}

abstract class TextResult(name: String) extends EvaluationResult {

  val maybeFoundContent: Option[String]

  val awaitedContent: String

  override def success: SuccessType = maybeFoundContent match {
    case None     => SuccessType.NONE
    case Some(fc) =>
      if (fc contains awaitedContent)
        SuccessType.COMPLETE
      else
        SuccessType.PARTIALLY
  }

}

final case class TextContentResult(maybeFoundContent: Option[String], awaitedContent: String) extends TextResult("Der Textinhalt") {

  val points: Points = if (isSuccessful) 1 point else 0 points

}

final case class AttributeResult(attribute: Attribute, maybeFoundContent: Option[String]) extends
  TextResult(s"Das Attribut '${attribute.key}'") {

  override val awaitedContent: String = attribute.value

  val points: Points = if (isSuccessful) 1 point else maybeFoundContent map (_ => 1 halfPoint) getOrElse 0.points

}

// Javascript Results

final case class JsWebResult(task: JsCompleteTask, preResults: Seq[ConditionResult], actionPerformed: Boolean, postResults: Seq[ConditionResult])
  extends WebResult {

  override val success: SuccessType = SuccessType.ofBool(allResultsSuccessful(preResults) && actionPerformed && allResultsSuccessful(postResults))

  override def isCompletelySuccessful: Boolean = preResults.forall(_.isSuccessful) && actionPerformed && postResults.forall(_.isSuccessful)

  override val points: Points = addUp(preResults.map(_.points)) + (if (actionPerformed) 1 point else 0 points) + addUp(postResults.map(_.points))

}

final case class ConditionResult(override val success: SuccessType, condition: JsCondition, gottenValue: Option[String]) extends EvaluationResult {

  def points: Points = if (isSuccessful) 1 point else 0 points

}
