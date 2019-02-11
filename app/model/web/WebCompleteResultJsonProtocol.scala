package model.web

import model._
import model.core.result.CompleteResultJsonProtocol
import model.web.WebConsts._
import play.api.libs.functional.syntax._
import play.api.libs.json._

object WebCompleteResultJsonProtocol extends CompleteResultJsonProtocol[WebResult, WebCompleteResult] {

  // Html Result

  private implicit val textContentResultWrites: Writes[TextContentResult] = (
    (__ \ successName).write[Boolean] and
      (__ \ pointsName).write[String] and
      (__ \ maxPointsName).write[String] and
      (__ \ awaitedName).write[String] and
      (__ \ foundName).write[Option[String]]
    ) (tcr =>
    (tcr.isSuccessful, tcr.points.asDoubleString, 1.point.asDoubleString, tcr.awaitedContent, tcr.maybeFoundContent)
  )

  private implicit val attributeResultWrites: Writes[AttributeResult] = (
    (__ \ successName).write[Boolean] and
      (__ \ pointsName).write[String] and
      (__ \ maxPointsName).write[String] and
      (__ \ "attrName").write[String] and
      (__ \ awaitedName).write[String] and
      (__ \ foundName).write[Option[String]]
    ) (ar =>
    (ar.isSuccessful, ar.points.asDoubleString, 1.point.asDoubleString, ar.attribute.key, ar.awaitedContent, ar.maybeFoundContent)
  )

  private implicit val elementResultWrites: Writes[ElementResult] = (
    (__ \ idName).write[Int] and
      (__ \ pointsName).write[String] and
      (__ \ maxPointsName).write[String] and
      (__ \ successName).write[Boolean] and
      (__ \ "elementFound").write[Boolean] and
      (__ \ "textContent").write[Option[TextContentResult]] and
      (__ \ "attributeResults").write[Seq[AttributeResult]]
    ) (er =>
    (er.task.task.id, er.points.asDoubleString, er.task.maxPoints.asDoubleString, er.isCompletelySuccessful, er.foundElement.isDefined,
      er.textContentResult, er.attributeResults)
  )

  // Js Result

  private implicit val conditionResultWrites: Writes[ConditionResult] = (
    (__ \ pointsName).write[String] and
      (__ \ maxPointsName).write[String] and
      (__ \ successName).write[Boolean] and
      (__ \ descriptionName).write[String] and
      (__ \ awaitedName).write[String] and
      (__ \ gottenName).write[Option[String]]
    ) (cr =>
    (cr.points.asDoubleString, cr.condition.maxPoints.asDoubleString, cr.isSuccessful, cr.condition.description, cr.condition.awaitedValue, cr.gottenValue)
  )

  private implicit val jsWebResultWrites: Writes[JsWebResult] = (
    (__ \ idName).write[Int] and
      (__ \ pointsName).write[String] and
      (__ \ maxPointsName).write[String] and
      (__ \ successName).write[Boolean] and
      (__ \ "preResults").write[Seq[ConditionResult]] and
      (__ \ "actionDescription").write[String] and
      (__ \ "actionPerformed").write[Boolean] and
      (__ \ "postResults").write[Seq[ConditionResult]]
    ) (jwr =>
    (jwr.task.task.id, jwr.points.asDoubleString, jwr.task.maxPoints.asDoubleString, jwr.isCompletelySuccessful,
      jwr.preResults, jwr.task.task.actionDescription, jwr.actionPerformed, jwr.postResults)
  )

  // Complete Result

  override def completeResultWrites(solutionSaved: Boolean): Writes[WebCompleteResult] = (
    (__ \ solutionSavedName).write[Boolean] and
      (__ \ partName).write[String] and
      (__ \ successName).write[Boolean] and

      (__ \ pointsName).write[String] and
      (__ \ maxPointsName).write[String] and

      (__ \ htmlResultsName).write[Seq[ElementResult]] and
      (__ \ jsResultsName).write[Seq[JsWebResult]]
    ) (wcr =>
    (solutionSaved, wcr.part.urlName, wcr.results.forall(_.isSuccessful), wcr.points.asDoubleString, wcr.maxPoints.asDoubleString,
      wcr.elementResults, wcr.jsWebResults)
  )


}
