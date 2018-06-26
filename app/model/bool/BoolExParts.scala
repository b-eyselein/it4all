package model.bool

import enumeratum.{Enum, EnumEntry}
import model.ExPart

import scala.collection.immutable.IndexedSeq


sealed abstract class BoolExPart(val urlName: String, val partName: String) extends ExPart with EnumEntry


object BoolExParts extends Enum[BoolExPart] {

  override def values: IndexedSeq[BoolExPart] = findValues

  case object FormulaCreation extends BoolExPart("formula", "Formel erstellen")

  case object TableFillout extends BoolExPart("fillout", "Wahrheitstabelle ausfüllen")

}
