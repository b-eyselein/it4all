package model.tools.collectionTools

import model.core.{LongText, LongTextYamlProtocol}
import model.{ExerciseState, MyYamlProtocol, SemanticVersion}
import net.jcazevedo.moultingyaml.{DefaultYamlProtocol, YamlFormat, YamlValue}
import play.api.libs.json.{Format, JsValue}

object ToolYamlProtocol extends MyYamlProtocol {

  import DefaultYamlProtocol._

  val exerciseStateYamlFormat: EnumYamlFormat[ExerciseState] = new EnumYamlFormat(ExerciseState)

  val exerciseCollectionYamlFormat: YamlFormat[ExerciseCollection] = {
    implicit val esyf: YamlFormat[ExerciseState] = exerciseStateYamlFormat

    yamlFormat7(ExerciseCollection)
  }

  def exerciseYamlFormat[EC <: ExerciseContent](contentYamlFormat: YamlFormat[EC], contentJsonFormat: Format[EC]): YamlFormat[Exercise] = {
    implicit val svyf: YamlFormat[SemanticVersion] = ToolYamlProtocol.semanticVersionYamlFormat
    implicit val esyf: YamlFormat[ExerciseState]   = exerciseStateYamlFormat
    implicit val ltyf: YamlFormat[LongText]        = LongTextYamlProtocol.longTextYamlFormat

    implicit val jvyf: YamlFormat[JsValue] = new YamlFormat[JsValue] {

      override def write(obj: JsValue): YamlValue = contentYamlFormat.write(contentJsonFormat.reads(obj).get)

      override def read(yaml: YamlValue): JsValue = contentJsonFormat.writes(contentYamlFormat.read(yaml))

    }

    yamlFormat9(Exercise)
  }

}
