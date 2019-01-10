package model.regex

import model._
import model.core.result.{CompleteResult, EvaluationResult, SuccessType}
import play.api.libs.json.{JsValue, Json}
import play.twirl.api.Html

import scala.language.postfixOps

final case class RegexExercise(id: Int, title: String, author: String, text: String, state: ExerciseState,
                               semanticVersion: SemanticVersion) extends Exercise

final case class RegexSampleSolution(id: Int, exerciseId: Int, exSemVer: SemanticVersion, sample: String) extends SampleSolution[String]

final case class RegexTestData(id: Int, exerciseId: Int, exSemVer: SemanticVersion, data: String, isIncluded: Boolean)

final case class RegexCompleteEx(ex: RegexExercise, sampleSolutions: Seq[RegexSampleSolution], testData: Seq[RegexTestData]) extends SingleCompleteEx[RegexExercise, RegexExPart] {

  override def hasPart(partType: RegexExPart): Boolean = true

  override def preview: Html = Html(toString) // FIXME: implement!

}

// Solution

final case class RegexDBSolution(id: Int, username: String, exerciseId: Int, exSemVer: SemanticVersion,
                                 part: RegexExPart, solution: String, points: Points, maxPoints: Points)
  extends DBPartSolution[RegexExPart, String]


// Results

final case class RegexEvaluationResult(testData: RegexTestData, resultType: BinaryClassificationResultType) extends EvaluationResult {

  override def success: SuccessType = ???

  def toJson: JsValue = Json.obj(
    "testData" -> testData.data,
    "included" -> testData.isIncluded,
    "resultType" -> resultType.entryName
  )

}


final case class RegexCompleteResult(learnerSolution: String, exercise: RegexCompleteEx, part: RegexExPart,
                                     results: Seq[RegexEvaluationResult]) extends CompleteResult[RegexEvaluationResult] {

  override type SolType = String

  override def toJson(saved: Boolean): JsValue = Json.obj(
    "solution" -> learnerSolution,
    "results" -> results.map(_.toJson)
  )

}

// Review

final case class RegexExerciseReview(username: String, exerciseId: Int, exerciseSemVer: SemanticVersion, exercisePart: RegexExPart,
                                     difficulty: Difficulty, maybeDuration: Option[Int]) extends ExerciseReview[RegexExPart]