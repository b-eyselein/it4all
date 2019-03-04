package model.tools.nary

import enumeratum.{EnumEntry, PlayEnum}
import model.ExPart

import scala.collection.immutable.IndexedSeq


sealed abstract class NaryExPart(val urlName: String, val partName: String) extends ExPart with EnumEntry


object NaryExParts extends PlayEnum[NaryExPart] {

  override def values: IndexedSeq[NaryExPart] = findValues

  case object NaryConversionExPart extends NaryExPart("conversion", "Zahlenumwandlung")

  case object NaryAdditionExPart extends NaryExPart("addition", "Addition")

  case object TwoComplementExPart extends NaryExPart("twocomplement", "Zweierkomplement")

}

