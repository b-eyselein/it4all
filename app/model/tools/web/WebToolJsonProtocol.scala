package model.tools.web

import model.tools._
import model.tools.web.sitespec._
import model.{ExerciseFile, FilesSolution}
import play.api.libs.json.{Format, Json, OFormat}

object WebToolJsonProtocol extends FilesSolutionToolJsonProtocol[WebExerciseContent, WebExPart] {

  override val partTypeFormat: Format[WebExPart] = WebExPart.jsonFormat

  private val jsActionFormat: Format[JsAction] = {
    implicit val jatf: Format[JsActionType] = JsActionType.jsonFormat

    Json.format
  }

  private val webElementSpecFormat: Format[WebElementSpec] = {
    implicit val af: Format[Map[String, String]] = keyValueObjectMapFormat

    Json.format
  }

  private val jsTaskFormat: Format[JsTask] = {
    implicit val wesf: Format[WebElementSpec] = webElementSpecFormat
    implicit val jsf: Format[JsAction]        = jsActionFormat

    Json.format
  }

  private val htmlTaskFormat: Format[HtmlTask] = {
    implicit val wesf: Format[WebElementSpec] = webElementSpecFormat

    Json.format
  }

  private val siteSpecFormat: Format[SiteSpec] = {
    implicit val htf: Format[HtmlTask] = htmlTaskFormat
    implicit val jtf: Format[JsTask]   = jsTaskFormat

    Json.format
  }

  override val exerciseContentFormat: OFormat[WebExerciseContent] = {
    implicit val eff: Format[ExerciseFile]    = exerciseFileFormat
    implicit val ssf: Format[SiteSpec]        = siteSpecFormat
    implicit val sasof: Format[FilesSolution] = filesSolutionFormat

    Json.format
  }

}
