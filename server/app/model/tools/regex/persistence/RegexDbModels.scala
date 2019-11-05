package model.tools.regex.persistence

import model.persistence._
import model.tools.regex._
import model.{Difficulty, ExerciseState, SemanticVersion, StringSampleSolution}

object RegexDbModels extends ADbModels[RegexExercise, DbRegexExercise] {

  override def dbExerciseFromExercise(ex: RegexExercise): DbRegexExercise = ex match {
    case RegexExercise(id, collId, semanticVersion, title, author, text, state, maxPoints, correctionType, _, _, _) =>
      DbRegexExercise(id, collId, semanticVersion, title, author, text, state, maxPoints, correctionType)
  }

  def exerciseFromDbExercise(
    ex: DbRegexExercise,
    sampleSolutions: Seq[StringSampleSolution],
    matchTestData: Seq[RegexMatchTestData],
    dbExtractionTestData: Seq[DbRegexExtractionTestData]
  ): RegexExercise = ex match {
    case DbRegexExercise(id, collectionId, semanticVersion, title, author, text, state, maxPoints, correctionType) =>

      val extractionTestData = dbExtractionTestData.map { x => RegexExtractionTestData(x.id, x.base) }

      RegexExercise(id, collectionId, semanticVersion, title, author, text, state, maxPoints, correctionType, sampleSolutions, matchTestData, extractionTestData)
  }

  // Match Test data

  def dbMatchTestDataFromMatchTestData(exId: Int, exSemVer: SemanticVersion, collId: Int, testData: RegexMatchTestData): DbRegexMatchTestData =
    DbRegexMatchTestData(testData.id, exId, exSemVer, collId, testData.data, testData.isIncluded)

  def matchTestDataFromDbMatchTestData(dbTestdata: DbRegexMatchTestData): RegexMatchTestData =
    RegexMatchTestData(dbTestdata.id, dbTestdata.data, dbTestdata.isIncluded)

}

object RegexExerciseReviewDbModels extends AExerciseReviewDbModels[RegexExPart, RegexExerciseReview, DbRegexExerciseReview] {

  override def dbReviewFromReview(username: String, collId: Int, exId: Int, part: RegexExPart, review: RegexExerciseReview): DbRegexExerciseReview =
    DbRegexExerciseReview(username, collId, exId, part, review.difficulty, review.maybeDuration)

  override def reviewFromDbReview(dbReview: DbRegexExerciseReview): RegexExerciseReview =
    RegexExerciseReview(dbReview.difficulty, dbReview.maybeDuration)

}

final case class DbRegexExercise(
  id: Int, collectionId: Int, semanticVersion: SemanticVersion, title: String,
  author: String, text: String, state: ExerciseState, maxPoints: Int, correctionType: RegexCorrectionType
) extends ADbExercise


final case class DbRegexMatchTestData(id: Int, exerciseId: Int, exSemVer: SemanticVersion, collId: Int, data: String, isIncluded: Boolean)


final case class DbRegexExtractionTestData(id: Int, exerciseId: Int, exSemVer: SemanticVersion, collId: Int, base: String)

final case class DbRegexExtractionTestDataToExtract(id: Int, testDataId: Int, exerciseId: Int, exSemVer: SemanticVersion, collId: Int, toExtract: String)


// Exercise review

final case class DbRegexExerciseReview(
  username: String, collId: Int, exerciseId: Int, exercisePart: RegexExPart, difficulty: Difficulty, maybeDuration: Option[Int]
) extends DbExerciseReview[RegexExPart]
