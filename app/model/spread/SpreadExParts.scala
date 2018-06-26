package model.spread

import enumeratum.{Enum, EnumEntry}
import model.ExPart

import scala.collection.immutable.IndexedSeq


sealed abstract class SpreadExPart(val fileName: String, val toolName: String) extends ExPart with EnumEntry {

  override def urlName: String = fileName

  override def partName: String = toolName

}


object SpreadExParts extends Enum[SpreadExPart] {

  val values: IndexedSeq[SpreadExPart] = findValues

  case object MSExcel extends SpreadExPart("xlsx", "MS Excel")

  case object OOCalc extends SpreadExPart("ods", "OpenOffice")

}
