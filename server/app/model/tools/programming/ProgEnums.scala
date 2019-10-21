package model.tools.programming

import enumeratum.{EnumEntry, PlayEnum}
import model.ExPart


sealed abstract class ProgExPart(val partName: String, val urlName: String) extends ExPart with EnumEntry


object ProgExParts extends PlayEnum[ProgExPart] {

  override def values: IndexedSeq[ProgExPart] = findValues


  case object TestCreation extends ProgExPart(partName = "Erstellen der Tests", urlName = "testCreation")

  case object Implementation extends ProgExPart(partName = "Implementierung", urlName = "implementation")

  case object ActivityDiagram extends ProgExPart(partName = "Als Aktivitätsdiagramm", urlName = "activity")

}


sealed trait UnitTestType extends EnumEntry


case object UnitTestTypes extends PlayEnum[UnitTestType] {

  override val values: IndexedSeq[UnitTestType] = findValues


  case object Simplified extends UnitTestType

  case object Normal extends UnitTestType

  case object Both extends UnitTestType

}
