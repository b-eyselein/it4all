package model.programming

import enumeratum.{Enum, EnumEntry}
import model.ExPart

import scala.collection.immutable.IndexedSeq


sealed abstract class ProgrammingExPart(val partName: String, val urlName: String) extends ExPart with EnumEntry


object ProgrammingExParts extends Enum[ProgrammingExPart] {

  val values: IndexedSeq[ProgrammingExPart] = findValues

  case object TestdataCreation extends ProgrammingExPart(partName = "Erstellen der Testdaten", urlName = "testdata")

  case object Implementation extends ProgrammingExPart(partName = "Implementierung", urlName = "implementation")

  case object ActivityDiagram extends ProgrammingExPart(partName = "Als Aktivitätsdiagramm", urlName = "activity")

}
