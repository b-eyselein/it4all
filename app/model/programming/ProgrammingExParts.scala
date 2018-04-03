package model.programming

import model.core.{ExPart, ExParts}


sealed abstract class ProgrammingExPart(val partName: String, val urlName: String) extends ExPart


case object TestdataCreation extends ProgrammingExPart(partName = "Erstellen der Testdaten", urlName = "testdata")

case object Implementation extends ProgrammingExPart(partName = "Implementierung", urlName = "implementation")

case object ActivityDiagram extends ProgrammingExPart(partName = "Als Aktivitätsdiagramm", urlName = "activity")


object ProgrammingExParts extends ExParts[ProgrammingExPart] {

  val values = Seq(TestdataCreation, Implementation, ActivityDiagram)

}
