package model.tools.web

import model.core.result.CompleteResultJsonProtocol
import model.points.Points
import model.tools.web.WebConsts._
import play.api.libs.functional.syntax._
import play.api.libs.json._

//noinspection ConvertibleToMethodValue
object WebCompleteResultJsonProtocol extends CompleteResultJsonProtocol[GradedWebTaskResult, WebCompleteResult] {

  // Text Result: HtmlAttributeResult, TextContentResult

  //  private def unapplyGradedTextResult(gtcr: GradedTextResult): (String, String, Option[String], Boolean, Double, Double) =
  //    (gtcr.keyName, gtcr.awaitedContent, gtcr.maybeFoundContent, gtcr.isSuccessful, gtcr.points.asDouble, gtcr.maxPoints.asDouble)
  //
  //  private implicit val gradedTextResultWrites: Writes[GradedTextResult] = (
  //    (__ \ keyName).write[String] and
  //      (__ \ awaitedName).write[String] and
  //      (__ \ foundName).write[Option[String]] and
  //      (__ \ successName).write[Boolean] and
  //      (__ \ pointsName).write[Double] and
  //      (__ \ maxPointsName).write[Double]
  //    ) (unapplyGradedTextResult(_))

  private implicit val pointsWrites: Writes[Points] = { point => JsNumber(point.asDouble) }

  private implicit val gradedTextResultWrites: Writes[GradedTextResult] = Json.writes[GradedTextResult]

  // GradedElementSpecResult

  private def unapplyGradedHtmlTaskResult(gesr: GradedElementSpecResult): (Int, Boolean, Option[GradedTextResult], Seq[GradedTextResult], Boolean, Double, Double) =
    (gesr.id, gesr.foundElement.isDefined, gesr.textContentResult, gesr.attributeResults, gesr.isSuccessful, gesr.points.asDouble, gesr.maxPoints.asDouble)

  private implicit val elementResultWrites: Writes[GradedElementSpecResult] = (
    (__ \ idName).write[Int] and
      (__ \ elementFoundName).write[Boolean] and
      (__ \ textContentResultName).write[Option[GradedTextResult]] and
      (__ \ attributeResultsName).write[Seq[GradedTextResult]] and
      (__ \ successName).write[Boolean] and
      (__ \ pointsName).write[Double] and
      (__ \ maxPointsName).write[Double]
    ) (unapplyGradedHtmlTaskResult(_))

  // Js Result

  private def unapplyGradedJsTaskResult(gjtr: GradedJsTaskResult): (Int, Seq[GradedElementSpecResult], String, Boolean, Seq[GradedElementSpecResult], Boolean, Double, Double) =
    (gjtr.id, gjtr.gradedPreResults, gjtr.gradedJsActionResult.actionDescription, gjtr.gradedJsActionResult.actionPerformed, gjtr.gradedPostResults,
      gjtr.isSuccessful, gjtr.points.asDouble, gjtr.maxPoints.asDouble)

  private implicit val jsWebResultWrites: Writes[GradedJsTaskResult] = (
    (__ \ idName).write[Int] and
      (__ \ "preResults").write[Seq[GradedElementSpecResult]] and
      (__ \ "actionDescription").write[String] and
      (__ \ "actionPerformed").write[Boolean] and
      (__ \ "postResults").write[Seq[GradedElementSpecResult]] and
      (__ \ successName).write[Boolean] and
      (__ \ pointsName).write[Double] and
      (__ \ maxPointsName).write[Double]
    ) (unapplyGradedJsTaskResult(_))

  // Complete Result

  private def unapplyWebCompleteResult(solutionSaved: Boolean, wcr: WebCompleteResult): (Boolean, String, Seq[GradedElementSpecResult], Seq[GradedJsTaskResult], Boolean, Double, Double) =
    (solutionSaved, wcr.part.urlName, wcr.gradedHtmlTaskResults.map(_.gradedElementSpecResult), wcr.gradedJsTaskResults,
      wcr.results.forall(_.isSuccessful), wcr.points.asDouble, wcr.maxPoints.asDouble)

  override def completeResultWrites(solutionSaved: Boolean): Writes[WebCompleteResult] = (
    (__ \ solutionSavedName).write[Boolean] and
      (__ \ partName).write[String] and

      (__ \ htmlResultsName).write[Seq[GradedElementSpecResult]] and
      (__ \ jsResultsName).write[Seq[GradedJsTaskResult]] and

      (__ \ successName).write[Boolean] and
      (__ \ pointsName).write[Double] and
      (__ \ maxPointsName).write[Double]
    ) (unapplyWebCompleteResult(solutionSaved, _))


}
