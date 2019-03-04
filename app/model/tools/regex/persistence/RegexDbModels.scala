package model.tools.regex.persistence

import model.persistence._
import model.tools.regex._
import model.{Difficulty, ExerciseState, SemanticVersion, StringSampleSolution}

object RegexDbModels extends ADbModels[RegexExercise, DbRegexExercise] {

  override def dbExerciseFromExercise(collId: Int, ex: RegexExercise): DbRegexExercise =
    DbRegexExercise(ex.id, ex.semanticVersion, collId, ex.title, ex.author, ex.text, ex.state, ex.maxPoints)

  def exerciseFromDbExercise(ex: DbRegexExercise, sampleSolutions: Seq[StringSampleSolution], testData: Seq[RegexTestData]) =
    RegexExercise(ex.id, ex.semanticVersion, ex.title, ex.author, ex.text, ex.state, ex.maxPoints, sampleSolutions, testData)


  // Test data

  def dbTestDataFromTestData(exId: Int, exSemVer: SemanticVersion, collId: Int, testData: RegexTestData): DbRegexTestData =
    DbRegexTestData(testData.id, exId, exSemVer, collId, testData.data, testData.isIncluded)

  def testDataFromDbTestData(dbTestdata: DbRegexTestData): RegexTestData =
    RegexTestData(dbTestdata.id, dbTestdata.data, dbTestdata.isIncluded)

}

object RegexExerciseReviewDbModels extends AExerciseReviewDbModels[RegexExPart, RegexExerciseReview, DbRegexExerciseReview] {

  override def dbReviewFromReview(username: String, collId: Int, exId: Int, part: RegexExPart, review: RegexExerciseReview): DbRegexExerciseReview =
    DbRegexExerciseReview(username, collId, exId, part, review.difficulty, review.maybeDuration)

  override def reviewFromDbReview(dbReview: DbRegexExerciseReview): RegexExerciseReview =
    RegexExerciseReview(dbReview.difficulty, dbReview.maybeDuration)

}

final case class DbRegexExercise(id: Int, semanticVersion: SemanticVersion, collectionId: Int, title: String,
                                 author: String, text: String, state: ExerciseState, maxPoints: Int) extends ADbExercise


final case class DbRegexTestData(id: Int, exerciseId: Int, exSemVer: SemanticVersion, collId: Int, data: String, isIncluded: Boolean)

// Exercise review

final case class DbRegexExerciseReview(username: String, collId: Int, exerciseId: Int, exercisePart: RegexExPart, difficulty: Difficulty, maybeDuration: Option[Int])
  extends DbExerciseReview[RegexExPart]
