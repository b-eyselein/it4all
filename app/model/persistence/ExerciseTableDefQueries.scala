package model.persistence

import model._
import model.core.overviewHelpers.{SolvedState, SolvedStates}
import model.tools.uml._
import play.api.Logger
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.libs.json.{JsError, JsSuccess, Json}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

trait ExerciseTableDefQueries[PartType <: ExPart, ExType <: Exercise, CollType <: ExerciseCollection, SolType, SampleSolType <: SampleSolution[SolType], UserSolType <: UserSolution[PartType, SolType], ReviewType <: ExerciseReview]
  extends HasDatabaseConfigProvider[JdbcProfile] {
  self: ExerciseTableDefs[PartType, ExType, CollType, SolType, SampleSolType, UserSolType, ReviewType] =>

  private val logger = Logger(this.getClass)

  import profile.api._

  protected def copyDbUserSolType(sol: DbUserSolType, newId: Int): DbUserSolType

  def futureSaveUserSolution(exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, sol: UserSolType): Future[Boolean]

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

  // Numbers

  def futureNumOfCollections: Future[Int] = db.run(collTable.length.result)

  def futureHighestCollectionId: Future[Int] = db.run(collTable.map(_.id).max.result) map (_ getOrElse (-1))

  def futureNumOfExesInColl(collId: Int): Future[Int] = db.run(exTable.filter(_.collectionId === collId).length.result)

  def futureHighestExerciseIdInCollection(collId: Int): Future[Int] =
    db.run(exTable.filter(_.collectionId === collId).map(_.id).max.result) map (_ getOrElse (-1))

  protected def maybeOldLastSolutionId(exId: Int, collId: Int, username: String, part: PartType): Future[Option[Int]] = db.run(
    userSolutionsTableQuery
      .filter { us => us.username === username && us.collectionId === collId && us.exerciseId === exId && us.part === part }
      .map(_.id)
      .max
      .result
  )

  protected def nextUserSolutionId(exId: Int, collId: Int, username: String, part: PartType): Future[Int] =
    maybeOldLastSolutionId(exId, collId, username, part).map(_.map(_ + 1).getOrElse(0))

  // Reading

  def futureAllCollections: Future[Seq[CollType]] = db.run(collTable.result)

  def futureCollById(id: Int): Future[Option[CollType]] = db.run(collTable.filter(_.id === id).result.headOption)

  def futureExercisesInColl(collId: Int): Future[Seq[ExType]] =
    db.run(exTable.filter(_.collectionId === collId).result) flatMap {
      futureExes: Seq[DbExType] => Future.sequence(futureExes map (ex => completeExForEx(collId, ex)))
    }

  def futureExerciseById(collId: Int, id: Int): Future[Option[ExType]] =
    db.run(exTable.filter { ex => ex.id === id && ex.collectionId === collId }.result.headOption) flatMap {
      case None     => Future.successful(None)
      case Some(ex) => completeExForEx(collId, ex) map Some.apply
    }

  def futureMaybeOldSolution(username: String, collId: Int, exId: Int, part: PartType): Future[Option[UserSolType]]

  //  = db.run(
  //    userSolutionsTableQuery
  //      .filter {
  //        sol => sol.username === username && sol.collectionId === scenarioId && sol.exerciseId === exerciseId && sol.part === part
  //      }
  //      .sortBy(_.id.desc) // take last sample sol (with highest id)
  //      .result
  //      .headOption
  //      .map(_ map solutionDbModels.userSolFromDbUserSol))

  def futureSampleSolutionsForExPart(scenarioId: Int, exerciseId: Int, part: PartType): Future[Seq[SampleSolType]]

  def futureSolveStateForExercisePart(user: User, collId: Int, exId: Int, part: PartType): Future[Option[SolvedState]] = db.run(
    userSolutionsTableQuery
      .filter {
        sol => sol.username === user.username && sol.collectionId === collId && sol.exerciseId === exId && sol.part === part
      }
      .sortBy(_.id.desc)
      .result.headOption
      .map {
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

  def futureInsertExercise(collId: Int, exercise: ExType): Future[Boolean] = {
    val deleteOldExQuery = exTable.filter {
      dbEx: ExTableDef =>
        dbEx.id === exercise.id && dbEx.semanticVersion === exercise.semanticVersion && dbEx.collectionId === collId
    }.delete

    val insertNewExQuery = exTable += exDbValuesFromExercise(collId, exercise)

    db.run(deleteOldExQuery) flatMap { _ =>
      db.run(insertNewExQuery) flatMap {
        _ => saveExerciseRest(collId, exercise)
      }
    }
  }

  def futureSaveReview(username: String, collId: Int, exId: Int, part: PartType, review: ReviewType): Future[Boolean] = {
    val dbReview = exerciseReviewDbModels.dbReviewFromReview(username, collId, exId, part, review)
    db.run(reviewsTable insertOrUpdate dbReview).transform(_ == 1, identity)
  }


  // Update

  def updateCollectionState(collId: Int, newState: ExerciseState): Future[Boolean] = db.run((for {
    coll <- collTable if coll.id === collId
  } yield coll.state).update(newState)) map (_ => true) recover {
    case e: Throwable =>
      logger.error(s"Could not update collection $collId", e)
      false
  }

  def updateExerciseState(collId: Int, exId: Int, newState: ExerciseState): Future[Boolean] =
    db.run((for {
      ex <- exTable if ex.id === exId && ex.collectionId === collId
    } yield ex.state).update(newState)) map (_ => true) recover {
      case e: Throwable =>
        logger.error(s"Could not update state of exercise $exId in collection $collId", e)
        false
    }

  // Deletion

  def futureDeleteExercise(collId: Int, id: Int): Future[Boolean] = db.run(exTable.filter(ex => ex.id === id && ex.collectionId === collId).delete) map (_ => true) recover { case e: Throwable =>
    logger.error(s"Could not delete exercise $id in collection $collId", e)
    false
  }

  def futureDeleteCollection(collId: Int): Future[Boolean] = db.run(collTable.filter(_.id === collId).delete) map (_ => true) recover { case e: Throwable =>
    logger.error(s"Could not delete collection $collId", e)
    false
  }

  // For programming and uml!

  protected implicit val umlClassDiagramColumnType: BaseColumnType[UmlClassDiagram] = {

    val write = (ucd: UmlClassDiagram) => UmlClassDiagramJsonFormat.umlSolutionJsonFormat.writes(ucd).toString

    val read = (str: String) => UmlClassDiagramJsonFormat.umlSolutionJsonFormat.reads(Json.parse(str)) match {
      case JsSuccess(ucd, _) => ucd
      case JsError(errors)   =>
        errors.foreach(error => logger.error(s"There has been an error loading a uml class diagram from json: $error"))
        UmlClassDiagram(Seq[UmlClass](), Seq[UmlAssociation](), Seq[UmlImplementation]())
    }

    MappedColumnType.base[UmlClassDiagram, String](write, read)
  }


}
