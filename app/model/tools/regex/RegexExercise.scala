package model.tools.regex

import enumeratum.{EnumEntry, PlayEnum}
import model.tools._

sealed trait RegexCorrectionType extends EnumEntry

case object RegexCorrectionTypes extends PlayEnum[RegexCorrectionType] {

  override val values: IndexedSeq[RegexCorrectionType] = findValues

  case object MATCHING extends RegexCorrectionType

  case object EXTRACTION extends RegexCorrectionType

}

sealed trait RegexExTag extends EnumEntry

case object RegexExTag extends PlayEnum[RegexExTag] {

  override val values: IndexedSeq[RegexExTag] = findValues

  case object RegexExTagTodo extends RegexExTag

}

final case class RegexExercise(
  id: Int,
  collectionId: Int,
  toolId: String,
  semanticVersion: SemanticVersion,
  title: String,
  authors: Seq[String],
  text: String,
  tags: Seq[RegexExTag],
  difficulty: Option[Int],
  sampleSolutions: Seq[SampleSolution[String]],
  maxPoints: Int,
  correctionType: RegexCorrectionType,
  matchTestData: Seq[RegexMatchTestData],
  extractionTestData: Seq[RegexExtractionTestData] = Seq.empty
) extends Exercise {

  override type ET      = RegexExTag
  override type SolType = String

}

final case class RegexExerciseContent(
  maxPoints: Int,
  correctionType: RegexCorrectionType,
  sampleSolutions: Seq[SampleSolution[String]],
  matchTestData: Seq[RegexMatchTestData],
  extractionTestData: Seq[RegexExtractionTestData] = Seq.empty
) extends StringExerciseContent

final case class RegexMatchTestData(id: Int, data: String, isIncluded: Boolean)

final case class RegexExtractionTestData(id: Int, base: String)
