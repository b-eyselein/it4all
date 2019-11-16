package model.tools.web

import de.uniwue.webtester._
import model._
import net.jcazevedo.moultingyaml._

object WebToolYamlProtocol extends MyYamlProtocol {

  private val htmlElementSpecYamlFormat: YamlFormat[HtmlElementSpec] = yamlFormat5(HtmlElementSpec)

  private val jsTaskYamlFormat: YamlFormat[JsTask] = {
    implicit val hesyf: YamlFormat[HtmlElementSpec] = htmlElementSpecYamlFormat

    implicit val jayf: YamlFormat[JsAction] = {
      implicit val jatyf: YamlFormat[JsActionType] = new EnumYamlFormat(JsActionType)

      yamlFormat3(JsAction)
    }

    yamlFormat5(JsTask)
  }


  private val siteSpecYamlFormat: YamlFormat[SiteSpec] = {
    implicit val htyf: YamlFormat[HtmlTask] = {
      implicit val hesyf: YamlFormat[HtmlElementSpec] = htmlElementSpecYamlFormat

      yamlFormat2(HtmlTask)
    }

    implicit val jtyf: YamlFormat[JsTask] = jsTaskYamlFormat

    yamlFormat3(SiteSpec)
  }

  val webExerciseYamlFormat: YamlFormat[WebExercise] = {
    implicit val svyf: YamlFormat[SemanticVersion] = semanticVersionYamlFormat

    implicit val esyf: YamlFormat[ExerciseState] = exerciseStateYamlFormat

    implicit val ssyf: YamlFormat[SiteSpec] = siteSpecYamlFormat

    implicit val fssyf: YamlFormat[FilesSampleSolution] = filesSampleSolutionYamlFormat

    implicit val efyf: YamlFormat[ExerciseFile] = exerciseFileYamlFormat


    yamlFormat12(WebExercise)
  }

}
