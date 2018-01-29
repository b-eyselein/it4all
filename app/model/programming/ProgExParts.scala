package model.programming

import controllers.exes.idPartExes.{ExPart, ExParts}


sealed abstract class ProgExPart(val partName: String, val urlName: String) extends ExPart

case object TestdataCreation extends ProgExPart("Erstellen der Testdaten", "testdata")

case object Implementation extends ProgExPart("Implementierung", "implementation")

case object ActivityDiagram extends ProgExPart("Als Aktivitätsdiagramm", "activity")


object ProgExParts extends ExParts[ProgExPart] {

  val values = Seq(TestdataCreation, Implementation, ActivityDiagram)

}
