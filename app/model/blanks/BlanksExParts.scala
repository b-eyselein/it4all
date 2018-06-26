package model.blanks

import enumeratum.{Enum, EnumEntry}
import model.ExPart

import scala.collection.immutable.IndexedSeq


sealed abstract class BlanksExPart(val partName: String, val urlName: String) extends ExPart with EnumEntry


object BlanksExParts extends Enum[BlanksExPart] {

  val values: IndexedSeq[BlanksExPart] = findValues

  case object BlankExSinglePart extends BlanksExPart("Lückentext", "blanks")

}
