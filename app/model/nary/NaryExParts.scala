package model.nary

import enumeratum.{Enum, EnumEntry}
import model.ExPart

import scala.collection.immutable.IndexedSeq


sealed abstract class NaryExPart(val urlName: String, val partName: String) extends ExPart with EnumEntry


object NaryExParts extends Enum[NaryExPart] {

  override def values: IndexedSeq[NaryExPart] = findValues

  case object NaryConversionExPart extends NaryExPart("conversion", "Zahlenumwandlung")

  case object NaryAdditionExPart extends NaryExPart("addition", "Addition")

  case object TwoComplementExPart extends NaryExPart("twocomplement", "Zweierkomplement")

}

