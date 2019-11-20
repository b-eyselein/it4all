package model.tools.collectionTools

import model.ExerciseState

import scala.concurrent.Future

trait CollectionToolMainDbQueries {
  self: CollectionToolMain =>

  // Numbers

  def futureHighestCollectionId: Future[Int] = tables.futureHighestCollectionId

  def futureNumOfExesInColl(id: Int): Future[Int] = tables.futureNumOfExesInColl(id)

  def futureHighestExerciseIdInCollection(collId: Int): Future[Int] = tables.futureHighestExerciseIdInCollection(collId)

  // Reading

  def futureCollById(id: Int): Future[Option[ExerciseCollection]] = tables.futureCollById(id)

  def futureAllCollections: Future[Seq[ExerciseCollection]] = tables.futureAllCollections


  def futureExerciseBasicsInColl(toolId: String, collId: Int): Future[Seq[ApiExerciseBasics]] =
    tables.futureExerciseBasicsInColl(toolId, collId)


  def futureExerciseById(collId: Int, id: Int): Future[Option[ExType]] = tables.futureExerciseById(collId, id)

  def futureExercisesInColl(collId: Int): Future[Seq[ExType]] = tables.futureExercisesInColl(collId)


  def futureMaybeOldSolution(username: String, collId: Int, exId: Int, part: PartType): Future[Option[UserSolType]] =
    tables.futureMaybeOldSolution(username, collId, exId, part)

  // Saving

  def futureDeleteOldAndInsertNewCollection(collection: ExerciseCollection): Future[Boolean] =
    tables.futureDeleteOldAndInsertNewCollection(collection)

  def futureDeleteOldAndInsertNewExercise(collId: Int, exercise: ExType): Future[Boolean] =
    tables.futureDeleteOldAndInsertNewExercise(collId, exercise)


  def futureSaveReview(username: String, collId: Int, exId: Int, part: PartType, review: ReviewType): Future[Boolean] =
    tables.futureSaveReview(username, collId, exId, part, review)

  // Update

  def updateExerciseState(collId: Int, exId: Int, newState: ExerciseState): Future[Boolean] = tables.updateExerciseState(collId, exId, newState)

  def updateCollectionState(collId: Int, newState: ExerciseState): Future[Boolean] = tables.updateCollectionState(collId, newState)

  // Deletion

  def futureDeleteCollection(collId: Int): Future[Boolean] = tables.futureDeleteCollection(collId)

  def futureDeleteExercise(collId: Int, exId: Int): Future[Boolean] = tables.futureDeleteExercise(collId, exId)


}
