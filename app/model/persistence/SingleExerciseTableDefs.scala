package model.persistence

import model._
import model.uml._
import play.api.Logger
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.libs.json.{JsError, JsSuccess, Json}
import slick.jdbc.JdbcProfile
import slick.lifted.ForeignKeyQuery

import scala.concurrent.Future
import scala.util.{Failure, Success}

trait SingleExerciseTableDefs[CompEx <: CompleteEx, SolType, DBSolType <: DBPartSolution[PartType, SolType], PartType <: ExPart, ReviewType <: ExerciseReview[PartType]]
  extends IdExerciseTableDefs[CompEx, PartType, ReviewType] {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  protected type SolTableDef <: PartSolutionsTable

  protected val solTable: TableQuery[SolTableDef]

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

  // Abstract table definitions

  protected abstract class PartSolutionsTable(tag: Tag, name: String) extends ExForeignKeyTable[DBSolType](tag, name) {

    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def username: Rep[String] = column[String]("username")

    def part: Rep[PartType] = column[PartType]("part")

    def points = column[Points]("points")

    def maxPoints: Rep[Points] = column[Points]("max_points")


    def userFk: ForeignKeyQuery[UsersTable, User] = foreignKey("user_fk", username, users)(_.username)

  }

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

}
