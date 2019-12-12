package model.persistence

import model._
import model.tools.collectionTools._
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.libs.json.JsValue
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

trait ExerciseTableDefQueries extends HasDatabaseConfigProvider[JdbcProfile] {
  self: ExerciseTableDefs =>

  import profile.api._

  // Helpers

  private def collectionFilter(toolId: String, collId: Int): ExerciseCollectionsTable => Rep[Boolean] =
    coll => coll.toolId === toolId && coll.id === collId

  private def solutionFilter(exercise: Exercise, user: User, part: ExPart): UserSolutionsTable => Rep[Boolean] = {
    implicit val ptct: profile.api.BaseColumnType[ExPart] = exPartColumnType

    userSolution =>
      userSolution.username === user.username &&
        userSolution.exerciseId === exercise.id &&
        userSolution.collectionId === exercise.collectionId &&
        userSolution.toolId === exercise.toolId &&
        userSolution.part === part
  }


  private def futureNextUserSolutionId(exercise: Exercise, user: User, part: ExPart): Future[Int] = for {
    maybeCurrentHighestId <- db.run(
      userSolutionsTQ.filter(solutionFilter(exercise, user, part)).map(_.id).max.result
    )
  } yield maybeCurrentHighestId.fold(0)(_ + 1)


  // Reading

  def futureAllCollections(toolId: String): Future[Seq[ExerciseCollection]] = db.run(
    collectionsTQ
      .filter { coll => coll.toolId === toolId }
      .result
  )

  def futureCollById(toolId: String, collId: Int): Future[Option[ExerciseCollection]] = db.run(
    collectionsTQ
      .filter(collectionFilter(toolId, collId))
      .result
      .headOption
  )

  def futureExerciseMetaData(toolId: String, collId: Int): Future[Seq[ExerciseMetaData]] = db.run(
    exercisesTQ
      .filter { ex => ex.toolId === toolId && ex.collectionId === collId }
      .result
      .map { exes => exes.map(ExerciseMetaData.forExercise) }
  )

  def futureExercisesInColl(toolMain: CollectionToolMain, collId: Int): Future[Seq[Exercise]] = db.run(
    exercisesTQ
      .filter { ex => ex.toolId === toolMain.urlPart && ex.collectionId === collId }
      .result
  )

  def futureExerciseById(toolId: String, collId: Int, id: Int): Future[Option[Exercise]] = db.run(
    exercisesTQ
      .filter { ex => ex.toolId === toolId && ex.collectionId === collId && ex.id === id }
      .result
      .headOption
  )

  def futureCollectionAndExercise(toolId: String, collectionId: Int, exerciseId: Int): Future[Option[(ExerciseCollection, Exercise)]] = for {
    collection <- futureCollById(toolId, collectionId)
    exercise <- futureExerciseById(toolId, collectionId, exerciseId)
  } yield collection zip exercise

  // Saving

  def futureUpsertCollection(collection: ExerciseCollection): Future[Boolean] =
    db.run(collectionsTQ.insertOrUpdate(collection)).transform(_ == 1, identity)

  def futureUpsertExercise(exercise: Exercise): Future[Boolean] =
    db.run(exercisesTQ.insertOrUpdate(exercise)).transform(_ == 1, identity)

  def futureInsertSolution(user: User, exercise: Exercise, part: ExPart, solution: JsValue): Future[Boolean] = for {
    nextSolutionId <- futureNextUserSolutionId(exercise, user, part)

    dbUserSolution = DbUserSolution(
      nextSolutionId, exercise.id, exercise.collectionId, exercise.toolId, exercise.semanticVersion,
      part, user.username, solution
    )

    inserted <- db.run(userSolutionsTQ += dbUserSolution).transform(_ == 1, identity)
  } yield inserted

  def futureSaveReview(toolMain: CollectionToolMain, username: String, collId: Int, exId: Int, part: ExPart, review: ExerciseReview): Future[Boolean] = {
    val toInsert: DbExerciseReview = AExerciseReviewDbModels.dbReviewFromReview(username, collId, exId, part, review)

    db.run(reviewsTQ.insertOrUpdate(toInsert)).transform(_ == 1, identity)
  }

  // Deletion

  def futureDeleteCollection(collId: Int): Future[Boolean] =
    db.run(collectionsTQ.filter(_.id === collId).delete).transform(_ == 1, identity)

  def futureDeleteExercise(collId: Int, id: Int): Future[Boolean] =
    db.run(exercisesTQ.filter(ex => ex.id === id && ex.collectionId === collId).delete).transform(_ == 1, identity)

}
