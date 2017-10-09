package model

import com.fasterxml.jackson.annotation.JsonGetter

case class Attribute(key: String, value: String) {

  def forDB = key + Attribute.KEY_VALUE_CHARACTER + value

  @JsonGetter(value = "key")
  def getKey = key

  @JsonGetter(value = "value")
  def getValue = value

}

object Attribute {

  val KEY_VALUE_CHARACTER = "="

  def fromString(keyEqualsValue: String): Attribute = if (keyEqualsValue.contains(KEY_VALUE_CHARACTER)) {
    val kv = keyEqualsValue.split(KEY_VALUE_CHARACTER)
    new Attribute(kv(0).toString, kv(1).toString)
  } else {
    new Attribute(keyEqualsValue, keyEqualsValue)
  }

}