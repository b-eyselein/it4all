package model.tools.collectionTools.web

import de.uniwue.webtester._
import model._
import model.core.{LongText, LongTextYamlProtocol}
import model.tools.collectionTools.ExerciseFile
import net.jcazevedo.moultingyaml._

object WebToolYamlProtocol extends MyYamlProtocol {

  // FIXME: make super trait and reference it in ToolMain! ???

  private val jsTaskYamlFormat: YamlFormat[JsTask] = {
    implicit val hesyf: YamlFormat[JsHtmlElementSpec] = yamlFormat5(JsHtmlElementSpec)

    implicit val jayf: YamlFormat[JsAction] = {
      implicit val jatyf: YamlFormat[JsActionType] = new EnumYamlFormat(JsActionType)

      yamlFormat3(JsAction)
    }

    yamlFormat5(JsTask)
  }


  private val siteSpecYamlFormat: YamlFormat[SiteSpec] = {
    implicit val htyf: YamlFormat[HtmlTask] = yamlFormat6(HtmlTask)
    implicit val jtyf: YamlFormat[JsTask]   = jsTaskYamlFormat

    yamlFormat3(SiteSpec)
  }

  override implicit def mapFormat[K: YamlFormat, Y: YamlFormat]: YamlFormat[Map[K, Y]] = myMapFormat

  val webExerciseYamlFormat: YamlFormat[WebExercise] = {
    implicit val svyf : YamlFormat[SemanticVersion]     = semanticVersionYamlFormat
    implicit val ltyf : YamlFormat[LongText]            = LongTextYamlProtocol.longTextYamlFormat
    implicit val esyf : YamlFormat[ExerciseState]       = exerciseStateYamlFormat
    implicit val ssyf : YamlFormat[SiteSpec]            = siteSpecYamlFormat
    implicit val fssyf: YamlFormat[FilesSampleSolution] = filesSampleSolutionYamlFormat
    implicit val efyf : YamlFormat[ExerciseFile]        = exerciseFileYamlFormat

    yamlFormat13(WebExercise)
  }

}
