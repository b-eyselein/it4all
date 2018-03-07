package model.persistence

import model._
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

trait ExerciseCollectionTableDefs[Ex <: ExInColl, CompEx <: CompleteExInColl[Ex], Coll <: ExerciseCollection[Ex, CompEx], CompColl <: CompleteCollection, SolType <: CollectionExSolution]
  extends ExerciseWithSolTableDefs[Ex, CompEx, SolType] {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // Abstract types

  override protected type ExTableDef <: ExerciseInCollectionTable[Ex]

  protected type CollTableDef <: HasBaseValuesTable[Coll]

  protected type SolTableDef <: CollectionExSolutionsTable[SolType]

  // Abstract members

  protected val collTable: TableQuery[CollTableDef]

  protected val solTable: TableQuery[SolTableDef]

  // Numbers

  def futureNumOfCollections: Future[Int] = db.run(collTable.length.result)

  def futureNumOfExesInColl(collId: Int): Future[Int] = db.run(exTable.filter(_.collectionId === collId).length.result)

  def futureHighestCollectionId(implicit ec: ExecutionContext): Future[Int] = db.run(collTable.map(_.id).max.result) map (_ getOrElse (-1))

  // Reading

  def futureCompleteExById(collId: Int, id: Int)(implicit ec: ExecutionContext): Future[Option[CompEx]] =
    db.run(exTable.filter(ex => ex.id === id && ex.collectionId === collId).result.headOption) flatMap {
      case Some(ex) => completeExForEx(ex) map Some.apply
      case None     => Future(None)
    }

  def futureColls: Future[Seq[Coll]] = db.run(collTable.result)

  def futureCollById(id: Int): Future[Option[Coll]] = db.run(collTable.filter(_.id === id).result.headOption)

  def futureCompleteColls(implicit ec: ExecutionContext): Future[Seq[CompColl]] = futureColls flatMap (colls => Future.sequence(colls map completeCollForColl))

  def futureCompleteCollById(id: Int)(implicit ec: ExecutionContext): Future[Option[CompColl]] = futureCollById(id) flatMap {
    case Some(coll) => completeCollForColl(coll) map Some.apply
    case None       => Future(None)
  }

  protected def completeCollForColl(coll: Coll)(implicit ec: ExecutionContext): Future[CompColl]

  def futureMaybeOldSolution(username: String, scenarioId: Int, exerciseId: Int): Future[Option[SolType]] =
    db.run(solTable.filter(sol => sol.username === username && sol.collectionId === scenarioId && sol.exerciseId === exerciseId).result.headOption)

  // Saving

  def saveCompleteColl(compColl: CompColl)(implicit ec: ExecutionContext): Future[Boolean]

  // Deletion

  def deleteExercise(collId: Int, id: Int): Future[Int] = db.run(exTable.filter(ex => ex.id === id && ex.collectionId === collId).delete)

  def deleteColl(id: Int): Future[Int] = db.run(collTable.filter(_.id === id).delete)

  // Abstract table definitions

  abstract class ExerciseInCollectionTable[E <: ExInColl](tag: Tag, name: String) extends HasBaseValuesTable[E](tag, name) {

    def collectionId = column[Int]("collection_id")

  }

  abstract class CollectionExSolutionsTable[S <: CollectionExSolution](tag: Tag, name: String) extends SolutionsTable[S](tag, name) {

    def collectionId = column[Int]("collection_id")


    override def pk = primaryKey("pk", (username, collectionId, exerciseId))

  }

}
