package model.persistence

import model.uml._
import model.{Difficulties, Difficulty, ExPart, Exercise, ExerciseReview, ExerciseState, SampleSolution, SemanticVersion, UserSolution}
import play.api.Logger
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.libs.json.{JsError, JsSuccess, Json}
import slick.jdbc.JdbcProfile
import slick.lifted.{ForeignKeyQuery, PrimaryKey}

import scala.concurrent.Future
import scala.util.{Failure, Success}

trait IdExerciseTableDefs[ExType <: Exercise, PartType <: ExPart, SolType, SampleSolType <: SampleSolution[SolType], DBSolType <: UserSolution[PartType, SolType], ReviewType <: ExerciseReview[PartType]]
  extends ExerciseTableDefs[ExType, PartType, SolType, SampleSolType, DBSolType] {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  protected type ReviewsTableDef <: ExerciseReviewsTable

  protected val reviewsTable: TableQuery[ReviewsTableDef]

  // Numbers

  def futureHighestExerciseId: Future[Int] = db.run(exTable.map(_.id).max.result) map (_ getOrElse (-1))

  // Reading

  def futureAllReviews: Future[Seq[ReviewType]] = db.run(reviewsTable.result)

  def futureReviewsForExercise(id: Int): Future[Seq[ReviewType]] = db.run(reviewsTable.filter(_.exerciseId === id).result)

  // Update

  def futureInsertExercise(compEx: ExType): Future[Boolean] = {
    val deleteOldExQuery = exTable.filter {
      dbEx: ExTableDef => dbEx.id === compEx.id && dbEx.semanticVersion === compEx.semanticVersion
    }.delete
    val insertNewExQuery = exTable += exDbValuesFromExercise(compEx)

    db.run(deleteOldExQuery) flatMap { _ =>
      db.run(insertNewExQuery) flatMap {
        insertCount: Int => saveExerciseRest(compEx)
      }
    }
  }

  def futureSaveReview(review: ReviewType): Future[Boolean] = db.run(reviewsTable insertOrUpdate review) map (_ => true) recover {
    case e: Throwable =>
      Logger.error("Error while saving review", e)
      false
  }

  def updateExerciseState(id: Int, newState: ExerciseState): Future[Boolean] = db.run((for {
    ex <- exTable if ex.id === id
  } yield ex.state).update(newState)) map (_ => true) recover {
    case e: Throwable =>
      Logger.error(s"Could not update state of exercise $id", e)
      false
  }

  // Deletion

  def deleteExercise(id: Int): Future[Int] = db.run(exTable.filter(_.id === id).delete)

  // Queries

  def futureOldSolution(username: String, exerciseId: Int, exSemVer: SemanticVersion, part: PartType): Future[Option[DBSolType]] = db.run(solTable
    .filter { sol => sol.username === username && sol.exerciseId === exerciseId && sol.part === part && sol.exSemVer === exSemVer }
    .sortBy(_.id.desc)
    .result.headOption)

  protected def copyDBSolType(oldSol: DBSolType, newId: Int): DBSolType

  def futureSaveSolution(sol: DBSolType): Future[Boolean] = {
    val insertQuery = solTable returning solTable.map(_.id) into ((sol, id) => copyDBSolType(sol, id))

    db.run(insertQuery += sol) transform {
      case Success(_)     => Success(true)
      case Failure(error) =>
        Logger.error("Error while saving sample solution", error)
        Success(false)
    }
  }

  def futureOldSolutions(exerciseId: Int): Future[Seq[DBSolType]] = db.run(solTable.filter(_.exerciseId === exerciseId).result)

  def futureUserCanSolvePartOfExercise(username: String, exId: Int, exSemVer: SemanticVersion, part: PartType): Future[Boolean] = Future(true)

  def futureSampleSolutionsForExercisePart(exerciseId: Int, part: PartType): Future[Seq[String]]


  // For programming and uml!

  protected implicit val umlClassDiagramColumnType: BaseColumnType[UmlClassDiagram] = {

    val write = (ucd: UmlClassDiagram) => UmlClassDiagramJsonFormat.umlSolutionJsonFormat.writes(ucd).toString

    val read = (str: String) => UmlClassDiagramJsonFormat.umlSolutionJsonFormat.reads(Json.parse(str)) match {
      case JsSuccess(ucd, _) => ucd
      case JsError(errors)   =>
        errors.foreach(error => Logger.error("There has been an error loading a uml class diagram from json" + error))
        UmlClassDiagram(Seq[UmlClass](), Seq[UmlAssociation](), Seq[UmlImplementation]())
    }

    MappedColumnType.base[UmlClassDiagram, String](write, read)
  }

  // Implicit column types

  protected implicit val difficultyColumnType: BaseColumnType[Difficulty] =
    MappedColumnType.base[Difficulty, String](_.entryName, Difficulties.withNameInsensitive)

  // Abstract table definitions

  abstract class ExerciseTableDef(tag: Tag, tableName: String) extends HasBaseValuesTable[ExDbValues](tag, tableName) {

    def pk: PrimaryKey = primaryKey("pk", (id, semanticVersion))

  }

  abstract class ExerciseReviewsTable(tag: Tag, tableName: String) extends Table[ReviewType](tag, tableName) {

    def username: Rep[String] = column[String]("username")

    def exerciseId: Rep[Int] = column[Int]("exercise_id")

    def exerciseSemVer: Rep[SemanticVersion] = column[SemanticVersion]("ex_sem_ver")

    def exercisePart: Rep[PartType] = column[PartType]("exercise_part")

    def difficulty: Rep[Difficulty] = column[Difficulty]("difficulty")

    def maybeDuration: Rep[Int] = column[Int]("maybe_duration")


    def pk: PrimaryKey = primaryKey("pk", (exerciseId, exercisePart))

    def exerciseFk: ForeignKeyQuery[ExTableDef, ExDbValues] = foreignKey("exercise_fk", exerciseId, exTable)(_.id)

  }

}
