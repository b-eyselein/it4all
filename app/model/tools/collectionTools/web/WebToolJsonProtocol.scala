package model.tools.collectionTools.web

import de.uniwue.webtester.sitespec._
import model.points._
import model.tools.collectionTools.{ExerciseFile, FilesSampleSolutionToolJsonProtocol, SampleSolution, ToolJsonProtocol}
import play.api.libs.json.{Format, Json, Writes}

object WebToolJsonProtocol
    extends FilesSampleSolutionToolJsonProtocol[WebExerciseContent, WebCompleteResult, WebExPart] {

  private val jsActionFormat: Format[JsAction] = {
    implicit val jatf: Format[JsActionType] = JsActionType.jsonFormat

    Json.format[JsAction]
  }

  private val jsHtmlElementSpecFormat = {
    implicit val af: Format[Map[String, String]] = keyValueObjectMapFormat

    Json.format[JsHtmlElementSpec]
  }

  private val jsTaskFormat: Format[JsTask] = {
    implicit val hesf: Format[JsHtmlElementSpec] = jsHtmlElementSpecFormat
    implicit val jsf: Format[JsAction]           = jsActionFormat

    Json.format[JsTask]
  }

  private val htmlTaskFormat: Format[HtmlTask] = {
    implicit val af: Format[Map[String, String]] = keyValueObjectMapFormat

    Json.format[HtmlTask]
  }

  private val siteSpecFormat: Format[SiteSpec] = {
    implicit val htf: Format[HtmlTask] = htmlTaskFormat
    implicit val jtf: Format[JsTask]   = jsTaskFormat

    Json.format[SiteSpec]
  }

  override val exerciseContentFormat: Format[WebExerciseContent] = {
    implicit val ssf: Format[SiteSpec]                           = siteSpecFormat
    implicit val eff: Format[Seq[ExerciseFile]]                  = solutionFormat
    implicit val fssf: Format[SampleSolution[Seq[ExerciseFile]]] = sampleSolutionFormat

    Json.format[WebExerciseContent]
  }

  // Other

  private val gradedTextResultWrites: Writes[GradedTextResult] = {
    implicit val pw: Writes[Points] = ToolJsonProtocol.pointsFormat

    Json.writes[GradedTextResult]
  }

  private val gradedHtmlTaskResultWrites: Writes[GradedHtmlTaskResult] = {
    implicit val gtrw: Writes[GradedTextResult] = gradedTextResultWrites
    implicit val pw: Writes[Points]             = ToolJsonProtocol.pointsFormat

    Json.writes[GradedHtmlTaskResult]
  }

  private val gradedJsTaskResultWrites: Writes[GradedJsHtmlElementSpecResult] = {
    implicit val gtrw: Writes[GradedTextResult] = gradedTextResultWrites
    implicit val pw: Writes[Points]             = ToolJsonProtocol.pointsFormat

    Json.writes[GradedJsHtmlElementSpecResult]
  }

  private val gradedJsActionResultWrites = {
    implicit val jaf: Format[JsAction] = jsActionFormat
    implicit val pw: Writes[Points]    = ToolJsonProtocol.pointsFormat

    Json.writes[GradedJsActionResult]
  }

  private val jsWebResultWrites: Writes[GradedJsTaskResult] = {
    implicit val pw: Writes[Points]                           = ToolJsonProtocol.pointsFormat
    implicit val gesrw: Writes[GradedJsHtmlElementSpecResult] = gradedJsTaskResultWrites
    implicit val gjarw: Writes[GradedJsActionResult]          = gradedJsActionResultWrites

    Json.writes[GradedJsTaskResult]
  }

  override val completeResultWrites: Writes[WebCompleteResult] = {
    implicit val gesrw: Writes[GradedHtmlTaskResult] = gradedHtmlTaskResultWrites
    implicit val gjtrw: Writes[GradedJsTaskResult]   = jsWebResultWrites
    implicit val pw: Writes[Points]                  = ToolJsonProtocol.pointsFormat

    Json.writes[WebCompleteResult]
  }

  override val partTypeFormat: Format[WebExPart] = WebExParts.jsonFormat

}
