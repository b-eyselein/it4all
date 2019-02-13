package model.xml.persistence

import model.persistence.DbModels
import model.xml.{XmlExercise, XmlSample}
import model.{ExerciseState, HasBaseValues, SemanticVersion}

object XmlDbModels extends DbModels {

  override type Exercise = XmlExercise

  override type DbExercise = DbXmlExercise

  override def dbExerciseFromExercise(ex: XmlExercise): DbXmlExercise =
    DbXmlExercise(ex.id, ex.semanticVersion, ex.title, ex.author, ex.text, ex.state, ex.grammarDescription, ex.rootNode)

  def exerciseFromDbValues(dbXmlEx: DbXmlExercise, samples: Seq[XmlSample]): XmlExercise =
    XmlExercise(
      dbXmlEx.id, dbXmlEx.semanticVersion, dbXmlEx.title, dbXmlEx.author, dbXmlEx.text, dbXmlEx.state,
      dbXmlEx.grammarDescription, dbXmlEx.rootNode, samples
    )

}


final case class DbXmlExercise(id: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState,
                               grammarDescription: String, rootNode: String) extends HasBaseValues
