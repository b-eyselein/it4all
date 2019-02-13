package model.rose.persistence

import model.persistence.DbModels
import model.rose.{RoseExercise, RoseInputType, RoseSampleSolution}
import model.{ExerciseState, HasBaseValues, SemanticVersion}

object RoseDbModels extends DbModels {
  def exerciseFromDbValues(ex: DbRoseExercise, inputTypes: Seq[RoseInputType], samples: Seq[RoseSampleSolution]): RoseExercise =
    RoseExercise(ex.id, ex.semanticVersion, ex.title, ex.author, ex.text, ex.state, ex.fieldWidth, ex.fieldHeight, ex.isMultiplayer,
      inputTypes, samples)


  override type Exercise = RoseExercise

  override type DbExercise = DbRoseExercise

  override def dbExerciseFromExercise(ex: RoseExercise): DbRoseExercise =
    DbRoseExercise(ex.id, ex.semanticVersion, ex.title, ex.author, ex.text, ex.state, ex.fieldWidth, ex.fieldHeight, ex.isMultiplayer)
}

final case class DbRoseExercise(id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
                                fieldWidth: Int, fieldHeight: Int, isMultiplayer: Boolean) extends HasBaseValues

