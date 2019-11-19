package model

import model.core.CoreConsts.{relativePathName, resourcesBasePath}
import net.jcazevedo.moultingyaml.{YamlFormat, YamlObject, YamlString, YamlValue}
import play.api.libs.json._

final case class LongText(wrapped: String)

object LongTextJsonProtocol {

  val format: Format[LongText] = {
    val reads: Reads[LongText] = {
      case JsString(value) => JsSuccess(LongText(value))
      case _               => JsError()
    }

    val writes: Writes[LongText] = lt => JsString(lt.wrapped)

    Format(reads, writes)
  }

}

object LongTextYamlProtocol {

  val longTextYamlFormat: YamlFormat[LongText] = new YamlFormat[LongText] {

    override def write(obj: LongText): YamlValue = YamlString(obj.wrapped)

    override def read(yaml: YamlValue): LongText = yaml match {
      case YamlString(value)  => LongText(value)
      case YamlObject(fields) =>
        fields.get(YamlString(relativePathName)) match {
          case Some(YamlString(relativePath)) => LongText((resourcesBasePath / relativePath).contentAsString)
          case _                              => ???
        }
      case _                  => ???
    }

  }

}
