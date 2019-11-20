package model.tools.collectionTools.regex

import enumeratum.{EnumEntry, PlayEnum}
import model._
import model.persistence.ADbExercise

import scala.collection.immutable.IndexedSeq


sealed trait RegexCorrectionType extends EnumEntry

case object RegexCorrectionTypes extends PlayEnum[RegexCorrectionType] {

  override val values: IndexedSeq[RegexCorrectionType] = findValues


  case object MATCHING extends RegexCorrectionType

  case object EXTRACTION extends RegexCorrectionType

}

final case class RegexExercise(
  id: Int, collectionId: Int, toolId: String = RegexConsts.toolId, semanticVersion: SemanticVersion,
  title: String, author: String, text: LongText, state: ExerciseState,
  maxPoints: Int,
  correctionType: RegexCorrectionType,
  sampleSolutions: Seq[StringSampleSolution],
  matchTestData: Seq[RegexMatchTestData],
  extractionTestData: Seq[RegexExtractionTestData] = Seq.empty
) extends Exercise with ADbExercise {

  override protected type SolutionType = String
  override protected type SampleSolutionType = StringSampleSolution

}


final case class RegexMatchTestData(id: Int, data: String, isIncluded: Boolean)

final case class RegexExtractionTestData(id: Int, base: String)


final case class RegexExerciseReview(difficulty: Difficulty, maybeDuration: Option[Int]) extends ExerciseReview
