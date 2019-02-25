package model.persistence

import model.core.overviewHelpers.SolvedState
import model.{ExerciseState, _}
import play.api.Logger
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.lifted.{ForeignKeyQuery, PrimaryKey}

import scala.concurrent.Future
import scala.util.{Failure, Success}

trait ExerciseCollectionTableDefs[CompEx <: CollectionExercise, Coll <: ExerciseCollection[CompEx], CompColl <: CompleteCollection,
SolType, DBSolType <: CollectionExSolution[SolType]] extends ExerciseTableDefs[CompEx] {
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

  protected def copyDBSolType(sol: DBSolType, newId: Int): DBSolType

  def futureOldSolution(username: String, exerciseId: Int): Future[Option[DBSolType]] =
    db.run(solTable.filter(sol => sol.username === username && sol.exerciseId === exerciseId).result.headOption)

  def futureSaveSolution(sol: DBSolType): Future[Boolean] = {
    val insertQuery = solTable returning solTable.map(_.id) into ((sol, id) => copyDBSolType(sol, id))

    db.run(insertQuery += sol) transform {
      case Success(_) => Success(true)
      case Failure(e) =>
        Logger.error("Could not save solution", e)
        Success(false)
    }
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
    case None       => Future.successful(None)
  }

  protected def completeCollForColl(coll: Coll): Future[CompColl]

  def futureExercisesInColl(collId: Int): Future[Seq[CompEx]] =
    db.run(exTable.filter(_.collectionId === collId).result) flatMap {
      futureExes: Seq[ExDbValues] => Future.sequence(futureExes map completeExForEx)
    }

  def futureExerciseById(collId: Int, id: Int): Future[Option[CompEx]] =
    db.run(exTable.filter(ex => ex.id === id && ex.collectionId === collId).result.headOption) flatMap {
      case Some(ex) => completeExForEx(ex) map Some.apply
      case None     => Future.successful(None)
    }

  def futureMaybeOldSolution(username: String, scenarioId: Int, exerciseId: Int): Future[Option[DBSolType]] =
    db.run(solTable
      .filter(sol => sol.username === username && sol.collectionId === scenarioId && sol.exerciseId === exerciseId)
      // take last sample sol (with highest id)
      .sortBy(_.id.desc)
      .result.headOption)

  def futureSampleSolutions(scenarioId: Int, exerciseId: Int): Future[Seq[String]]

  def futureSolveState(user: User, collId: Int, exId: Int): Future[Option[SolvedState]]

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

    def pk: PrimaryKey = primaryKey("pk", (id, semanticVersion))

  }

  abstract class ExerciseInCollectionTable(tag: Tag, name: String) extends HasBaseValuesTable[ExDbValues](tag, name) {

    def collectionId: Rep[Int] = column[Int]("collection_id")

    def collSemVer: Rep[SemanticVersion] = column[SemanticVersion]("coll_sem_ver")


    def pk: PrimaryKey = primaryKey("pk", (id, semanticVersion, collectionId, collSemVer))

    def scenarioFk: ForeignKeyQuery[CollTableDef, Coll] = foreignKey("scenario_fk", (collectionId, collSemVer), collTable)(co => (co.id, co.semanticVersion))

  }

  abstract class CollectionExSolutionsTable(tag: Tag, name: String) extends Table[DBSolType](tag, name) {

    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def username: Rep[String] = column[String]("username")

    def exerciseId: Rep[Int] = column[Int]("exercise_id")

    def exSemVer: Rep[SemanticVersion] = column[SemanticVersion]("ex_sem_ver")

    def collectionId: Rep[Int] = column[Int]("collection_id")

    def collSemVer: Rep[SemanticVersion] = column[SemanticVersion]("coll_sem_ver")

    def points: Rep[Points] = column[Points]("points")

    def maxPoints: Rep[Points] = column[Points]("max_points")


    //    def pk: PrimaryKey = primaryKey("pk", (id, username, collectionId, collSemVer, exerciseId, exSemVer))

    def exerciseFk: ForeignKeyQuery[ExTableDef, ExDbValues] = foreignKey("exercise_fk", (collectionId, collSemVer, exerciseId, exSemVer), exTable)(ex =>
      (ex.collectionId, ex.collSemVer, ex.id, ex.semanticVersion))

    def userFk: ForeignKeyQuery[UsersTable, User] = foreignKey("user_fk", username, users)(_.username)

  }

}
