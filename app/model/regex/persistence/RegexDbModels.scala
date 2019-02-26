package model.regex.persistence

import model.persistence.DbModels
import model.regex.{RegexExercise, RegexSampleSolution, RegexTestData}
import model.{ExerciseState, HasBaseValues, SemanticVersion}

object RegexDbModels extends DbModels {

  override type Exercise = RegexExercise

  override type DbExercise = DbRegexExercise

  override def dbExerciseFromExercise(ex: RegexExercise): DbRegexExercise =
    DbRegexExercise(ex.id, ex.semanticVersion, ex.collectionId, ex.title, ex.author, ex.text, ex.state)

  def exerciseFromDbExercise(ex: DbRegexExercise, sampleSolutions: Seq[RegexSampleSolution], testData: Seq[RegexTestData]) =
    RegexExercise(ex.id, ex.semanticVersion, ex.collId, ex.title, ex.author, ex.text, ex.state, sampleSolutions, testData)

}

final case class DbRegexExercise(id: Int, semanticVersion: SemanticVersion, collId: Int, title: String, author: String, text: String, state: ExerciseState) extends HasBaseValues
