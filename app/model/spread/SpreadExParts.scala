package model.spread

import model.core.ExPart

sealed abstract class SpreadExPart(val fileName: String, val toolName: String) extends ExPart {

  override def urlName: String = fileName

  override def partName: String = toolName

}

case object MSExcel extends SpreadExPart("xlsx", "MS Excel")

case object OOCalc extends SpreadExPart("ods", "OpenOffice")

object SpreadExParts {

  val values: Seq[SpreadExPart] = Seq(MSExcel, OOCalc)

}
