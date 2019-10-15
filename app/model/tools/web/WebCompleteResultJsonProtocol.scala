package model.tools.web

import de.uniwue.webtester.{HtmlAttribute, HtmlElementSpec, HtmlTask, JsAction, JsActionType, JsTask, SiteSpec}
import model.{ExerciseFile, ExerciseFileJsonProtocol, FilesSampleSolution, SemanticVersion, SemanticVersionHelper}
import model.core.result.{CompleteResultJsonProtocol, SuccessType}
import model.points._
import model.tools.web.WebConsts._
import play.api.libs.functional.syntax._
import play.api.libs.json._

//noinspection ConvertibleToMethodValue
object WebCompleteResultJsonProtocol extends CompleteResultJsonProtocol[GradedWebTaskResult, WebCompleteResult] {

  // Collection

  val collectionFormat: Format[WebCollection] = Json.format[WebCollection]

  private val htmlElementSpecFormat: Format[HtmlElementSpec] = {
    implicit val haf: Format[HtmlAttribute] = Json.format[HtmlAttribute]

    Json.format[HtmlElementSpec]
  }

  private val htmlTaskFormat: Format[HtmlTask] = {
    implicit val hesf: Format[HtmlElementSpec] = htmlElementSpecFormat

    Json.format[HtmlTask]
  }

  private val jsActionFormat: Format[JsAction] = {
    implicit val jatf: Format[JsActionType] = JsActionType.jsonFormat

    Json.format[JsAction]
  }

  private val jsTaskFormat: Format[JsTask] = {
    implicit val hesf: Format[HtmlElementSpec] = htmlElementSpecFormat

    implicit val jsf: Format[JsAction] = jsActionFormat

    Json.format[JsTask]
  }

  private val siteSpecFormat: Format[SiteSpec] = {
    implicit val htf: Format[HtmlTask] = htmlTaskFormat

    implicit val jtf: Format[JsTask] = jsTaskFormat

    Json.format[SiteSpec]
  }

  private val filesSampleSolutionFormat: Format[FilesSampleSolution] = {
    implicit val eff: Format[ExerciseFile] = ExerciseFileJsonProtocol.exerciseFileFormat

    Json.format[FilesSampleSolution]
  }

  val exerciseFormat: Format[WebExercise] = {
    implicit val svf: Format[SemanticVersion] = SemanticVersionHelper.format

    implicit val ssf: Format[SiteSpec] = siteSpecFormat

    implicit val eff: Format[ExerciseFile] = ExerciseFileJsonProtocol.exerciseFileFormat

    implicit val fssf: Format[FilesSampleSolution] = filesSampleSolutionFormat

    Json.format[WebExercise]
  }

  // Other

  // FIXME: use macro Json.format[...]!

  private val gradedTextResultWrites: Writes[GradedTextResult] = {
    implicit val pw: Writes[Points] = pointsJsonWrites

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

    implicit val jwrw: Writes[GradedJsTaskResult] = jsWebResultWrites

    implicit val pw: Writes[Points] = pointsJsonWrites

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
