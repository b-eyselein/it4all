package model.tools.web

import de.uniwue.webtester.sitespec._
import model.tools.{ExerciseFile, SampleSolution, ToolJsonProtocol}
import play.api.libs.json.{Format, Json}

object WebToolJsonProtocol extends ToolJsonProtocol[WebExerciseContent, WebSolution, WebExPart] {

  // solution format

  override val solutionFormat: Format[WebSolution] = {
    implicit val eff: Format[ExerciseFile] = ToolJsonProtocol.exerciseFileFormat

    Json.format
  }

  protected val sampleSolutionFormat: Format[SampleSolution[WebSolution]] = {
    implicit val sf: Format[WebSolution] = solutionFormat

    Json.format
  }

  // other...

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
    implicit val ssf: Format[SiteSpec]                     = siteSpecFormat
    implicit val eff: Format[ExerciseFile]                 = ToolJsonProtocol.exerciseFileFormat
    implicit val fssf: Format[SampleSolution[WebSolution]] = sampleSolutionFormat

    Json.format[WebExerciseContent]
  }

  override val partTypeFormat: Format[WebExPart] = WebExParts.jsonFormat

}
