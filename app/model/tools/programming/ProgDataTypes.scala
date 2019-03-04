package model.tools.programming


import enumeratum.{EnumEntry, PlayEnum}
import play.api.libs.json._

import scala.collection.immutable.IndexedSeq
import scala.util.Try
import scala.util.matching.Regex

// FIXME: use enumeratum!!!

sealed trait ProgDataType extends EnumEntry {

  def toJson(str: String): JsValue

  def typeName: String

  def defaultValue: ProgLanguage => String

}

object ProgDataTypes extends PlayEnum[ProgDataType] {

  override val values: IndexedSeq[ProgDataType] = findValues


  private val ListPattern: Regex = "list<(.*?)>".r

  private def string2IntBigDecimal(str: String): BigDecimal = BigDecimal(Try(str.toInt) getOrElse 0)

  private def string2FloatBigDecimal(str: String): BigDecimal = BigDecimal(Try(str.toDouble) getOrElse 0d)


  sealed abstract class NonGenericProgDataType(val typeName: String, val convertToJson: String => JsValue, val defaultValue: ProgLanguage => String) extends ProgDataType {

    override def toJson(str: String): JsValue = convertToJson(str)

  }

  sealed abstract class GenericProgDataType extends ProgDataType


  case object VOID extends NonGenericProgDataType("void", _ => JsString("void"), _ => "void")

  case object INTEGER extends NonGenericProgDataType("int", str => JsNumber(string2IntBigDecimal(str)), _ => "0")

  case object FLOAT extends NonGenericProgDataType("float", str => JsNumber(string2FloatBigDecimal(str)), _ => "0.0")

  case object BOOLEAN extends NonGenericProgDataType("boolean", str => JsBoolean(str == "true"), {
    case ProgLanguages.PYTHON_3 => "False"
    case _                      => "false"
  })

  case object STRING extends NonGenericProgDataType("string", str => JsString(str), _ => "\"\"")

  final case class LIST(subtype: ProgDataType) extends GenericProgDataType {

    override def toJson(str: String): JsValue = JsArray(str.split(", ") map subtype.toJson)

    override val typeName: String = s"list<${subtype.typeName}>"

    override def defaultValue: ProgLanguage => String = {
      case ProgLanguages.PYTHON_3 => "[]"
      case _                      => ""
    }
  }

  def byName(str: String): Option[ProgDataType] = str match {
    case "void"    => Some(VOID)
    case "int"     => Some(INTEGER)
    case "float"   => Some(FLOAT)
    case "boolean" => Some(BOOLEAN)
    case "string"  => Some(STRING)

    case ListPattern(c) =>
      val subType: Option[ProgDataType] = ProgDataTypes.byName(c)
      subType map LIST

    case _ => None
  }

}
