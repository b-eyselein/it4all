package model.tools.regex

import model._
import model.points.Points
import play.twirl.api.Html

import scala.language.postfixOps


final case class RegexCollection(id: Int, title: String, author: String, text: String, state: ExerciseState, shortName: String)
  extends ExerciseCollection

final case class RegexExercise(id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
                               maxPoints: Int, sampleSolutions: Seq[RegexSampleSolution], testData: Seq[RegexTestData])
  extends Exercise {

  override def baseValues: BaseValues = BaseValues(id, semanticVersion, title, author, text, state)

  // other methods

  override def preview: Html = Html(toString) // FIXME: implement!

}

// Solution

final case class RegexTestData(id: Int, data: String, isIncluded: Boolean)

final case class RegexSampleSolution(id: Int, sample: String)
  extends SampleSolution[String]

final case class RegexUserSolution(id: Int, part: RegexExPart, solution: String, points: Points, maxPoints: Points)
  extends UserSolution[RegexExPart, String]

// Review

final case class RegexExerciseReview(difficulty: Difficulty, maybeDuration: Option[Int]) extends ExerciseReview
