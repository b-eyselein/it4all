package model.toolMains

import model.{ExerciseState, User}
import model.core.overviewHelpers.SolvedState

import scala.concurrent.Future

trait CollectionToolMainDbQueries {
  self: CollectionToolMain =>

  // Numbers

  def futureHighestCollectionId: Future[Int] = tables.futureHighestCollectionId

  def futureNumOfExesInColl(id: Int): Future[Int] = tables.futureNumOfExesInColl(id)

  def futureHighestExerciseIdInCollection(collId: Int): Future[Int] = tables.futureHighestExerciseIdInCollection(collId)

  // Reading

  def futureCollById(id: Int): Future[Option[CollType]] = tables.futureCollById(id)

  def futureAllCollections: Future[Seq[CollType]] = tables.futureAllCollections

  def futureExerciseById(collId: Int, id: Int): Future[Option[ExType]] = tables.futureExerciseById(collId, id)

  def futureExercisesInColl(collId: Int): Future[Seq[ExType]] = tables.futureExercisesInColl(collId)

  def futureMaybeOldSolution(username: String, collId: Int, exId: Int, part: PartType): Future[Option[UserSolType]] =
    tables.futureMaybeOldSolution(username, collId, exId, part)

  def futureSampleSolutions(collId: Int, exId: Int, part: PartType): Future[Seq[String]] = tables.futureSampleSolutionsForExPart(collId, exId, part)

  def futureSolveStateForExercisePart(user: User, collId: Int, exId: Int, part: PartType): Future[Option[SolvedState]] =
    tables.futureSolveStateForExercisePart(user, collId, exId, part)

  // Saving

  def futureInsertExercise(collId: Int, exercise: ExType): Future[Boolean] = tables.futureInsertExercise(collId, exercise)

  def futureInsertAndDeleteOldCollection(collection: CollType): Future[Boolean] =
    tables.futureInsertAndDeleteOldCollection(collection)

  def futureSaveReview(username: String, collId: Int, exId: Int, part: PartType, review: ReviewType): Future[Boolean] =
    tables.futureSaveReview(username, collId, exId, part, review)

  // Update

  def updateExerciseState(collId: Int, exId: Int, newState: ExerciseState): Future[Boolean] = tables.updateExerciseState(collId, exId, newState)

  def updateCollectionState(collId: Int, newState: ExerciseState): Future[Boolean] = tables.updateCollectionState(collId, newState)

  // Deletion

  def futureDeleteCollection(collId: Int): Future[Boolean] = tables.futureDeleteCollection(collId)

  def futureDeleteExercise(collId: Int, exId: Int): Future[Boolean] = tables.futureDeleteExercise(collId, exId)


}
