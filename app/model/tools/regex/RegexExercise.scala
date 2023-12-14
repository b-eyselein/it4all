package model.tools.regex

import enumeratum.{Enum, EnumEntry, PlayJsonEnum}
import model.ExerciseContent

sealed trait RegexCorrectionType extends EnumEntry

object RegexCorrectionType extends Enum[RegexCorrectionType] with PlayJsonEnum[RegexCorrectionType] {

  case object MATCHING   extends RegexCorrectionType
  case object EXTRACTION extends RegexCorrectionType

  override val values: IndexedSeq[RegexCorrectionType] = findValues

}

final case class RegexExerciseContent(
  maxPoints: Int,
  correctionType: RegexCorrectionType,
  matchTestData: Seq[RegexMatchTestData],
  extractionTestData: Seq[RegexExtractionTestData] = Seq.empty,
  sampleSolutions: Seq[String]
) extends ExerciseContent {

  override protected type S = String

}

final case class RegexMatchTestData(id: Int, data: String, isIncluded: Boolean)

final case class RegexExtractionTestData(id: Int, base: String)
