package model.tools.uml.persistence

import model.persistence._
import model.tools.uml._
import model.{Difficulty, ExerciseState, HasBaseValues, Points, SemanticVersion}

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

object UmlExerciseReviewDbModels extends AExerciseReviewDbModels[UmlExPart, UmlExerciseReview, DbUmlExerciseReview] {

  override def dbReviewFromReview(username: String, collId: Int, exId: Int, part: UmlExPart, review: UmlExerciseReview): DbUmlExerciseReview =
    DbUmlExerciseReview(username, collId, exId, part, review.difficulty, review.maybeDuration)

  override def reviewFromDbReview(dbReview: DbUmlExerciseReview): UmlExerciseReview =
    UmlExerciseReview(dbReview.difficulty, dbReview.maybeDuration)

}


final case class DbUmlExercise(id: Int, semanticVersion: SemanticVersion, /*collId: Int,*/ title: String, author: String, text: String,
                               state: ExerciseState, markedText: String) extends HasBaseValues

final case class DbUmlSampleSolution(id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, sample: UmlClassDiagram)
  extends ADbSampleSol[UmlClassDiagram]

final case class DbUmlUserSolution(id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, part: UmlExPart,
                                   solution: UmlClassDiagram, points: Points, maxPoints: Points)
  extends ADbUserSol[UmlClassDiagram]


// Exercise review

final case class DbUmlExerciseReview(username: String, collId: Int, exerciseId: Int, exercisePart: UmlExPart,
                                     difficulty: Difficulty, maybeDuration: Option[Int]) extends DbExerciseReview[UmlExPart]
