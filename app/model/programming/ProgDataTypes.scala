package model.programming


import play.api.libs.json._

import scala.util.Try

object ProgDataTypes {

  private def string2IntBigDecimal(str: String): BigDecimal = BigDecimal(Try(str.toInt) getOrElse 0)

  private def string2FloatBigDecimal(str: String): BigDecimal = BigDecimal(Try(str.toDouble) getOrElse 0d)

  sealed abstract class ProgDataType(val typeName: String, val toJson: String => JsValue)


  case object INTEGER extends ProgDataType("int", str => JsNumber(string2IntBigDecimal(str)))

  case object FLOAT extends ProgDataType("float", str => JsNumber(string2FloatBigDecimal(str)))

  case object BOOLEAN extends ProgDataType("boolean", str => JsBoolean(str == "true"))

  case object STRING extends ProgDataType("string", str => JsString(str))


  val values: Seq[ProgDataType] = Seq(INTEGER, FLOAT, BOOLEAN, STRING)

  def byName(str: String): Option[ProgDataType] = values.find(p => p.typeName == str)

}
