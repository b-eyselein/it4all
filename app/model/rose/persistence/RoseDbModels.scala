package model.rose.persistence

import model.persistence.{ADbModels, ADbSampleSol, ADbUserSol}
import model.programming.{ProgDataType, ProgLanguage}
import model.rose._
import model.{ExerciseState, HasBaseValues, Points, SemanticVersion}

object RoseDbModels extends ADbModels[RoseExercise, DbRoseExercise, RoseSampleSolution, DbRoseSampleSolution, RoseUserSolution, DbRoseUserSolution] {

  override def dbExerciseFromExercise(collId: Int, ex: RoseExercise): DbRoseExercise =
    DbRoseExercise(ex.id, ex.semanticVersion, ex.title, ex.author, ex.text, ex.state, ex.fieldWidth, ex.fieldHeight, ex.isMultiplayer)

  def exerciseFromDbValues(ex: DbRoseExercise, inputTypes: Seq[RoseInputType], samples: Seq[RoseSampleSolution]): RoseExercise =
    RoseExercise(ex.id, ex.semanticVersion, ex.title, ex.author, ex.text, ex.state, ex.fieldWidth, ex.fieldHeight, ex.isMultiplayer, inputTypes, samples)

  override def dbSampleSolFromSampleSol(exId: Int, exSemVer: SemanticVersion, collId: Int, sample: RoseSampleSolution): DbRoseSampleSolution =
    DbRoseSampleSolution(sample.id, exId, exSemVer, collId, sample.language, sample.sample)

  override def sampleSolFromDbSampleSol(dbSample: DbRoseSampleSolution): RoseSampleSolution =
    RoseSampleSolution(dbSample.id, dbSample.language, dbSample.sample)

  override def dbUserSolFromUserSol(exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, solution: RoseUserSolution): DbRoseUserSolution =
    DbRoseUserSolution(solution.id, exId, exSemVer, collId, username, solution.part, solution.language, solution.solution, solution.points, solution.maxPoints)

  override def userSolFromDbUserSol(dbSolution: DbRoseUserSolution): RoseUserSolution =
    RoseUserSolution(dbSolution.id, dbSolution.part, dbSolution.language, dbSolution.solution, dbSolution.points, dbSolution.maxPoints)


  def dbInputTypeFromInputType(exId: Int, exSemVer: SemanticVersion, collId: Int, inputType: RoseInputType): DbRoseInputType =
    DbRoseInputType(inputType.id, exId, exSemVer, collId, inputType.name, inputType.inputType)

  def inputTypeFromDbInputType(dbInputType: DbRoseInputType): RoseInputType =
    RoseInputType(dbInputType.id, dbInputType.name, dbInputType.inputType)

}

final case class DbRoseExercise(id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
                                fieldWidth: Int, fieldHeight: Int, isMultiplayer: Boolean) extends HasBaseValues

final case class DbRoseSampleSolution(id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, language: ProgLanguage, sample: String)
  extends ADbSampleSol[String]

final case class DbRoseUserSolution(id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, part: RoseExPart,
                                    language: ProgLanguage, solution: String, points: Points, maxPoints: Points)
  extends ADbUserSol[String]

final case class DbRoseInputType(id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, name: String, inputType: ProgDataType)
