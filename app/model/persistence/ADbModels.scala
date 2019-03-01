package model.persistence

import model.{Difficulty, ExPart, ExerciseReview, HasBaseValues, Points, SemanticVersion}

trait ADbModels[Exercise, DbExercise, SampleSolType, DbSampleSolType, UserSolType, DbUserSolType] {

  def dbExerciseFromExercise(collId: Int, ex: Exercise): DbExercise


  def dbSampleSolFromSampleSol(exId: Int, exSemVer: SemanticVersion, collId: Int, sample: SampleSolType): DbSampleSolType

  def sampleSolFromDbSampleSol(dbSample: DbSampleSolType): SampleSolType


  def dbUserSolFromUserSol(exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, solution: UserSolType): DbUserSolType

  def userSolFromDbUserSol(dbSolution: DbUserSolType): UserSolType


}

trait ADbExercise extends HasBaseValues {

  def collectionId: Int

}

trait AExerciseReviewDbModels[PartType <: ExPart, ReviewType <: ExerciseReview, DbReviewType <: DbExerciseReview[PartType]] {

  def dbReviewFromReview(username: String, collId: Int, exId: Int, part: PartType, review: ReviewType): DbReviewType

  def reviewFromDbReview(dbReview: DbReviewType): ReviewType

}

sealed trait ADbSolution[SolType] {

  val id: Int

  val exId: Int

  val exSemVer: SemanticVersion

  val collId: Int

}

trait ADbSampleSol[SolType] extends ADbSolution[SolType] {

  val sample: SolType

}

trait ADbUserSol[SolType] extends ADbSolution[SolType] {

  val username: String

  val solution: SolType

  val points: Points

  val maxPoints: Points

}

trait DbExerciseReview[PartType <: ExPart] {

  val username: String

  val exerciseId: Int

  val collId: Int

  val exercisePart: PartType

  val difficulty: Difficulty

  val maybeDuration: Option[Int]


}
