package model.tools.programming

import enumeratum.{EnumEntry, PlayEnum}
import model.ExPart

import scala.collection.immutable.IndexedSeq


sealed abstract class ProgExPart(val partName: String, val urlName: String) extends ExPart with EnumEntry


object ProgExParts extends PlayEnum[ProgExPart] {

  val values: IndexedSeq[ProgExPart] = findValues

  case object TestCreation extends ProgExPart(partName = "Erstellen der Tests", urlName = "testCreation")

  case object Implementation extends ProgExPart(partName = "Implementierung", urlName = "implementation")

  case object ActivityDiagram extends ProgExPart(partName = "Als Aktivitätsdiagramm", urlName = "activity")

}
