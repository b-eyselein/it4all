package model.tools.web

import model.core.result.{CompleteResultJsonProtocol, SuccessType}
import model.points._
import model.tools.web.WebConsts._
import play.api.libs.functional.syntax._
import play.api.libs.json._

//noinspection ConvertibleToMethodValue
object WebCompleteResultJsonProtocol extends CompleteResultJsonProtocol[GradedWebTaskResult, WebCompleteResult] {

  private implicit val pointsWrites: Writes[Points] = pointsJsonWrites

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
      gjtr.success == SuccessType.COMPLETE, gjtr.points.asDouble, gjtr.maxPoints.asDouble)

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

  private def unapplyWebCompleteResult: WebCompleteResult => (Boolean, Seq[GradedElementSpecResult], Seq[GradedJsTaskResult], Boolean, Points, Points) = {
    case WebCompleteResult(gradedHtmlTaskResults, gradedJsTaskResults, points, maxPoints, solutionSaved) =>
      (solutionSaved, gradedHtmlTaskResults.map(_.gradedElementSpecResult), gradedJsTaskResults,
        (gradedHtmlTaskResults ++ gradedJsTaskResults).forall(_.success == SuccessType.COMPLETE), points, maxPoints)
  }

  override val completeResultWrites: Writes[WebCompleteResult] = (
    (__ \ solutionSavedName).write[Boolean] and
      (__ \ htmlResultsName).write[Seq[GradedElementSpecResult]] and
      (__ \ jsResultsName).write[Seq[GradedJsTaskResult]] and
      (__ \ successName).write[Boolean] and
      (__ \ pointsName).write[Points] and
      (__ \ maxPointsName).write[Points]
    ) (unapplyWebCompleteResult)


}
