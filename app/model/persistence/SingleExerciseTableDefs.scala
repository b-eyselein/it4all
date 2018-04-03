package model.persistence

import model.core.ExPart
import model.uml.{UmlClassDiagram, UmlClassDiagramJsonFormat}
import model.{CompleteEx, Exercise, PartSolution, Solution}
import play.api.Logger
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.libs.json.{JsError, JsSuccess, Json}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

trait SingleExerciseTableDefs[Ex <: Exercise, CompEx <: CompleteEx[Ex], SolType <: PartSolution, PartType <: ExPart] extends IdExerciseTableDefs[Ex, CompEx] {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  protected type SolTableDef <: PartSolutionsTable[SolType]

  protected val solTable: TableQuery[SolTableDef]

  // Implicit column types

  protected implicit val partTypeColumnType: BaseColumnType[PartType]

  // Queries

  def futureOldSolution(username: String, exerciseId: Int, part: PartType): Future[Option[SolType]] =
    db.run(solTable.filter(sol => sol.username === username && sol.exerciseId === exerciseId && sol.part === part).result.headOption)

  def futureSaveSolution(sol: SolType)(implicit ec: ExecutionContext): Future[Boolean] =
    db.run(solTable insertOrUpdate sol) map (_ => true) recover {
      case e: Exception =>
        Logger.error("Could not save solution", e)
        false
    }

  // Abstract table definitions

  abstract class PartSolutionsTable[S <: Solution](tag: Tag, name: String) extends Table[S](tag, name) {

    def username = column[String]("username")

    def exerciseId = column[Int]("exercise_id")

    def part = column[PartType]("part")


    def pk = primaryKey("pk", (username, exerciseId, part))

    def exerciseFk = foreignKey("exercise_fk", exerciseId, exTable)(_.id)

    def userFk = foreignKey("user_fk", username, users)(_.username)

  }

  // For programming and uml!

  protected implicit val umlClassDiagramColumnType: BaseColumnType[UmlClassDiagram] = {

    val write = (ucd: UmlClassDiagram) => UmlClassDiagramJsonFormat.umlSolutionJsonFormat.writes(ucd).toString

    val read = (str: String) => UmlClassDiagramJsonFormat.umlSolutionJsonFormat.reads(Json.parse(str)) match {
      case JsSuccess(ucd, _) => ucd
      case JsError(errors)   =>
        errors.foreach(error => Logger.error("There has been an error loading a uml class diagram from json" + error))
        UmlClassDiagram(Seq.empty, Seq.empty, Seq.empty)
    }

    MappedColumnType.base[UmlClassDiagram, String](write, read)
  }

}
