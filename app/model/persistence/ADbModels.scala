package model.persistence

import model.points.Points
import model._

trait ADbModels[ExType, DbExercise] {

  def dbExerciseFromExercise(collId: Int, ex: ExType): DbExercise

  def dbExerciseFileFromExerciseFile(exId: Int, exSemVer: SemanticVersion, collId: Int, webFile: ExerciseFile): DbExerciseFile =
    DbExerciseFile(webFile.name, exId, exSemVer, collId, webFile.content, webFile.fileType, webFile.editable)

  def exerciseFileFromDbExerciseFile(dbExerciseFile: DbExerciseFile): ExerciseFile = dbExerciseFile match {
    case DbExerciseFile(path, _, _, _, resourcePath, fileType, editable) => ExerciseFile(path, resourcePath, fileType, editable)
  }

}

final case class DbExerciseFile(path: String, exId: Int, exSemVer: SemanticVersion, collId: Int, resourcePath: String, fileType: String, editable: Boolean)

trait ASolutionDbModels[SolType, PartType <: ExPart, SampleSolType <: SampleSolution[SolType], DbSampleSolType, UserSolType <: UserSolution[PartType, SolType], DbUserSolType] {

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

sealed trait ADbSolution {

  val id: Int

  val exId: Int

  val exSemVer: SemanticVersion

  val collId: Int

}

trait ADbSampleSol extends ADbSolution

trait ADbUserSol[PartType <: ExPart] extends ADbSolution {

  val username: String

  val part: PartType

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
