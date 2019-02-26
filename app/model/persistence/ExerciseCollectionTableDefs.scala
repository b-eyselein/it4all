package model.persistence

import model._
import model.core.CoreConsts.idName
import model.core.overviewHelpers.{SolvedState, SolvedStates}
import model.tools.sql.SqlConsts.shortNameName
import play.api.Logger
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.lifted.{ForeignKeyQuery, PrimaryKey}

import scala.concurrent.Future
import scala.util.{Failure, Success}

trait ExerciseCollectionTableDefs[ExType <: Exercise, PartType <: ExPart, CollType <: ExerciseCollection, SolType, SampleSolType <: SampleSolution[SolType], UserSolType <: UserSolution[PartType, SolType]]
  extends ExerciseTableDefs[ExType, PartType, SolType, SampleSolType, UserSolType] {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  // Abstract types

  override protected type ExTableDef <: ExerciseInCollectionTable

  protected type CollTableDef <: ExerciseCollectionTable

  protected type SamplesTableDef <: ACollectionSamplesTable

  protected type SolTableDef <: CollectionExSolutionsTable

  // Abstract members

  protected val collTable: TableQuery[CollTableDef]

  protected val solTable: TableQuery[SolTableDef]

  // Queries

  protected def copyDBSolType(sol: UserSolType, newId: Int): UserSolType

  def futureOldSolution(username: String, exerciseId: Int): Future[Option[UserSolType]] =
    db.run(solTable.filter(sol => sol.username === username && sol.exerciseId === exerciseId).result.headOption)

  def futureSaveSolution(sol: UserSolType): Future[Boolean] = {
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

  def futureAllCollections: Future[Seq[CollType]] = db.run(collTable.result)

  def futureCollById(id: Int): Future[Option[CollType]] = db.run(collTable.filter(_.id === id).result.headOption)

  def futureExercisesInColl(collId: Int): Future[Seq[ExType]] =
    db.run(exTable.filter(_.collectionId === collId).result) flatMap {
      futureExes: Seq[ExDbValues] => Future.sequence(futureExes map completeExForEx)
    }

  def futureExerciseById(collId: Int, id: Int): Future[Option[ExType]] =
    db.run(exTable.filter(ex => ex.id === id && ex.collectionId === collId).result.headOption) flatMap {
      case Some(ex) => completeExForEx(ex) map Some.apply
      case None     => Future.successful(None)
    }

  def futureMaybeOldSolution(username: String, scenarioId: Int, exerciseId: Int): Future[Option[UserSolType]] =
    db.run(solTable
      .filter(sol => sol.username === username && sol.collectionId === scenarioId && sol.exerciseId === exerciseId)
      // take last sample sol (with highest id)
      .sortBy(_.id.desc)
      .result.headOption)

  def futureSampleSolutionsForExPart(scenarioId: Int, exerciseId: Int, part: PartType): Future[Seq[String]]

  def futureSolveState(user: User, collId: Int, exId: Int): Future[Option[SolvedState]] = db.run(
    solTable
      .filter {
        sol => sol.username === user.username && sol.collectionId === collId && sol.exerciseId === exId
      }
      .sortBy(_.id.desc)
      .result.headOption.map {
      case None           => None
      case Some(solution) =>
        if (solution.points == solution.maxPoints) Some(SolvedStates.CompletelySolved)
        else Some(SolvedStates.PartlySolved)
    }
  )

  // Saving

  def futureInsertAndDeleteOldCollection(collection: CollType): Future[Boolean] = {
    val deleteOldQuery = collTable.filter(_.id === collection.id).delete

    db.run(deleteOldQuery) flatMap {
      _ => db.run(collTable += collection).transform(_ == 1, identity)
    }
  }

  def futureInsertExercise(compEx: ExType): Future[Boolean] = {
    val deleteOldExQuery = exTable.filter {
      dbEx: ExTableDef =>
        dbEx.id === compEx.id && dbEx.semanticVersion === compEx.semanticVersion && dbEx.collectionId === compEx.collectionId
    }.delete

    val insertNewExQuery = exTable += exDbValuesFromExercise(compEx)

    db.run(deleteOldExQuery) flatMap { _ =>
      db.run(insertNewExQuery) flatMap {
        insertCount: Int => saveExerciseRest(compEx)
      }
    }
  }

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

  abstract class ExerciseCollectionTable(tag: Tag, tableName: String) extends Table[CollType](tag, tableName) {

    def id: Rep[Int] = column[Int](idName, O.PrimaryKey)

    def title: Rep[String] = column[String]("title")

    def author: Rep[String] = column[String]("author")

    def text: Rep[String] = column[String]("ex_text")

    def state: Rep[ExerciseState] = column[ExerciseState]("ex_state")

    def shortName: Rep[String] = column[String]("short_name")

  }

  abstract class ExerciseInCollectionTable(tag: Tag, name: String) extends HasBaseValuesTable[ExDbValues](tag, name) {

    def collectionId: Rep[Int] = column[Int]("collection_id")


    def pk: PrimaryKey = primaryKey("pk", (id, semanticVersion, collectionId))

    def scenarioFk: ForeignKeyQuery[CollTableDef, CollType] = foreignKey("scenario_fk", collectionId, collTable)(_.id)

  }

  abstract class ACollectionSamplesTable(tag: Tag, name: String) extends ASampleSolutionsTable(tag, name) {

    def collectionId: Rep[Int] = column[Int]("collection_id")

  }

  abstract class CollectionExSolutionsTable(tag: Tag, name: String) extends AUserSolutionsTable(tag, name) {

    def collectionId: Rep[Int] = column[Int]("collection_id")

  }

}
