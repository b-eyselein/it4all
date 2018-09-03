package model.persistence

import model._
import model.uml._
import play.api.Logger
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.libs.json.{JsError, JsSuccess, Json}
import slick.jdbc.JdbcProfile

import scala.concurrent.Future

trait SingleExerciseTableDefs[Ex <: Exercise, CompEx <: CompleteEx[Ex], SolType, DBSolType <: DBPartSolution[PartType, SolType], PartType <: ExPart] extends IdExerciseTableDefs[Ex, CompEx] {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  protected type SolTableDef <: PartSolutionsTable

  protected val solTable: TableQuery[SolTableDef]

  // Implicit column types

  protected implicit val partTypeColumnType: BaseColumnType[PartType]

  // Queries

  def futureOldSolution(username: String, exerciseId: Int, part: PartType): Future[Option[DBSolType]] =
    db.run(solTable.filter(sol => sol.username === username && sol.exerciseId === exerciseId && sol.part === part).result.headOption)

  def futureSaveSolution(sol: DBSolType): Future[Boolean] =
    db.run(solTable insertOrUpdate sol) map (_ => true) recover {
      case e: Exception =>
        Logger.error("Could not save solution", e)
        false
    }

  def futureOldSolutions(exerciseId: Int): Future[Seq[DBSolType]] = db.run(solTable.filter(_.exerciseId === exerciseId).result)

  def futureUserCanSolvePartOfExercise(username: String, exerciseId: Int, part: PartType): Future[Boolean] = Future(true)

  // Abstract table definitions

  protected abstract class PartSolutionsTable(tag: Tag, name: String) extends Table[DBSolType](tag, name) {

    def username = column[String]("username")

    def exerciseId = column[Int]("exercise_id")

    def exSemVer = column[SemanticVersion]("ex_sem_ver")

    def part = column[PartType]("part")

    def points = column[Points]("points")

    def maxPoints = column[Points]("max_points")


    def pk = primaryKey("pk", (username, exerciseId, exSemVer, part))

    def exerciseFk = foreignKey("exercise_fk", (exerciseId, exSemVer), exTable)(ex => (ex.id, ex.semanticVersion))

    def userFk = foreignKey("user_fk", username, users)(_.username)

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
