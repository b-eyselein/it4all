package model.uml.persistence

import model.persistence.{ADbSampleSol, ADbUserSol, ADbModels}
import model.uml._
import model.{ExerciseState, HasBaseValues, Points, SemanticVersion}

object UmlDbModels extends ADbModels[UmlExercise, DbUmlExercise, UmlSampleSolution, DbUmlSampleSolution, UmlUserSolution, DbUmlUserSolution] {

  override def dbExerciseFromExercise(collId: Int, ex: UmlExercise): DbUmlExercise =
    DbUmlExercise(ex.id, ex.semanticVersion, /*collId, */ ex.title, ex.author, ex.text, ex.state, ex.markedText)

  def exerciseFromDbExercise(ex: DbUmlExercise, toIgnore: Seq[String], mappings: Map[String, String], samples: Seq[UmlSampleSolution]) =
    UmlExercise(ex.id, ex.semanticVersion, ex.title, ex.author, ex.text, ex.state, ex.markedText, toIgnore, mappings, samples)

  override def dbSampleSolFromSampleSol(exId: Int, exSemVer: SemanticVersion, collId: Int, sample: UmlSampleSolution): DbUmlSampleSolution =
    DbUmlSampleSolution(sample.id, exId, exSemVer, collId, sample.sample)

  override def sampleSolFromDbSampleSol(dbSample: DbUmlSampleSolution): UmlSampleSolution =
    UmlSampleSolution(dbSample.id, dbSample.sample)

  override def dbUserSolFromUserSol(exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, solution: UmlUserSolution): DbUmlUserSolution =
    DbUmlUserSolution(solution.id, exId, exSemVer, collId, username, solution.part, solution.solution, solution.points, solution.maxPoints)

  override def userSolFromDbUserSol(dbSolution: DbUmlUserSolution): UmlUserSolution =
    UmlUserSolution(dbSolution.id, dbSolution.part, dbSolution.solution, dbSolution.points, dbSolution.maxPoints)

}


final case class DbUmlExercise(id: Int, semanticVersion: SemanticVersion, /*collId: Int,*/ title: String, author: String, text: String,
                               state: ExerciseState, markedText: String) extends HasBaseValues

final case class DbUmlSampleSolution(id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, sample: UmlClassDiagram)
  extends ADbSampleSol[UmlClassDiagram]

final case class DbUmlUserSolution(id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, part: UmlExPart,
                                   solution: UmlClassDiagram, points: Points, maxPoints: Points)
  extends ADbUserSol[UmlClassDiagram]
