package model.tools.programming

import enumeratum.{EnumEntry, PlayEnum}
import model.{ExPart, ExParts, Topic}

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

  val ForLoops: Topic   = Topic("FL", "programming", "For-Schleifen")
  val WhileLoops: Topic = Topic("WL", "programming", "While-Schleifen")
  val Conditions: Topic = Topic("C", "programming", "Bedingungen")
  val Lists: Topic      = Topic("L", "programming", "Listen")
  val Tuples: Topic     = Topic("T", "programming", "Tuples")
  val Dicts: Topic      = Topic("D", "programming", "Dictionaries")
  val Classes: Topic    = Topic("CL", "programming", "Klassen")
  val Exceptions: Topic = Topic("E", "programming", "Exceptions")
  val Maths: Topic      = Topic("M", "programming", "Mathematik")
  val Strings: Topic    = Topic("S", "programming", "Strings")
  val Slicing: Topic    = Topic("SL", "programming", "Slicing")
  val Recursion: Topic  = Topic("R", "programming", "Rekursion")

  val values: Seq[Topic] = Seq(
    ForLoops,
    WhileLoops,
    Conditions,
    Lists,
    Tuples,
    Dicts,
    Classes,
    Exceptions,
    Maths,
    Strings,
    Slicing,
    Recursion
  )
}

// Unit Test Types

@deprecated
sealed trait UnitTestType extends EnumEntry

@deprecated
case object UnitTestType extends PlayEnum[UnitTestType] {

  override val values: IndexedSeq[UnitTestType] = findValues

  case object Simplified extends UnitTestType

  case object Normal extends UnitTestType

}
