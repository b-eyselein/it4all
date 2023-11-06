package model.tools.web

import model.tools._
import model.tools.web.sitespec._
import model.{ExerciseFile, FilesSolution, FilesSolutionInput}
import play.api.libs.json.{Format, Json, OFormat}

import scala.annotation.unused

object WebToolJsonProtocol extends ToolWithPartsJsonProtocol[FilesSolutionInput, WebExerciseContent, WebExPart] with FilesSolutionToolJsonProtocol {

  override val partTypeFormat: Format[WebExPart] = WebExPart.jsonFormat

  private val jsActionFormat: Format[JsAction] = {
    @unused implicit val jatf: Format[JsActionType] = JsActionType.jsonFormat

    Json.format
  }

  private val webElementSpecFormat: Format[WebElementSpec] = {
    @unused implicit val af: Format[Map[String, String]] = keyValueObjectMapFormat

    Json.format
  }

  private val jsTaskFormat: Format[JsTask] = {
    @unused implicit val wesf: Format[WebElementSpec] = webElementSpecFormat
    @unused implicit val jsf: Format[JsAction]        = jsActionFormat

    Json.format
  }

  private val htmlTaskFormat: Format[HtmlTask] = {
    @unused implicit val wesf: Format[WebElementSpec] = webElementSpecFormat

    Json.format
  }

  private val siteSpecFormat: Format[SiteSpec] = {
    @unused implicit val htf: Format[HtmlTask] = htmlTaskFormat
    @unused implicit val jtf: Format[JsTask]   = jsTaskFormat

    Json.format
  }

  override val exerciseContentFormat: OFormat[WebExerciseContent] = {
    @unused implicit val eff: Format[ExerciseFile]    = exerciseFileFormat
    @unused implicit val ssf: Format[SiteSpec]        = siteSpecFormat
    @unused implicit val sasof: Format[FilesSolution] = filesSolutionFormat

    Json.format
  }

}
