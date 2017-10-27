package model.web

import com.fasterxml.jackson.annotation.JsonGetter

case class Attribute(key: String, value: String) {

  def forDB: String = key + Attribute.KEY_VALUE_CHARACTER + value

  @JsonGetter(value = "key")
  def getKey: String = key

  @JsonGetter(value = "value")
  def getValue: String = value

}

object Attribute {

  val KEY_VALUE_CHARACTER = "="

  def fromString(keyEqualsValue: String): Attribute = if (keyEqualsValue.contains(KEY_VALUE_CHARACTER)) {
    val kv = keyEqualsValue.split(KEY_VALUE_CHARACTER)
    new Attribute(kv(0), kv(1))
  } else {
    new Attribute(keyEqualsValue, keyEqualsValue)
  }

}