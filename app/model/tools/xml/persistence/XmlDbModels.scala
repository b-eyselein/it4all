package model.tools.xml.persistence

import model.persistence._
import model.tools.xml._
import model.{Difficulty, ExerciseState, HasBaseValues, Points, SemanticVersion}

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

object XmlExerciseReviewDbModels extends AExerciseReviewDbModels[XmlExPart, XmlExerciseReview, DbXmlExerciseReview] {

  override def dbReviewFromReview(username: String, collId: Int, exId: Int, part: XmlExPart, review: XmlExerciseReview): DbXmlExerciseReview =
    DbXmlExerciseReview(username, collId, exId, part, review.difficulty, review.maybeDuration)

  override def reviewFromDbReview(dbReview: DbXmlExerciseReview): XmlExerciseReview =
    XmlExerciseReview(dbReview.difficulty, dbReview.maybeDuration)

}

final case class DbXmlExercise(id: Int, semanticVersion: SemanticVersion, collectionId: Int, title: String, author: String, text: String, state: ExerciseState,
                               grammarDescription: String, rootNode: String) extends ADbExercise

final case class DbXmlSampleSolution(id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, document: String, grammar: String)
  extends ADbSampleSol[XmlSolution] {

  val sample = XmlSolution(document, grammar)

}

final case class DbXmlUserSolution(id: Int, exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, part: XmlExPart,
                                   document: String, grammar: String, points: Points, maxPoints: Points)
  extends ADbUserSol[XmlSolution] {

  val solution = XmlSolution(document, grammar)

}

// Exercise review

final case class DbXmlExerciseReview(username: String, collId: Int, exerciseId: Int, exercisePart: XmlExPart,
                                     difficulty: Difficulty, maybeDuration: Option[Int]) extends DbExerciseReview[XmlExPart]
