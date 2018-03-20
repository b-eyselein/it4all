package model.persistence

import model.Enums.ExerciseState
import model._
import play.api.Logger
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

trait ExerciseCollectionTableDefs[Ex <: ExInColl, CompEx <: CompleteExInColl[Ex], Coll <: ExerciseCollection[Ex, CompEx], CompColl <: CompleteCollection, SolType <: CollectionExSolution]
  extends ExerciseTableDefs[Ex, CompEx] {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // Abstract types

  override protected type ExTableDef <: ExerciseInCollectionTable[Ex]

  protected type CollTableDef <: HasBaseValuesTable[Coll]

  protected type SolTableDef <: CollectionExSolutionsTable[SolType]

  // Abstract members

  protected val collTable: TableQuery[CollTableDef]

  protected val solTable: TableQuery[SolTableDef]

  // Queries

  def futureOldSolution(username: String, exerciseId: Int): Future[Option[SolType]] =
    db.run(solTable.filter(sol => sol.username === username && sol.exerciseId === exerciseId).result.headOption)

  def futureSaveSolution(sol: SolType)(implicit ec: ExecutionContext): Future[Boolean] =
    db.run(solTable insertOrUpdate sol) map (_ => true) recover {
      case e: Exception =>
        Logger.error("Could not save solution", e)
        false
    }

  // Numbers

  def futureNumOfCollections: Future[Int] = db.run(collTable.length.result)

  def futureNumOfExesInColl(collId: Int): Future[Int] = db.run(exTable.filter(_.collectionId === collId).length.result)

  def futureHighestCollectionId(implicit ec: ExecutionContext): Future[Int] = db.run(collTable.map(_.id).max.result) map (_ getOrElse (-1))

  def futureHighestIdInCollection(collId: Int)(implicit ec: ExecutionContext): Future[Int] =
    db.run(exTable.filter(_.collectionId === collId).map(_.id).max.result) map (_ getOrElse (-1))

  // Reading

  def futureColls: Future[Seq[Coll]] = db.run(collTable.result)

  def futureCollById(id: Int): Future[Option[Coll]] = db.run(collTable.filter(_.id === id).result.headOption)

  def futureCompleteColls(implicit ec: ExecutionContext): Future[Seq[CompColl]] = futureColls flatMap (colls => Future.sequence(colls map completeCollForColl))

  def futureCompleteCollById(id: Int)(implicit ec: ExecutionContext): Future[Option[CompColl]] = futureCollById(id) flatMap {
    case Some(coll) => completeCollForColl(coll) map Some.apply
    case None       => Future(None)
  }

  protected def completeCollForColl(coll: Coll)(implicit ec: ExecutionContext): Future[CompColl]

  def futureCompleteExesInColl(collId: Int)(implicit ec: ExecutionContext): Future[Seq[CompEx]] =
    db.run(exTable.filter(_.collectionId === collId).result) flatMap (futureExes => Future.sequence(futureExes map completeExForEx))

  def futureCompleteExById(collId: Int, id: Int)(implicit ec: ExecutionContext): Future[Option[CompEx]] =
    db.run(exTable.filter(ex => ex.id === id && ex.collectionId === collId).result.headOption) flatMap {
      case Some(ex) => completeExForEx(ex) map Some.apply
      case None     => Future(None)
    }


  def futureMaybeOldSolution(username: String, scenarioId: Int, exerciseId: Int): Future[Option[SolType]] =
    db.run(solTable.filter(sol => sol.username === username && sol.collectionId === scenarioId && sol.exerciseId === exerciseId).result.headOption)

  // Saving

  def saveCompleteColl(compColl: CompColl)(implicit ec: ExecutionContext): Future[Boolean]

  // Update

  def updateCollectionState(collId: Int, newState: ExerciseState)(implicit ec: ExecutionContext): Future[Boolean] =
    db.run((for {
      coll <- collTable if coll.id === collId
    } yield coll.state).update(newState)) map (_ => true) recover {
      case e: Throwable =>
        Logger.error(s"Could not update collection $collId")
        false
    }

  def updateExerciseState(collId: Int, exId: Int, newState: ExerciseState)(implicit ec: ExecutionContext): Future[Boolean] =
    db.run((for {
      ex <- exTable if ex.id === exId && ex.collectionId === collId
    } yield ex.state).update(newState)) map (_ => true) recover {
      case e: Throwable =>
        Logger.error(s"Could not update state of exercise $exId in collection $collId", e)
        false
    }


  // Deletion

  def futureDeleteExercise(collId: Int, id: Int)(implicit ec: ExecutionContext): Future[Boolean] =
    db.run(exTable.filter(ex => ex.id === id && ex.collectionId === collId).delete) map (_ => true) recover { case e: Throwable =>
      Logger.error(s"Could not delete exercise $id in collection $collId", e)
      false
    }

  def futureDeleteCollection(collId: Int)(implicit ec: ExecutionContext): Future[Boolean] =
    db.run(collTable.filter(_.id === collId).delete) map (_ => true) recover { case e: Throwable =>
      Logger.error(s"Could not delete collection $collId", e)
      false
    }

  // Abstract table definitions

  abstract class ExerciseInCollectionTable[E <: ExInColl](tag: Tag, name: String) extends HasBaseValuesTable[E](tag, name) {

    def collectionId = column[Int]("collection_id")

  }


  abstract class CollectionExSolutionsTable[S <: Solution](tag: Tag, name: String) extends Table[S](tag, name) {

    def username = column[String]("username")

    def exerciseId = column[Int]("exercise_id")

    def collectionId = column[Int]("collection_id")


    def pk = primaryKey("pk", (username, collectionId, exerciseId))

    def exerciseFk = foreignKey("exercise_fk", exerciseId, exTable)(_.id)

    def userFk = foreignKey("user_fk", username, users)(_.username)

  }

}
