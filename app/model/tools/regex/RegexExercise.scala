package model.tools.regex

import enumeratum.{EnumEntry, PlayEnum}
import model._
import play.twirl.api.Html

import scala.collection.immutable.IndexedSeq


sealed trait RegexCorrectionType extends EnumEntry

case object RegexCorrectionTypes extends PlayEnum[RegexCorrectionType] {

  override val values: IndexedSeq[RegexCorrectionType] = findValues


  case object MATCHING extends RegexCorrectionType

  case object EXTRACTION extends RegexCorrectionType

}


final case class RegexCollection(id: Int, title: String, author: String, text: String, state: ExerciseState, shortName: String)
  extends ExerciseCollection


final case class RegexExercise(
  id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState, maxPoints: Int,
  correctionType: RegexCorrectionType,
  sampleSolutions: Seq[StringSampleSolution],
  matchTestData: Seq[RegexMatchTestData],
  extractionTestData: Seq[RegexExtractionTestData] = Seq.empty
) extends Exercise {

  override def preview: Html = Html(toString) // FIXME: implement!

}


final case class RegexMatchTestData(id: Int, data: String, isIncluded: Boolean)

final case class RegexExtractionTestData(id: Int, base: String)


final case class RegexExerciseReview(difficulty: Difficulty, maybeDuration: Option[Int]) extends ExerciseReview
