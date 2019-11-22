package model.persistence

import model._
import model.tools.collectionTools._
import play.api.Logger
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

trait ExerciseTableDefQueries extends HasDatabaseConfigProvider[JdbcProfile] {
  self: ExerciseTableDefs =>

  private val logger = Logger(this.getClass)

  import profile.api._

  //  protected def copyDbUserSolType(sol: DbUserSolType, newId: Int): DbUserSolType

  //  def futureSaveUserSolution(exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, sol: UserSolType): Future[Boolean]

  //  = {
  //    val dbUserSol = solutionDbModels.dbUserSolFromUserSol(exId, exSemVer, collId, username, sol)
  //
  //    val insertQuery = userSolutionsTableQuery returning userSolutionsTableQuery.map(_.id) into ((dbUserSol, id) => copyDbUserSolType(dbUserSol, id))
  //
  //    db.run(insertQuery += dbUserSol) transform {
  //      case Success(_) => Success(true)
  //      case Failure(e) =>
  //        logger.error("Could not save solution", e)
  //        Success(false)
  //    }
  //  }

  // Helpers


  private def collectionFilter(toolId: String, collId: Int): ExerciseCollectionsTable => Rep[Boolean] =
    coll => coll.toolId === toolId && coll.id === collId

  // Numbers


  def futureHighestCollectionId: Future[Int] = db.run(collTable.map(_.id).max.result) map (_ getOrElse (-1))

  def futureNumOfExesInColl(toolId: String, collId: Int): Future[Int] = db.run(
    exTable
      .filter { ex => ex.toolId === toolId && ex.collectionId === collId }
      .length.result
  )

  def futureHighestExerciseIdInCollection(toolId: String, collId: Int): Future[Int] = db.run(
    exTable
      .filter { ex => ex.toolId === toolId && ex.collectionId === collId }
      .map(_.id)
      .max.result
  ).map(_.getOrElse(-1))

  protected def maybeOldLastSolutionId(exId: Int, collId: Int, username: String, part: ExPart): Future[Option[Int]] = {
    //    db.run(
    //      userSolutionsTableQuery
    //        .filter { us => us.username === username && us.collectionId === collId && us.exerciseId === exId && us.part === part }
    //        .map(_.id)
    //        .max
    //        .result
    //    )

    ???
  }

  protected def nextUserSolutionId(exId: Int, collId: Int, username: String, part: ExPart): Future[Int] =
    maybeOldLastSolutionId(exId, collId, username, part).map(_.map(_ + 1).getOrElse(0))

  // Reading

  def futureAllCollections(toolId: String): Future[Seq[ExerciseCollection]] = db.run(
    collTable
      .filter { coll => coll.toolId === toolId }
      .result
  )

  def futureCollById(toolId: String, collId: Int): Future[Option[ExerciseCollection]] = db.run(
    collTable
      .filter(collectionFilter(toolId, collId))
      .result
      .headOption
  )

  def futureExerciseBasicsInColl(toolMain: CollectionToolMain, collId: Int): Future[Seq[ApiExerciseBasics]] = {
    implicit val svct: BaseColumnType[SemanticVersion] = semanticVersionColumnType

    db.run(
      exTable
        .filter { ex => ex.toolId === toolMain.urlPart && ex.collectionId === collId }
        .map(ex => (ex.id, ex.collectionId, ex.toolId, ex.semanticVersion, ex.title))
        .result
    ).map(_.map(ApiExerciseBasics.tupled))
  }


  def futureExercisesInColl(toolMain: CollectionToolMain, collId: Int): Future[Seq[Exercise]] = db.run(
    exTable
      .filter { ex => ex.toolId === toolMain.urlPart && ex.collectionId === collId }
      .result
  )

  def futureExerciseById(toolId: String, collId: Int, id: Int): Future[Option[Exercise]] = db.run(
    exTable
      .filter { ex => ex.toolId === toolId && ex.collectionId === collId && ex.id === id }
      .result
      .headOption
  )

  def futureMaybeOldSolution(toolMain: CollectionToolMain, username: String, collId: Int, exId: Int, part: ExPart): Future[Option[toolMain.UserSolType]] = ???

  //  = db.run(
  //    userSolutionsTableQuery
  //      .filter {
  //        sol => sol.username === username && sol.collectionId === scenarioId && sol.exerciseId === exerciseId && sol.part === part
  //      }
  //      .sortBy(_.id.desc) // take last sample sol (with highest id)
  //      .result
  //      .headOption
  //      .map(_ map solutionDbModels.userSolFromDbUserSol))

  // Saving

  def futureUpsertCollection(collection: ExerciseCollection): Future[Boolean] =
    db.run(collTable.insertOrUpdate(collection)).transform(_ == 1, identity)

  def futureUpsertExercise(exercise: Exercise): Future[Boolean] =
    db.run(exTable.insertOrUpdate(exercise)).transform(_ == 1, identity)

  def futureSaveReview(toolMain: CollectionToolMain, username: String, collId: Int, exId: Int, part: ExPart, review: ExerciseReview): Future[Boolean] = {
    val toInsert: DbExerciseReview = AExerciseReviewDbModels.dbReviewFromReview(username, collId, exId, part, review)

    db.run(reviewsTable.insertOrUpdate(toInsert)).transform(_ == 1, identity)
  }

  // Deletion

  def futureDeleteExercise(collId: Int, id: Int): Future[Boolean] =
    db.run(exTable.filter(ex => ex.id === id && ex.collectionId === collId).delete).transform(_ == 1, identity)

  def futureDeleteCollection(collId: Int): Future[Boolean] =
    db.run(collTable.filter(_.id === collId).delete).transform(_ == 1, identity)

}
