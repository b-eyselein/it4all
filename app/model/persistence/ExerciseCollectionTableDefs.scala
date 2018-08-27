package model.persistence

import model.{ExerciseState, _}
import play.api.Logger
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

trait ExerciseCollectionTableDefs[Ex <: ExInColl, CompEx <: CompleteExInColl[Ex], Coll <: ExerciseCollection[Ex, CompEx], CompColl <: CompleteCollection,
SolType, DBSolType <: CollectionExSolution[SolType]] extends ExerciseTableDefs[Ex, CompEx] {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // Abstract types

  override protected type ExTableDef <: ExerciseInCollectionTable

  protected type CollTableDef <: ExerciseCollectionTable

  protected type SolTableDef <: CollectionExSolutionsTable

  // Abstract members

  protected val collTable: TableQuery[CollTableDef]

  protected val solTable: TableQuery[SolTableDef]

  // Queries

  def futureOldSolution(username: String, exerciseId: Int): Future[Option[DBSolType]] =
    db.run(solTable.filter(sol => sol.username === username && sol.exerciseId === exerciseId).result.headOption)

  def futureSaveSolution(sol: DBSolType): Future[Boolean] =
    db.run(solTable insertOrUpdate sol) map (_ => true) recover {
      case e: Exception =>
        Logger.error("Could not save solution", e)
        false
    }

  // Numbers

  def futureNumOfCollections: Future[Int] = db.run(collTable.length.result)

  def futureNumOfExesInColl(collId: Int): Future[Int] = db.run(exTable.filter(_.collectionId === collId).length.result)

  def futureHighestCollectionId: Future[Int] = db.run(collTable.map(_.id).max.result) map (_ getOrElse (-1))

  def futureHighestIdInCollection(collId: Int): Future[Int] =
    db.run(exTable.filter(_.collectionId === collId).map(_.id).max.result) map (_ getOrElse (-1))

  // Reading

  def futureColls: Future[Seq[Coll]] = db.run(collTable.result)

  def futureCollById(id: Int): Future[Option[Coll]] = db.run(collTable.filter(_.id === id).result.headOption)

  def futureCompleteColls: Future[Seq[CompColl]] = futureColls flatMap (colls => Future.sequence(colls map completeCollForColl))

  def futureCompleteCollById(id: Int): Future[Option[CompColl]] = futureCollById(id) flatMap {
    case Some(coll) => completeCollForColl(coll) map Some.apply
    case None       => Future(None)
  }

  protected def completeCollForColl(coll: Coll): Future[CompColl]

  def futureCompleteExesInColl(collId: Int): Future[Seq[CompEx]] =
    db.run(exTable.filter(_.collectionId === collId).result) flatMap (futureExes => Future.sequence(futureExes map completeExForEx))

  def futureCompleteExById(collId: Int, id: Int): Future[Option[CompEx]] =
    db.run(exTable.filter(ex => ex.id === id && ex.collectionId === collId).result.headOption) flatMap {
      case Some(ex) => completeExForEx(ex) map Some.apply
      case None     => Future(None)
    }


  def futureMaybeOldSolution(username: String, scenarioId: Int, exerciseId: Int): Future[Option[DBSolType]] =
    db.run(solTable.filter(sol => sol.username === username && sol.collectionId === scenarioId && sol.exerciseId === exerciseId).result.headOption)

  // Saving

  def saveCompleteColl(compColl: CompColl): Future[Boolean]

  // Update

  def updateCollectionState(collId: Int, newState: ExerciseState): Future[Boolean] = db.run((for {
    coll <- collTable if coll.id === collId
  } yield coll.state).update(newState)) map (_ => true) recover {
    case e: Throwable =>
      Logger.error(s"Could not update collection $collId", e)
      false
  }

  def updateExerciseState(collId: Int, exId: Int, newState: ExerciseState): Future[Boolean] =
    db.run((for {
      ex <- exTable if ex.id === exId && ex.collectionId === collId
    } yield ex.state).update(newState)) map (_ => true) recover {
      case e: Throwable =>
        Logger.error(s"Could not update state of exercise $exId in collection $collId", e)
        false
    }

  // Deletion

  def futureDeleteExercise(collId: Int, id: Int): Future[Boolean] = db.run(exTable.filter(ex => ex.id === id && ex.collectionId === collId).delete) map (_ => true) recover { case e: Throwable =>
    Logger.error(s"Could not delete exercise $id in collection $collId", e)
    false
  }

  def futureDeleteCollection(collId: Int): Future[Boolean] = db.run(collTable.filter(_.id === collId).delete) map (_ => true) recover { case e: Throwable =>
    Logger.error(s"Could not delete collection $collId", e)
    false
  }

  // Abstract table definitions

  protected implicit val solutionTypeColumnType: slick.ast.TypedType[SolType]

  abstract class ExerciseCollectionTable(tag: Tag, tableName: String) extends HasBaseValuesTable[Coll](tag, tableName) {

    def pk = primaryKey("pk", (id, semanticVersion))

  }

  abstract class ExerciseInCollectionTable(tag: Tag, name: String) extends HasBaseValuesTable[Ex](tag, name) {

    def collectionId = column[Int]("collection_id")

    def collSemVer = column[SemanticVersion]("coll_sem_ver")


    def pk = primaryKey("pk", (id, semanticVersion, collectionId, collSemVer))

    def scenarioFk = foreignKey("scenario_fk", (collectionId, collSemVer), collTable)(co => (co.id, co.semanticVersion))

  }

  abstract class CollectionExSolutionsTable(tag: Tag, name: String) extends Table[DBSolType](tag, name) {

    def username = column[String]("username")

    def exerciseId = column[Int]("exercise_id")

    def exSemVer = column[SemanticVersion]("ex_sem_ver")

    def collectionId = column[Int]("collection_id")

    def collSemVer = column[SemanticVersion]("coll_sem_ver")

    def points = column[Points]("points")

    def maxPoints = column[Points]("max_points")


    def pk = primaryKey("pk", (username, collectionId, collSemVer, exerciseId, exSemVer))

    def exerciseFk = foreignKey("exercise_fk", (collectionId, collSemVer, exerciseId, exSemVer), exTable)(ex =>
      (ex.collectionId, ex.collSemVer, ex.id, ex.semanticVersion))

    def userFk = foreignKey("user_fk", username, users)(_.username)

  }

}
