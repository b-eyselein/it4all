package model

import model.Enums.ExerciseState
import model.MyYamlProtocol._
import model.core.CoreConsts._
import net.jcazevedo.moultingyaml._

import scala.language.implicitConversions
import scala.util.Try

object MyYamlProtocol {

  implicit class PimpedYaml(yaml: YamlValue) {

    def asBool: Option[Boolean] = yaml match {
      case YamlBoolean(bool) => Some(bool)
      case _                 => None
    }

    def asInt: Option[Int] = yaml match {
      case YamlNumber(bigDec) => Some(bigDec.intValue)
      case _                  => None
    }

    def asStr: Option[String] = yaml match {
      case YamlString(str) => Some(str)
      case _               => None
    }

    def forgivingStr: Option[String] = yaml match {
      case YamlString(str)   => Some(str)
      case YamlNumber(num)   => Some(num.toString)
      case YamlBoolean(bool) => Some(bool.toString)
      case YamlNull          => Some("null")
      case _                 => None
    }

    def asArray[T](mapping: YamlValue => T): Option[Seq[T]] = yaml match {
      case YamlArray(vector) => Some(vector map mapping)
      case _                 => None
    }

  }

  implicit class PimpedYamlObject(yamlObject: YamlObject) {

    implicit def string2YamlString(str: String): YamlString = YamlString(str)

    private def someField[T](fieldName: String, f: YamlValue => T): T = yamlObject.fields get fieldName match {
      case Some(field) => f(field)
      case None        => deserializationError(msg = s"A field with name '$fieldName' was expected but not found!", fieldNames = List(fieldName))
    }

    private def optField[T](fieldName: String, f: YamlValue => T): Option[T] = yamlObject.fields get fieldName map f


    def boolField(fieldName: String): Boolean = someField(fieldName, _.asBool)
      .getOrElse(deserializationError(s"The field with name '$fieldName' was supposed to have a type of 'Int'"))

    def intField(fieldName: String): Int = someField(fieldName, _.asInt)
      .getOrElse(deserializationError(s"The field with name '$fieldName' was supposed to have a type of 'Int'"))

    def stringField(fieldName: String): String = someField(fieldName, _.asStr)
      .getOrElse(deserializationError(s"The field with name '$fieldName' was supposed to have a type of 'String'"))

    def forgivingStringField(fieldName: String): String = someField(fieldName, _.forgivingStr)
      .getOrElse(deserializationError(s"The field with name '$fieldName' was supposed to have a type of 'String'"))

    def optStringField(fieldName: String): Option[String] = optField(fieldName, identity).flatMap(_.asStr)

    def optForgivingStringField(fieldName: String): Option[String] = optField(fieldName, identity).flatMap(_.forgivingStr)

    def arrayField[T](fieldName: String, mapping: YamlValue => T): Seq[T] = someField(fieldName, _ asArray mapping)
      .getOrElse(deserializationError(s"The field with name '$fieldName' was supposed to be an array"))

    def optArrayField[T](fieldName: String, mapping: YamlValue => T): Seq[T] = optField(fieldName, identity) flatMap (_ asArray mapping) getOrElse Seq.empty

    def enumField[T](fieldName: String, valueOf: String => T, default: T): T = Try(valueOf(stringField(fieldName))) getOrElse default

    def objectField[T](fieldName: String, yamlFormat: YamlFormat[T]): T = someField(fieldName, _ convertTo[T] yamlFormat)

  }

}

abstract class MyYamlProtocol extends DefaultYamlProtocol {

  implicit def string2YamlString(str: String): YamlString = YamlString(str)

  implicit def int2YamlNumber(num: Int): YamlNumber = YamlNumber(num)

  implicit def bool2YamlBoolean(bool: Boolean): YamlBoolean = YamlBoolean(bool)

  protected def writeBaseValues(bv: BaseValues): Map[YamlValue, YamlValue] = Map(
    YamlString(ID_NAME) -> bv.id,
    YamlString(TITLE_NAME) -> bv.title,
    YamlString(AUTHOR_NAME) -> bv.author,
    YamlString(TEXT_NAME) -> bv.text,
    YamlString(STATE_NAME) -> bv.state.name
  )

  abstract class ExYamlFormat[E <: CompleteEx[_]] extends MyYamlFormat[E] {

    override def readObject(yamlObject: YamlObject): E = readRest(yamlObject,
      BaseValues(
        yamlObject.intField(ID_NAME),
        yamlObject.stringField(TITLE_NAME),
        yamlObject.stringField(AUTHOR_NAME),
        yamlObject.stringField(TEXT_NAME),
        yamlObject.enumField(STATE_NAME, ExerciseState.valueOf, ExerciseState.CREATED)
      )
    )

    override def write(completeEx: E): YamlObject = YamlObject(writeBaseValues(completeEx.ex.baseValues) ++ writeRest(completeEx))

    protected def readRest(yamlObject: YamlObject, baseValues: BaseValues): E

    protected def writeRest(completeEx: E): Map[YamlValue, YamlValue]

  }

  abstract class MyYamlFormat[T] extends YamlFormat[T] {

    override def read(yaml: YamlValue): T = yaml match {
      case yamlObj: YamlObject => readObject(yamlObj)
      case other               => deserializationError(s"Awaited an yaml object, instead got ${other.getClass}")
    }

    def readObject(yamlObject: YamlObject): T

  }

}