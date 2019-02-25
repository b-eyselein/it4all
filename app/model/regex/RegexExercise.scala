package model.regex

import model._
import play.twirl.api.Html

import scala.language.postfixOps

final case class RegexSampleSolution(id: Int, exerciseId: Int, exSemVer: SemanticVersion, sample: String) extends SampleSolution[String]

final case class RegexTestData(id: Int, exerciseId: Int, exSemVer: SemanticVersion, data: String, isIncluded: Boolean)

final case class RegexExercise(id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
                               sampleSolutions: Seq[RegexSampleSolution], testData: Seq[RegexTestData]) extends SingleExercise {

  override def baseValues: BaseValues = BaseValues(id, semanticVersion, title, author, text, state)

  // other methods

  override def preview: Html = Html(toString) // FIXME: implement!

}

// Solution

final case class RegexDBSolution(id: Int, username: String, exerciseId: Int, exSemVer: SemanticVersion,
                                 part: RegexExPart, solution: String, points: Points, maxPoints: Points)
  extends DBPartSolution[RegexExPart, String]

// Review

final case class RegexExerciseReview(username: String, exerciseId: Int, exerciseSemVer: SemanticVersion, exercisePart: RegexExPart,
                                     difficulty: Difficulty, maybeDuration: Option[Int]) extends ExerciseReview[RegexExPart]
