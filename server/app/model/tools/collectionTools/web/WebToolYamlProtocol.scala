package model.tools.collectionTools.web

import de.uniwue.webtester._
import model.tools.collectionTools.{ExerciseFile, ExerciseFileYamlProtocol}
import model.{FilesSampleSolution, MyYamlProtocol}
import net.jcazevedo.moultingyaml.{DefaultYamlProtocol, YamlFormat}

object WebToolYamlProtocol extends MyYamlProtocol {

  import DefaultYamlProtocol.{IntYamlFormat, StringYamlFormat, optionFormat, yamlFormat3, yamlFormat5, yamlFormat6, immSeqFormat}

  implicit def mapFormat[K: YamlFormat, Y: YamlFormat]: YamlFormat[Map[K, Y]] = myMapFormat

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

  val webExerciseYamlFormat: YamlFormat[WebExerciseContent] = {
    implicit val ssyf : YamlFormat[SiteSpec]            = siteSpecYamlFormat
    implicit val fssyf: YamlFormat[FilesSampleSolution] = filesSampleSolutionYamlFormat
    implicit val efyf : YamlFormat[ExerciseFile]        = ExerciseFileYamlProtocol.exerciseFileYamlFormat

    yamlFormat5(WebExerciseContent)
  }

}
