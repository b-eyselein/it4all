package model.tools.programming

import enumeratum.{EnumEntry, PlayEnum}
import model.tools.{ExPart, ExParts, Topic}

// Exercise Parts

sealed abstract class ProgExPart(val partName: String, val id: String) extends ExPart

object ProgExPart extends ExParts[ProgExPart] {

  override def values: IndexedSeq[ProgExPart] = findValues

  case object TestCreation extends ProgExPart(partName = "Erstellen der Tests", id = "testCreation")

  case object Implementation extends ProgExPart(partName = "Implementierung", id = "implementation")

  case object ActivityDiagram extends ProgExPart(partName = "Als Aktivitätsdiagramm", id = "activity")

}

// Topics

object ProgrammingTopics {
  val values: Seq[Topic] = Seq(
    Topic("FL", "programming", "For-Schleifen"),
    Topic("WL", "programming", "While-Schleifen"),
    Topic("C", "programming", "Bedingungen"),
    Topic("L", "programming", "Listen"),
    Topic("T", "programming", "Tuples"),
    Topic("D", "programming", "Dictionaries"),
    Topic("CL", "programming", "Klassen"),
    Topic("E", "programming", "Exceptions"),
    Topic("M", "programming", "Mathematik"),
    Topic("S", "programming", "Strings"),
    Topic("SL", "programming", "Slicing"),
    Topic("R", "programming", "Rekursion")
  )
}

// Unit Test Types

sealed trait UnitTestType extends EnumEntry

case object UnitTestTypes extends PlayEnum[UnitTestType] {

  override val values: IndexedSeq[UnitTestType] = findValues

  case object Simplified extends UnitTestType

  case object Normal extends UnitTestType

  //  case object Both extends UnitTestType

}
