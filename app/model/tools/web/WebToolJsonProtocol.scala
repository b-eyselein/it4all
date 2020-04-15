package model.tools.web

import de.uniwue.webtester.sitespec._
import model.tools.{ExerciseFile, SampleSolution, SemanticVersion, ToolJsonProtocol}
import play.api.libs.json.{Format, Json}

object WebToolJsonProtocol extends ToolJsonProtocol[WebExercise, WebSolution, WebExPart] {

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

  override val exerciseFormat: Format[WebExercise] = {
    implicit val svf: Format[SemanticVersion]              = ToolJsonProtocol.semanticVersionFormat
    implicit val ssf: Format[SiteSpec]                     = siteSpecFormat
    implicit val eff: Format[ExerciseFile]                 = ToolJsonProtocol.exerciseFileFormat
    implicit val fssf: Format[SampleSolution[WebSolution]] = sampleSolutionFormat

    Json.format
  }

  override val partTypeFormat: Format[WebExPart] = WebExParts.jsonFormat

}
