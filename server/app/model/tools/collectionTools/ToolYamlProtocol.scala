package model.tools.collectionTools

import model.core.{LongText, LongTextYamlProtocol}
import model.{MyYamlProtocol, SemanticVersion}
import net.jcazevedo.moultingyaml.{DefaultYamlProtocol, YamlFormat, YamlValue}
import play.api.libs.json.{Format, JsValue}

object ToolYamlProtocol extends MyYamlProtocol {

  import DefaultYamlProtocol._

  val exerciseCollectionYamlFormat: YamlFormat[ExerciseCollection] = yamlFormat6(ExerciseCollection)

  def exerciseYamlFormat[EC <: ExerciseContent](contentYamlFormat: YamlFormat[EC], contentJsonFormat: Format[EC]): YamlFormat[Exercise] = {
    implicit val svyf: YamlFormat[SemanticVersion] = ToolYamlProtocol.semanticVersionYamlFormat
    implicit val ltyf: YamlFormat[LongText]        = LongTextYamlProtocol.longTextYamlFormat

    implicit val jvyf: YamlFormat[JsValue] = new YamlFormat[JsValue] {

      override def write(obj: JsValue): YamlValue = contentYamlFormat.write(contentJsonFormat.reads(obj).get)

      override def read(yaml: YamlValue): JsValue = contentJsonFormat.writes(contentYamlFormat.read(yaml))

    }

    yamlFormat8(Exercise)
  }

}
