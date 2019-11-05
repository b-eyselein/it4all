package model.tools.rose.persistence

import model.persistence._
import model.points.Points
import model.tools.programming.{ProgDataType, ProgLanguage}
import model.tools.rose._
import model.{Difficulty, ExerciseState, SemanticVersion}

object RoseDbModels extends ADbModels[RoseExercise, DbRoseExercise] {

  override def dbExerciseFromExercise(ex: RoseExercise): DbRoseExercise =
    DbRoseExercise(ex.id, ex.collId, ex.semanticVersion, ex.title, ex.author, ex.text, ex.state, ex.fieldWidth, ex.fieldHeight, ex.isMultiplayer)

  def exerciseFromDbValues(ex: DbRoseExercise, inputTypes: Seq[RoseInputType], samples: Seq[RoseSampleSolution]): RoseExercise =
    RoseExercise(ex.id, ex.collectionId, ex.semanticVersion, ex.title, ex.author, ex.text, ex.state, ex.fieldWidth, ex.fieldHeight, ex.isMultiplayer, inputTypes, samples)

  def dbInputTypeFromInputType(exId: Int, exSemVer: SemanticVersion, collId: Int, inputType: RoseInputType): DbRoseInputType =
    DbRoseInputType(inputType.id, exId, exSemVer, collId, inputType.name, inputType.inputType)

  def inputTypeFromDbInputType(dbInputType: DbRoseInputType): RoseInputType =
    RoseInputType(dbInputType.id, dbInputType.name, dbInputType.inputType)

}

object RoseSolutionDbModels extends ASolutionDbModels[String, RoseExPart, RoseSampleSolution, DbRoseSampleSolution, RoseUserSolution, DbRoseUserSolution] {

  override def dbSampleSolFromSampleSol(exId: Int, exSemVer: SemanticVersion, collId: Int, sample: RoseSampleSolution): DbRoseSampleSolution =
    DbRoseSampleSolution(sample.id, exId, exSemVer, collId, sample.language, sample.sample)

  override def sampleSolFromDbSampleSol(dbSample: DbRoseSampleSolution): RoseSampleSolution =
    RoseSampleSolution(dbSample.id, dbSample.language, dbSample.sample)

  override def dbUserSolFromUserSol(exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, solution: RoseUserSolution): DbRoseUserSolution =
    DbRoseUserSolution(solution.id, exId, exSemVer, collId, username, solution.part, solution.language, solution.solution, solution.points, solution.maxPoints)

  override def userSolFromDbUserSol(dbSolution: DbRoseUserSolution): RoseUserSolution =
    RoseUserSolution(dbSolution.id, dbSolution.part, dbSolution.language, dbSolution.solution, dbSolution.points, dbSolution.maxPoints)

}

object RoseExerciseReviewDbModels extends AExerciseReviewDbModels[RoseExPart, RoseExerciseReview, DbRoseExerciseReview] {

  override def dbReviewFromReview(username: String, collId: Int, exId: Int, part: RoseExPart, review: RoseExerciseReview): DbRoseExerciseReview =
    DbRoseExerciseReview(username, collId, exId, part, review.difficulty, review.maybeDuration)

  override def reviewFromDbReview(dbReview: DbRoseExerciseReview): RoseExerciseReview =
    RoseExerciseReview(dbReview.difficulty, dbReview.maybeDuration)

}

final case class DbRoseExercise(id: Int, collectionId: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
                                fieldWidth: Int, fieldHeight: Int, isMultiplayer: Boolean) extends ADbExercise

final case class DbRoseSampleSolution(id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, language: ProgLanguage, sample: String)
  extends ADbSampleSol

final case class DbRoseUserSolution(id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, part: RoseExPart,
                                    language: ProgLanguage, solution: String, points: Points, maxPoints: Points)
  extends ADbUserSol[RoseExPart]

final case class DbRoseInputType(id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, name: String, inputType: ProgDataType)

// Exercise review

final case class DbRoseExerciseReview(username: String, collId: Int, exerciseId: Int, exercisePart: RoseExPart, difficulty: Difficulty, maybeDuration: Option[Int])
  extends DbExerciseReview[RoseExPart]
