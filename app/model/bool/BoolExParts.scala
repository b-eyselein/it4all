package model.bool

import model.core.{ExPart, ExParts}

object BoolExParts extends ExParts[BoolExPart] {

  override def values: Seq[BoolExPart] = Seq(FormulaCreation, TableFillout)

}

sealed abstract class BoolExPart(val urlName: String, val partName: String) extends ExPart

case object FormulaCreation extends BoolExPart("formula", "Formel erstellen")

case object TableFillout extends BoolExPart("fillout", "Wahrheitstabelle ausfüllen")