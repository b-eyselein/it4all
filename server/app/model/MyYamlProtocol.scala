package model

import enumeratum.{EnumEntry, PlayEnum}
import model.tools.collectionTools.{ExerciseFile, ExerciseFileYamlProtocol}
import net.jcazevedo.moultingyaml._
import play.api.Logger
import play.api.libs.json._

object MapYamlHelper extends DefaultYamlProtocol {

  final case class MapYamlHelperClass[K, V](key: K, value: V)

  def stringMapYamlFormat[K: YamlFormat, V: YamlFormat]: YamlFormat[Map[K, V]] = new YamlFormat[Map[K, V]] {

    private val mapYamlHelperYamlFormat: YamlFormat[List[MapYamlHelperClass[K, V]]] =
      DefaultYamlProtocol.listFormat(yamlFormat2(MapYamlHelperClass[K, V]))

    override def read(yaml: YamlValue): Map[K, V] = mapYamlHelperYamlFormat.read(yaml)
      .map { case MapYamlHelperClass(key, value) => (key, value) }
      .toMap

    override def write(obj: Map[K, V]): YamlValue = mapYamlHelperYamlFormat.write(
      obj.toList.map { case (key, value) => MapYamlHelperClass(key, value) }
    )

  }

  def mapToJson(yamlValue: YamlValue): JsValue = yamlValue match {
    case YamlArray(arrayValues) => JsArray(arrayValues.map(mapToJson))
    case YamlSet(content)       => JsArray(content.toSeq.map(mapToJson))

    case YamlObject(yamlFields) => JsObject(yamlFields.map {
      case (key, value) => key.toString -> mapToJson(value)
    })

    case YamlDate(date) => JsString(date.toString)

    case YamlNaN | YamlNegativeInf | YamlPositiveInf => JsNull

    case YamlString(str)        => JsString(str)
    case YamlBoolean(bool)      => JsBoolean(bool)
    case YamlNull               => JsNull
    case YamlNumber(bigDecimal) => JsNumber(bigDecimal)

  }

}


trait MyYamlProtocol {

  import DefaultYamlProtocol._

  private val logger = Logger(classOf[MyYamlProtocol])

  protected class EnumYamlFormat[E <: EnumEntry](protected val enum: PlayEnum[E]) extends YamlFormat[E] {

    override def read(yaml: YamlValue): E = yaml match {
      case YamlString(str) => enum.withNameInsensitive(str)
      case other           =>
        logger.error(s"Could not read yaml type ${other.getClass} as enum!")
        ???
    }

    override def write(obj: E): YamlValue = YamlString(obj.entryName)

  }

  protected val jsonValueYamlFormat: YamlFormat[JsValue] = new YamlFormat[JsValue] {

    override def read(yaml: YamlValue): JsValue = MapYamlHelper.mapToJson(yaml)

    override def write(obj: JsValue): YamlValue = {
      logger.error("TODO: implement yaml printing of json values!")
      ???
    }

  }

  protected val semanticVersionYamlFormat: YamlFormat[SemanticVersion] = yamlFormat3(SemanticVersion)

  protected val stringSampleSolutionYamlFormat: YamlFormat[StringSampleSolution] = yamlFormat2(StringSampleSolution)

  protected val filesSampleSolutionYamlFormat: YamlFormat[FilesSampleSolution] = {
    implicit val efyf: YamlFormat[ExerciseFile] = ExerciseFileYamlProtocol.exerciseFileYamlFormat

    yamlFormat2(FilesSampleSolution)
  }

  protected def myMapFormat[K: YamlFormat, V: YamlFormat]: YamlFormat[Map[K, V]] = new YamlFormat[Map[K, V]] {

    final case class KeyValueMapEntry(key: K, value: V)

    implicit val keyValueMapEntryYamlFormat: YamlFormat[Seq[KeyValueMapEntry]] = immSeqFormat(yamlFormat2(KeyValueMapEntry))

    override def write(obj: Map[K, V]): YamlValue = keyValueMapEntryYamlFormat.write(
      obj.toSeq.map { case (key, value) => KeyValueMapEntry(key, value) }
    )

    override def read(yaml: YamlValue): Map[K, V] = keyValueMapEntryYamlFormat
      .read(yaml)
      .map { case KeyValueMapEntry(key, value) => (key, value) }
      .toMap

  }

}
