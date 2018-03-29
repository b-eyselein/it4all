package model.programming

import model.core.{ExPart, ExParts}


sealed abstract class ProgrammingExPart(val partName: String, val urlName: String) extends ExPart

case object TestdataCreation extends ProgrammingExPart("Erstellen der Testdaten", "testdata")

case object Implementation extends ProgrammingExPart("Implementierung", "implementation")

case object ActivityDiagram extends ProgrammingExPart("Als Aktivitätsdiagramm", "activity")


object ProgrammingExParts extends ExParts[ProgrammingExPart] {

  val values = Seq(TestdataCreation, Implementation, ActivityDiagram)

}
