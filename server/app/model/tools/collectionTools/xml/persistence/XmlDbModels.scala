package model.tools.collectionTools.xml.persistence

import model.persistence._
import model.points.Points
import model.tools.collectionTools.xml._
import model.{Difficulty, SemanticVersion}

object XmlDbModels extends ADbModels[XmlExercise, XmlExercise] {

  override def dbExerciseFromExercise(ex: XmlExercise): XmlExercise = ex

}

object XmlSolutionDbModels extends ASolutionDbModels[XmlSolution, XmlExPart, XmlSampleSolution, DbXmlSampleSolution, XmlUserSolution, DbXmlUserSolution] {

  override def dbSampleSolFromSampleSol(exId: Int, exSemVer: SemanticVersion, collId: Int, sample: XmlSampleSolution): DbXmlSampleSolution =
    DbXmlSampleSolution(sample.id, exId, exSemVer, collId, sample.sample.document, sample.sample.grammar)

  override def sampleSolFromDbSampleSol(dbSample: DbXmlSampleSolution): XmlSampleSolution =
    XmlSampleSolution(dbSample.id, dbSample.sample)

  override def dbUserSolFromUserSol(exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, solution: XmlUserSolution): DbXmlUserSolution =
    DbXmlUserSolution(solution.id, exId, exSemVer, collId, username, solution.part, solution.solution.document, solution.solution.grammar, solution.points, solution.maxPoints)

  override def userSolFromDbUserSol(dbSolution: DbXmlUserSolution): XmlUserSolution =
    XmlUserSolution(dbSolution.id, dbSolution.part, dbSolution.solution, dbSolution.points, dbSolution.maxPoints)

}

object XmlExerciseReviewDbModels extends AExerciseReviewDbModels[XmlExPart, XmlExerciseReview, DbXmlExerciseReview] {

  override def dbReviewFromReview(username: String, collId: Int, exId: Int, part: XmlExPart, review: XmlExerciseReview): DbXmlExerciseReview =
    DbXmlExerciseReview(username, collId, exId, part, review.difficulty, review.maybeDuration)

  override def reviewFromDbReview(dbReview: DbXmlExerciseReview): XmlExerciseReview =
    XmlExerciseReview(dbReview.difficulty, dbReview.maybeDuration)

}


final case class DbXmlSampleSolution(id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, document: String, grammar: String)
  extends ADbSampleSol {

  val sample: XmlSolution = XmlSolution(document, grammar)

}

final case class DbXmlUserSolution(id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, part: XmlExPart,
                                   document: String, grammar: String, points: Points, maxPoints: Points)
  extends ADbUserSol[XmlExPart] {

  val solution: XmlSolution = XmlSolution(document, grammar)

}

// Exercise review

final case class DbXmlExerciseReview(username: String, collId: Int, exerciseId: Int, exercisePart: XmlExPart,
                                     difficulty: Difficulty, maybeDuration: Option[Int]) extends DbExerciseReview[XmlExPart]
