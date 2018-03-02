package model.nary

import model.core.{ExPart, ExParts}

object NaryExParts extends ExParts[NaryExPart] {

  override def values: Seq[NaryExPart] = Seq(NaryConversionExPart, NaryAdditionExPart, TwoComplementExPart)

}

sealed abstract class NaryExPart(val urlName: String, val partName: String) extends ExPart

case object NaryConversionExPart extends NaryExPart("conversion", "Zahlenumwandlung")

case object NaryAdditionExPart extends NaryExPart("addition", "Addition")

case object TwoComplementExPart extends NaryExPart("twocomplement", "Zweierkomplement")