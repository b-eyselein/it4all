package model.tools.regex

import enumeratum.{EnumEntry, PlayEnum}
import model.tools.{ExPart, ExParts, ExerciseContent, SampleSolution}

sealed abstract class RegexExPart(val partName: String, val id: String) extends ExPart

object RegexExPart extends ExParts[RegexExPart] {

  val values: IndexedSeq[RegexExPart] = findValues

  case object RegexSingleExPart extends RegexExPart(partName = "Ausdruck erstellen", id = "regex")

}

sealed trait RegexCorrectionType extends EnumEntry

case object RegexCorrectionTypes extends PlayEnum[RegexCorrectionType] {

  override val values: IndexedSeq[RegexCorrectionType] = findValues

  case object MATCHING extends RegexCorrectionType

  case object EXTRACTION extends RegexCorrectionType

}

final case class RegexExerciseContent(
  maxPoints: Int,
  correctionType: RegexCorrectionType,
  matchTestData: Seq[RegexMatchTestData],
  extractionTestData: Seq[RegexExtractionTestData] = Seq.empty,
  sampleSolutions: Seq[SampleSolution[String]]
) extends ExerciseContent[String] {

  override def parts: Seq[ExPart] = RegexExPart.values

}

final case class RegexMatchTestData(id: Int, data: String, isIncluded: Boolean)

final case class RegexExtractionTestData(id: Int, base: String)
