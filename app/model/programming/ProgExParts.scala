package model.programming

import controllers.exes.idPartExes.ExPart

object ProgExParts {

  sealed abstract class ProgExPart(val partName: String, val urlName: String) extends ExPart

  case object TestdataCreation extends ProgExPart("Erstellen der Testdaten", "testdata")

  case object Implementation extends ProgExPart("Implementierung", "implementation")

  val values = Seq(TestdataCreation, Implementation)

}
