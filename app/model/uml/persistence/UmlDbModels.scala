package model.uml.persistence

import model.{ExerciseState, HasBaseValues, SemanticVersion}
import model.persistence.DbModels
import model.uml.{UmlExercise, UmlSampleSolution}

object UmlDbModels extends DbModels {

  override type Exercise = UmlExercise

  override type DbExercise = DbUmlExercise

  override def dbExerciseFromExercise(ex: UmlExercise): DbUmlExercise =
    DbUmlExercise(ex.id, ex.semanticVersion, ex.title, ex.author, ex.text, ex.state, ex.markedText)

  def exerciseFromDbExercise(ex: DbUmlExercise, toIgnore: Seq[String], mappings: Map[String, String], samples: Seq[UmlSampleSolution]) =
    UmlExercise(ex.id, ex.semanticVersion, ex.title, ex.author, ex.text, ex.state, ex.markedText, toIgnore, mappings, samples)

}


final case class DbUmlExercise(id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String,
                               state: ExerciseState, markedText: String) extends HasBaseValues
