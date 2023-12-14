package model.tools.programming

import enumeratum.Enum
import model.ExPart

sealed abstract class ProgExPart(
  val partName: String,
  val id: String
) extends ExPart

object ProgExPart extends Enum[ProgExPart] {

  case object TestCreation   extends ProgExPart(partName = "Erstellen der Tests", id = "testCreation")
  case object Implementation extends ProgExPart(partName = "Implementierung", id = "implementation")

  override def values: IndexedSeq[ProgExPart] = findValues

}
