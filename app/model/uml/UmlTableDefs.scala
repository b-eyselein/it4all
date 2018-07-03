package model.uml

import javax.inject.Inject
import model.SemanticVersion
import model.persistence.SingleExerciseTableDefs
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.{JsError, JsSuccess, Json}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.language.{implicitConversions, postfixOps}

class UmlTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with SingleExerciseTableDefs[UmlExercise, UmlCompleteEx, UmlClassDiagram, UmlSolution, UmlExPart] {

  import profile.api._

  // Abstract types

  override protected type ExTableDef = UmlExercisesTable

  override protected type SolTableDef = UmlSolutionsTable

  // Table Queries

  override protected val exTable  = TableQuery[UmlExercisesTable]
  override protected val solTable = TableQuery[UmlSolutionsTable]

  private val umlMappings = TableQuery[UmlMappingsTable]

  // Reading

  override def completeExForEx(ex: UmlExercise): Future[UmlCompleteEx] = for {
    mappings <- db.run(umlMappings filter (_.exerciseId === ex.id) result)
  } yield UmlCompleteEx(ex, mappings)


  // Saving

  override protected def saveExerciseRest(compEx: UmlCompleteEx): Future[Boolean] =
    saveSeq[UmlMapping](compEx.mappings, m => db.run(umlMappings += m))

  // Implicit column types

  override protected implicit val partTypeColumnType: BaseColumnType[UmlExPart] =
    MappedColumnType.base[UmlExPart, String](_.entryName, UmlExParts.withNameInsensitive)

  //  override protected
  private implicit val solutionTypeColumnType: BaseColumnType[UmlClassDiagram] =
  // FIXME: refactor!
    MappedColumnType.base[UmlClassDiagram, String](UmlClassDiagramJsonFormat.umlSolutionJsonFormat.writes(_).toString(),
      str => UmlClassDiagramJsonFormat.umlSolutionJsonFormat.reads(Json.parse(str)) match {
        case JsSuccess(sol, _) => sol
        case JsError(_)        => ???
      })

  // Table definitions

  class UmlExercisesTable(tag: Tag) extends ExerciseTableDef(tag, "uml_exercises") {

    def solutionAsJson = column[UmlClassDiagram]("solution_json")

    def markedText = column[String]("marked_text")

    def toIgnore = column[String]("to_ignore")


    override def * = (id, semanticVersion, title, author, text, state, solutionAsJson, markedText, toIgnore).mapTo[UmlExercise]

  }

  class UmlMappingsTable(tag: Tag) extends Table[UmlMapping](tag, "uml_mappings") {

    def exerciseId = column[Int]("exercise_id")

    def exSemVer = column[SemanticVersion]("ex_sem_ver")

    def mappingKey = column[String]("mapping_key")

    def mappingValue = column[String]("mapping_value")


    def pk = primaryKey("pk", (exerciseId, exSemVer, mappingKey))

    def exerciseFk = foreignKey("exercise_fk", (exerciseId, exSemVer), exTable)(ex => (ex.id, ex.semanticVersion))


    override def * = (exerciseId, exSemVer, mappingKey, mappingValue).mapTo[UmlMapping]

  }

  class UmlSolutionsTable(tag: Tag) extends PartSolutionsTable(tag, "uml_solutions") {

    def solution = column[UmlClassDiagram]("solution")


    override def * = (username, exerciseId, exSemVer, part, solution, points, maxPoints).mapTo[UmlSolution]

  }

}