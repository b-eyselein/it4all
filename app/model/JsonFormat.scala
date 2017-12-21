package model

import play.api.libs.json._

import scala.language.{implicitConversions, postfixOps}

trait JsonFormat {

  implicit class PimpedJsValue(jsValue: JsValue) {

    def asBool: Option[Boolean] = jsValue match {
      case JsBoolean(bool) => Some(bool)
      case _               => None
    }

    def asChar: Option[Char] = jsValue.asStr map (_ apply 0)

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

    def asStr: Option[String] = jsValue match {
      case JsString(str) => Some(str)
      case _             => None
    }

    def asArray[T](func: JsValue => T): Option[Seq[T]] = jsValue match {
      case JsArray(values) => Some(values map func)
      case _               => None
    }

  }

  implicit class PimpedJsObject(jsObject: JsObject) {

    def arrayField[T](fieldName: String, fieldType: JsValue => Option[T]): Option[Seq[T]] = jsObject.value get fieldName flatMap {
      case JsArray(content) => Some((content map fieldType) flatten)
      case _                => None
    }

    def boolField(fieldName: String): Option[Boolean] = jsObject.value get fieldName flatMap (_.asBool)

    def charField(fieldName: String): Option[Char] = jsObject.value get fieldName flatMap (_.asChar)

    def intField(fieldName: String): Option[Int] = jsObject.value get fieldName flatMap (_.asInt)

    def stringField(fieldName: String): Option[String] = jsObject.value get fieldName flatMap (_.asStr)

  }

}
