package model.tools.collectionTools.web

import de.uniwue.webtester._
import model.core.result.SuccessType
import model.points._
import model.tools.collectionTools.web.WebConsts._
import model.tools.collectionTools.{ExerciseFile, FilesSampleSolutionToolJsonProtocol, SampleSolution, ToolJsonProtocol}
import play.api.libs.functional.syntax._
import play.api.libs.json._

object WebToolJsonProtocol extends FilesSampleSolutionToolJsonProtocol[WebExerciseContent, WebCompleteResult] {

  val jsHtmlElementSpecFormat: Format[JsHtmlElementSpec] = Json.format[JsHtmlElementSpec]

  private val jsTaskFormat: Format[JsTask] = {
    implicit val hesf: Format[JsHtmlElementSpec] = jsHtmlElementSpecFormat

    implicit val jsf: Format[JsAction] = {
      implicit val jatf: Format[JsActionType] = JsActionType.jsonFormat

      Json.format[JsAction]
    }

    Json.format[JsTask]
  }

  private val siteSpecFormat: Format[SiteSpec] = {
    implicit val htf: Format[HtmlTask] = Json.format[HtmlTask]
    implicit val jtf: Format[JsTask]   = jsTaskFormat

    Json.format[SiteSpec]
  }

  override val exerciseContentFormat: Format[WebExerciseContent] = {
    implicit val ssf : Format[SiteSpec]            = siteSpecFormat
    implicit val eff : Format[Seq[ExerciseFile]]   = solutionFormat
    implicit val fssf: Format[SampleSolution[Seq[ExerciseFile]]] = sampleSolutionFormat

    Json.format[WebExerciseContent]
  }

  // Other

  // FIXME: use macro Json.format[...]!

  private val gradedTextResultWrites: Writes[GradedTextResult] = {
    implicit val pw: Writes[Points] = ToolJsonProtocol.pointsFormat

    Json.writes[GradedTextResult]
  }

  // GradedElementSpecResult

  private def unapplyGradedHtmlTaskResult(gesr: GradedElementSpecResult): (Int, Boolean, Option[GradedTextResult], Seq[GradedTextResult], Boolean, Double, Double) =
    (gesr.id, gesr.foundElement.isDefined, gesr.textContentResult, gesr.attributeResults, gesr.isSuccessful, gesr.points.asDouble, gesr.maxPoints.asDouble)

  private val elementResultWrites: Writes[GradedElementSpecResult] = {
    implicit val gtrw: Writes[GradedTextResult] = gradedTextResultWrites

    (
      (__ \ idName).write[Int] and
        (__ \ elementFoundName).write[Boolean] and
        (__ \ textContentResultName).write[Option[GradedTextResult]] and
        (__ \ attributeResultsName).write[Seq[GradedTextResult]] and
        (__ \ successName).write[Boolean] and
        (__ \ pointsName).write[Double] and
        (__ \ maxPointsName).write[Double]
      ) (unapplyGradedHtmlTaskResult(_))
  }

  // Js Result

  private def unapplyGradedJsTaskResult(gjtr: GradedJsTaskResult): (Int, Seq[GradedElementSpecResult], String, Boolean, Seq[GradedElementSpecResult], Boolean, Double, Double) =
    (gjtr.id, gjtr.gradedPreResults, gjtr.gradedJsActionResult.actionDescription, gjtr.gradedJsActionResult.actionPerformed, gjtr.gradedPostResults,
      gjtr.success == SuccessType.COMPLETE, gjtr.points.asDouble, gjtr.maxPoints.asDouble)

  private val jsWebResultWrites: Writes[GradedJsTaskResult] = {
    implicit val gesrw: Writes[GradedElementSpecResult] = elementResultWrites

    (
      (__ \ idName).write[Int] and
        (__ \ "preResults").write[Seq[GradedElementSpecResult]] and
        (__ \ "actionDescription").write[String] and
        (__ \ "actionPerformed").write[Boolean] and
        (__ \ "postResults").write[Seq[GradedElementSpecResult]] and
        (__ \ successName).write[Boolean] and
        (__ \ pointsName).write[Double] and
        (__ \ maxPointsName).write[Double]
      ) (unapplyGradedJsTaskResult(_))
  }

  // Complete Result

  private def unapplyWebCompleteResult: WebCompleteResult => (Boolean, Seq[GradedElementSpecResult], Seq[GradedJsTaskResult], Boolean, Points, Points) = {
    case WebCompleteResult(gradedHtmlTaskResults, gradedJsTaskResults, points, maxPoints, solutionSaved) =>
      (solutionSaved, gradedHtmlTaskResults.map(_.gradedElementSpecResult), gradedJsTaskResults,
        (gradedHtmlTaskResults ++ gradedJsTaskResults).forall(_.success == SuccessType.COMPLETE), points, maxPoints)
  }

  override val completeResultWrites: Writes[WebCompleteResult] = {
    implicit val gesrw: Writes[GradedElementSpecResult] = elementResultWrites
    implicit val jwrw : Writes[GradedJsTaskResult]      = jsWebResultWrites
    implicit val pw   : Writes[Points]                  = ToolJsonProtocol.pointsFormat

    (
      (__ \ solutionSavedName).write[Boolean] and
        (__ \ htmlResultsName).write[Seq[GradedElementSpecResult]] and
        (__ \ jsResultsName).write[Seq[GradedJsTaskResult]] and
        (__ \ successName).write[Boolean] and
        (__ \ pointsName).write[Points] and
        (__ \ maxPointsName).write[Points]
      ) (unapplyWebCompleteResult)
  }


}
