package model.tools.collectionTools.regex

import enumeratum.{EnumEntry, PlayEnum}
import model.StringSampleSolution
import model.tools.collectionTools.StringExerciseContent

sealed trait RegexCorrectionType extends EnumEntry

case object RegexCorrectionTypes extends PlayEnum[RegexCorrectionType] {

  override val values: IndexedSeq[RegexCorrectionType] = findValues


  case object MATCHING extends RegexCorrectionType

  case object EXTRACTION extends RegexCorrectionType

}

final case class RegexExerciseContent(
  maxPoints: Int,
  correctionType: RegexCorrectionType,
  sampleSolutions: Seq[StringSampleSolution],
  matchTestData: Seq[RegexMatchTestData],
  extractionTestData: Seq[RegexExtractionTestData] = Seq.empty
) extends StringExerciseContent


final case class RegexMatchTestData(id: Int, data: String, isIncluded: Boolean)

final case class RegexExtractionTestData(id: Int, base: String)
