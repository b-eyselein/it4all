package model.tools.web

import de.uniwue.webtester.sitespec._
import model.json.JsonProtocols
import model.tools._
import play.api.libs.json.{Format, Json}

object WebToolJsonProtocol extends ToolJsonProtocol[WebSolution, WebExerciseContent, WebExPart] {

  // solution format

  override val solutionFormat: Format[WebSolution] = {
    implicit val eff: Format[ExerciseFile] = JsonProtocols.exerciseFileFormat

    Json.format
  }

  // other...

  private val jsActionFormat: Format[JsAction] = {
    implicit val jatf: Format[JsActionType] = JsActionType.jsonFormat

    Json.format
  }

  private val jsHtmlElementSpecFormat: Format[JsHtmlElementSpec] = {
    implicit val af: Format[Map[String, String]] = keyValueObjectMapFormat

    Json.format
  }

  private val jsTaskFormat: Format[JsTask] = {
    implicit val hesf: Format[JsHtmlElementSpec] = jsHtmlElementSpecFormat
    implicit val jsf: Format[JsAction]           = jsActionFormat

    Json.format
  }

  private val htmlTaskFormat: Format[HtmlTask] = {
    implicit val af: Format[Map[String, String]] = keyValueObjectMapFormat

    Json.format
  }

  private val siteSpecFormat: Format[SiteSpec] = {
    implicit val htf: Format[HtmlTask] = htmlTaskFormat
    implicit val jtf: Format[JsTask]   = jsTaskFormat

    Json.format
  }

  override val exerciseContentFormat: Format[WebExerciseContent] = {
    implicit val eff: Format[ExerciseFile]                  = JsonProtocols.exerciseFileFormat
    implicit val ssf: Format[SiteSpec]                      = siteSpecFormat
    implicit val sasof: Format[SampleSolution[WebSolution]] = sampleSolutionFormat

    Json.format
  }

  override val partTypeFormat: Format[WebExPart] = WebExPart.jsonFormat

}
