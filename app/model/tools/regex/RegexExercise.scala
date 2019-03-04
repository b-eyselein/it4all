package model.tools.regex

import model._
import model.points.Points
import play.twirl.api.Html

import scala.language.postfixOps


final case class RegexCollection(id: Int, title: String, author: String, text: String, state: ExerciseState, shortName: String)
  extends ExerciseCollection


final case class RegexExercise(id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
                               maxPoints: Int, sampleSolutions: Seq[StringSampleSolution], testData: Seq[RegexTestData])
  extends Exercise {

  override def baseValues: BaseValues = BaseValues(id, semanticVersion, title, author, text, state)

  // other methods

  override def preview: Html = Html(toString) // FIXME: implement!

}


final case class RegexTestData(id: Int, data: String, isIncluded: Boolean)


final case class RegexExerciseReview(difficulty: Difficulty, maybeDuration: Option[Int]) extends ExerciseReview
