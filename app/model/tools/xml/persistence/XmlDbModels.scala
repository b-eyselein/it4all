package model.tools.xml.persistence

import model.persistence.{ADbModels, ADbSampleSol, ADbUserSol}
import model.tools.xml._
import model.{ExerciseState, HasBaseValues, Points, SemanticVersion}

object XmlDbModels extends ADbModels[XmlExercise, DbXmlExercise, XmlSampleSolution, DbXmlSampleSolution, XmlUserSolution, DbXmlUserSolution] {

  override def dbExerciseFromExercise(collId: Int, ex: XmlExercise): DbXmlExercise =
    DbXmlExercise(ex.id, ex.semanticVersion, collId, ex.title, ex.author, ex.text, ex.state, ex.grammarDescription, ex.rootNode)

  def exerciseFromDbValues(dbXmlEx: DbXmlExercise, samples: Seq[XmlSampleSolution]): XmlExercise =
    XmlExercise(
      dbXmlEx.id, dbXmlEx.semanticVersion, dbXmlEx.title, dbXmlEx.author, dbXmlEx.text, dbXmlEx.state,
      dbXmlEx.grammarDescription, dbXmlEx.rootNode, samples
    )

  override def dbSampleSolFromSampleSol(exId: Int, exSemVer: SemanticVersion, collId: Int, sample: XmlSampleSolution): DbXmlSampleSolution =
    DbXmlSampleSolution(sample.id, exId, exSemVer, collId, sample.sample.document, sample.sample.grammar)

  override def sampleSolFromDbSampleSol(dbSample: DbXmlSampleSolution): XmlSampleSolution =
    XmlSampleSolution(dbSample.id, dbSample.sample)

  override def dbUserSolFromUserSol(exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, solution: XmlUserSolution): DbXmlUserSolution =
    DbXmlUserSolution(solution.id, exId, exSemVer, collId, username, solution.part, solution.solution.document, solution.solution.grammar, solution.points, solution.maxPoints)

  override def userSolFromDbUserSol(dbSolution: DbXmlUserSolution): XmlUserSolution =
    XmlUserSolution(dbSolution.id, dbSolution.part, dbSolution.solution, dbSolution.points, dbSolution.maxPoints)
}


final case class DbXmlExercise(id: Int, semanticVersion: SemanticVersion, collectionId: Int, title: String, author: String, text: String, state: ExerciseState,
                               grammarDescription: String, rootNode: String) extends HasBaseValues

final case class DbXmlSampleSolution(id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, document: String, grammar: String)
  extends ADbSampleSol[XmlSolution] {

  val sample = XmlSolution(document, grammar)

}

final case class DbXmlUserSolution(id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, part: XmlExPart,
                                   document: String, grammar: String, points: Points, maxPoints: Points)
  extends ADbUserSol[XmlSolution] {

  val solution = XmlSolution(document, grammar)

}
