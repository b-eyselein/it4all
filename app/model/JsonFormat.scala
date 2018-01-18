package model

import play.api.libs.json._

import scala.language.{implicitConversions, postfixOps}
import scala.util.{Failure, Success, Try}

class JsTypeException(msg: String) extends Exception(msg)

trait JsonFormat {

  implicit class PimpedJsValue(jsValue: JsValue) {

    def tryAsBool: Try[Boolean] = jsValue match {
      case JsBoolean(bool) => Success(bool)
      case other           => Failure(new JsTypeException("Awaited type JsBoolean, but got " + other.getClass))
    }

    def asBool: Option[Boolean] = jsValue match {
      case JsBoolean(bool) => Some(bool)
      case _               => None
    }


    def asChar: Option[Char] = jsValue.asStr map (_ apply 0)

    def tryAsInt: Try[Int] = jsValue match {
      case JsNumber(bigDecimal) => Success(bigDecimal.intValue)
      case other                => Failure(new JsTypeException("Awaited type JsNumber, but got " + other.getClass))
    }

    def asInt: Option[Int] = jsValue match {
      case JsNumber(bigDecimal) => Some(bigDecimal.intValue)
      case _                    => None
    }

    def asMap[A, B](keyType: (String => A), valueType: (JsValue => Option[B])): Option[Map[A, B]] = asObj map {
      jsMap: JsObject => jsMap.value flatMap { jsObj => valueType(jsObj._2) map (value => (keyType(jsObj._1), value)) } toMap
    }

    def asObj: Option[JsObject] = jsValue match {
      case jsObj: JsObject => Some(jsObj)
      case _               => None
    }

    def tryAsStr: Try[String] = jsValue match {
      case JsString(str) => Success(str)
      case other         => Failure(new JsTypeException("Awaited type JsString, but got " + other.getClass))
    }

    def asStr: Option[String] = jsValue match {
      case JsString(str) => Some(str)
      case _             => None
    }

    def asForgivingString: String = jsValue match {
      case JsString(str)   => str
      case JsNumber(i)     => i.toString
      case JsBoolean(bool) => bool.toString
      case JsNull          => "null"
      case other           => other.toString
    }

    def asArray[T](func: JsValue => Option[T]): Option[Seq[T]] = jsValue match {
      case JsArray(values) => Some((values map func) flatten)
      case _               => None
    }

  }

  implicit class PimpedJsObject(jsObject: JsObject) {

    def arrayField[T](fieldName: String, fieldType: JsValue => Option[T]): Option[Seq[T]] = jsObject.value get fieldName flatMap {
      case JsArray(content) => Some((content map fieldType) flatten)
      case _                => None
    }

    def field(fieldName: String): Option[JsValue] = jsObject.value get fieldName

    def objField(fieldName: String): Option[JsObject] = jsObject.value get fieldName flatMap (_.asObj)

    def boolField(fieldName: String): Option[Boolean] = jsObject.value get fieldName flatMap (_.asBool)

    def charField(fieldName: String): Option[Char] = jsObject.value get fieldName flatMap (_.asChar)

    def intField(fieldName: String): Option[Int] = jsObject.value get fieldName flatMap (_.asInt)

    def stringField(fieldName: String): Option[String] = jsObject.value get fieldName flatMap (_.asStr)

    def forgivingStringField(fieldName: String): Option[String] = jsObject.value get fieldName map (_.asForgivingString)

    def enumField[T](fieldName: String, func: String => T): Option[T] = jsObject.value get fieldName flatMap (_.asStr) map func

  }

}
