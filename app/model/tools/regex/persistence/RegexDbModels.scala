package model.tools.regex.persistence

import model.persistence._
import model.tools.regex._
import model.{Difficulty, ExerciseState, HasBaseValues, Points, SemanticVersion}

object RegexDbModels extends ADbModels[RegexExercise, DbRegexExercise, RegexSampleSolution, DbRegexSampleSolution, RegexUserSolution, DbRegexUserSolution] {

  override def dbExerciseFromExercise(collId: Int, ex: RegexExercise): DbRegexExercise =
    DbRegexExercise(ex.id, ex.semanticVersion, collId, ex.title, ex.author, ex.text, ex.state)

  def exerciseFromDbExercise(ex: DbRegexExercise, sampleSolutions: Seq[RegexSampleSolution], testData: Seq[RegexTestData]) =
    RegexExercise(ex.id, ex.semanticVersion, ex.title, ex.author, ex.text, ex.state, sampleSolutions, testData)

  // Sample solutions

  override def dbSampleSolFromSampleSol(exId: Int, exSemVer: SemanticVersion, collId: Int, sampleSol: RegexSampleSolution): DbRegexSampleSolution =
    DbRegexSampleSolution(sampleSol.id, exId, exSemVer, collId, sampleSol.sample)

  override def sampleSolFromDbSampleSol(dbSampleSol: DbRegexSampleSolution): RegexSampleSolution =
    RegexSampleSolution(dbSampleSol.id, dbSampleSol.sample)

  // Test data

  def dbTestDataFromTestData(exId: Int, exSemVer: SemanticVersion, collId: Int, testData: RegexTestData): DbRegexTestData =
    DbRegexTestData(testData.id, exId, exSemVer, collId, testData.data, testData.isIncluded)

  def testDataFromDbTestData(dbTestdata: DbRegexTestData): RegexTestData =
    RegexTestData(dbTestdata.id, dbTestdata.data, dbTestdata.isIncluded)

  // User solutions

  override def dbUserSolFromUserSol(exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, solution: RegexUserSolution): DbRegexUserSolution =
    DbRegexUserSolution(solution.id, exId, exSemVer, collId, username, solution.part, solution.solution, solution.points, solution.maxPoints)

  override def userSolFromDbUserSol(dbSolution: DbRegexUserSolution): RegexUserSolution =
    RegexUserSolution(dbSolution.id, dbSolution.part, dbSolution.solution, dbSolution.points, dbSolution.maxPoints)

}

object RegexExerciseReviewDbModels extends AExerciseReviewDbModels[RegexExPart, RegexExerciseReview, DbRegexExerciseReview] {

  override def dbReviewFromReview(username: String, collId: Int, exId: Int, part: RegexExPart, review: RegexExerciseReview): DbRegexExerciseReview =
    DbRegexExerciseReview(username, collId, exId, part, review.difficulty, review.maybeDuration)

  override def reviewFromDbReview(dbReview: DbRegexExerciseReview): RegexExerciseReview =
    RegexExerciseReview(dbReview.difficulty, dbReview.maybeDuration)

}

final case class DbRegexExercise(id: Int, semanticVersion: SemanticVersion, collId: Int, title: String, author: String, text: String, state: ExerciseState) extends HasBaseValues

final case class DbRegexSampleSolution(id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, sample: String)
  extends ADbSampleSol[String]

final case class DbRegexUserSolution(id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, username: String,
                                     part: RegexExPart, solution: String, points: Points, maxPoints: Points)
  extends ADbUserSol[String]

final case class DbRegexTestData(id: Int, exerciseId: Int, exSemVer: SemanticVersion, collId: Int, data: String, isIncluded: Boolean)

// Exercise review

final case class DbRegexExerciseReview(username: String, collId: Int, exerciseId: Int, exercisePart: RegexExPart, difficulty: Difficulty, maybeDuration: Option[Int])
  extends DbExerciseReview[RegexExPart]
