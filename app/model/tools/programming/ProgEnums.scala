package model.tools.programming

import enumeratum.{EnumEntry, PlayEnum}
import model.tools.{ExPart, ExParts}

// Exercise Parts

sealed abstract class ProgExPart(val partName: String, val urlName: String) extends ExPart

object ProgExPart extends ExParts[ProgExPart] {

  override def values: IndexedSeq[ProgExPart] = findValues

  case object TestCreation extends ProgExPart(partName = "Erstellen der Tests", urlName = "testCreation")

  case object Implementation extends ProgExPart(partName = "Implementierung", urlName = "implementation")

  case object ActivityDiagram extends ProgExPart(partName = "Als Aktivitätsdiagramm", urlName = "activity")

}

// Tags

sealed abstract class ProgrammingExerciseTag(val buttonContent: String, val title: String) extends EnumEntry

object ProgrammingExerciseTag extends PlayEnum[ProgrammingExerciseTag] {

  val values: IndexedSeq[ProgrammingExerciseTag] = findValues

  case object ForLoops extends ProgrammingExerciseTag("FL", "For-Schleifen")

  case object WhileLoops extends ProgrammingExerciseTag("WL", "While-Schleifen")

  case object Conditions extends ProgrammingExerciseTag("C", "Bedingungen")

  case object Lists extends ProgrammingExerciseTag("L", "Listen")

  case object Tuples extends ProgrammingExerciseTag("T", "Tuples")

  case object Dictionaries extends ProgrammingExerciseTag("D", "Dictionaries")

  case object Classes extends ProgrammingExerciseTag("C", "Klassen")

  case object Exceptions extends ProgrammingExerciseTag("E", "Exceptions")

  case object Math extends ProgrammingExerciseTag("M", "Mathematik")

  case object Strings extends ProgrammingExerciseTag("S", "Strings")

  case object Slicing extends ProgrammingExerciseTag("SL", "Slicing")

  case object Recursion extends ProgrammingExerciseTag("R", "Rekursion")

}

// Unit Test Types

sealed trait UnitTestType extends EnumEntry

case object UnitTestTypes extends PlayEnum[UnitTestType] {

  override val values: IndexedSeq[UnitTestType] = findValues

  case object Simplified extends UnitTestType

  case object Normal extends UnitTestType

  //  case object Both extends UnitTestType

}
