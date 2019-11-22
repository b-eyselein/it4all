package model.persistence

import model._
import model.core.LongText
import model.points.Points
import model.tools.collectionTools.ExPart
import play.api.libs.json.JsValue

final case class DbExercise(
  id: Int, collectionId: Int, toolId: String, semanticVersion: SemanticVersion,
  title: String, author: String, text: LongText, state: ExerciseState, content: JsValue
)

final case class DbUserSolution(
  id: Int, exerciseId: Int, collectionId: Int, toolId: String, semanticVersion: SemanticVersion, part: ExPart,
  username: String, solution: JsValue
)

final case class DbExerciseReview(
  exerciseId: Int,
  collectionId: Int,
  toolId: String,
  semanticVersion: SemanticVersion,
  part: ExPart,
  username: String,
  difficulty: Difficulty,
  maybeDuration: Option[Int]
)


final case class DbExerciseFile(path: String, exId: Int, collId: Int, resourcePath: String, fileType: String, editable: Boolean)

trait ASolutionDbModels[SolType, PartType <: ExPart, SampleSolType <: SampleSolution[SolType], DbSampleSolType, UserSolType <: UserSolution[PartType, SolType], DbUserSolType] {

  def dbSampleSolFromSampleSol(exId: Int, exSemVer: SemanticVersion, collId: Int, sample: SampleSolType): DbSampleSolType

  def sampleSolFromDbSampleSol(dbSample: DbSampleSolType): SampleSolType


  def dbUserSolFromUserSol(exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, solution: UserSolType): DbUserSolType

  def userSolFromDbUserSol(dbSolution: DbUserSolType): UserSolType


}


object AExerciseReviewDbModels {

  def dbReviewFromReview(username: String, collId: Int, exId: Int, part: ExPart, review: ExerciseReview): DbExerciseReview = ???

  def reviewFromDbReview(dbReview: DbExerciseReview): ExerciseReview = ???

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
