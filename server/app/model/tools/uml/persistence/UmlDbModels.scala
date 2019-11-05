package model.tools.uml.persistence

import model.persistence._
import model.points.Points
import model.tools.uml._
import model.{Difficulty, ExerciseState, SemanticVersion}

object UmlDbModels extends ADbModels[UmlExercise, DbUmlExercise] {

  override def dbExerciseFromExercise(ex: UmlExercise): DbUmlExercise =
    DbUmlExercise(ex.id, ex.collId, ex.semanticVersion, ex.title, ex.author, ex.text, ex.state, ex.markedText)

  def exerciseFromDbExercise(ex: DbUmlExercise, toIgnore: Seq[String], mappings: Map[String, String], samples: Seq[UmlSampleSolution]): UmlExercise =
    UmlExercise(ex.id, ex.collectionId, ex.semanticVersion, ex.title, ex.author, ex.text, ex.state, ex.markedText, toIgnore, mappings, samples)

}

object UmlSolutionDbModels extends ASolutionDbModels[UmlClassDiagram, UmlExPart, UmlSampleSolution, DbUmlSampleSolution, UmlUserSolution, DbUmlUserSolution] {

  override def dbSampleSolFromSampleSol(exId: Int, exSemVer: SemanticVersion, collectionId: Int, sample: UmlSampleSolution): DbUmlSampleSolution =
    DbUmlSampleSolution(sample.id, exId, exSemVer, collectionId, sample.sample)

  override def sampleSolFromDbSampleSol(dbSample: DbUmlSampleSolution): UmlSampleSolution =
    UmlSampleSolution(dbSample.id, dbSample.sample)

  override def dbUserSolFromUserSol(exId: Int, exSemVer: SemanticVersion, collectionId: Int, username: String, solution: UmlUserSolution): DbUmlUserSolution =
    DbUmlUserSolution(solution.id, exId, exSemVer, collectionId, username, solution.part, solution.solution, solution.points, solution.maxPoints)

  override def userSolFromDbUserSol(dbSolution: DbUmlUserSolution): UmlUserSolution =
    UmlUserSolution(dbSolution.id, dbSolution.part, dbSolution.solution, dbSolution.points, dbSolution.maxPoints)

}

object UmlExerciseReviewDbModels extends AExerciseReviewDbModels[UmlExPart, UmlExerciseReview, DbUmlExerciseReview] {

  override def dbReviewFromReview(username: String, collectionId: Int, exId: Int, part: UmlExPart, review: UmlExerciseReview): DbUmlExerciseReview =
    DbUmlExerciseReview(username, collectionId, exId, part, review.difficulty, review.maybeDuration)

  override def reviewFromDbReview(dbReview: DbUmlExerciseReview): UmlExerciseReview =
    UmlExerciseReview(dbReview.difficulty, dbReview.maybeDuration)

}

final case class DbUmlExercise(
  id: Int, collectionId: Int, semanticVersion: SemanticVersion, title: String, author: String, text: String, state: ExerciseState, markedText: String
) extends ADbExercise

final case class DbUmlSampleSolution(id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, sample: UmlClassDiagram)
  extends ADbSampleSol

final case class DbUmlUserSolution(id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, part: UmlExPart,
                                   solution: UmlClassDiagram, points: Points, maxPoints: Points)
  extends ADbUserSol[UmlExPart]


// Exercise review

final case class DbUmlExerciseReview(username: String, collId: Int, exerciseId: Int, exercisePart: UmlExPart,
                                     difficulty: Difficulty, maybeDuration: Option[Int]) extends DbExerciseReview[UmlExPart]
