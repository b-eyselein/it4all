package model.uml

import javax.inject.Inject
import model.persistence.SingleExerciseTableDefs
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
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

  // Table definitions

  class UmlExercisesTable(tag: Tag) extends HasBaseValuesTable[UmlExercise](tag, "uml_exercises") {

    def solutionAsJson = column[UmlClassDiagram]("solution_json")

    def markedText = column[String]("marked_text")

    def toIgnore = column[String]("to_ignore")


    def pk = primaryKey("pk", id)


    override def * = (id, title, author, text, state, solutionAsJson, markedText, toIgnore) <> (UmlExercise.tupled, UmlExercise.unapply)

  }

  class UmlMappingsTable(tag: Tag) extends Table[UmlMapping](tag, "uml_mappings") {

    def exerciseId = column[Int]("exercise_id")

    def mappingKey = column[String]("mapping_key")

    def mappingValue = column[String]("mapping_value")


    def pk = primaryKey("pk", (exerciseId, mappingKey))

    def exerciseFk = foreignKey("exercise_fk", exerciseId, exTable)(_.id)


    override def * = (exerciseId, mappingKey, mappingValue) <> (UmlMapping.tupled, UmlMapping.unapply)

  }

  override protected implicit val solutionTypeColumnType: BaseColumnType[UmlClassDiagram] =
    MappedColumnType.base[UmlClassDiagram, String](_.toString, _ => null)

  class UmlSolutionsTable(tag: Tag) extends PartSolutionsTable(tag, "uml_solutions") {

//    def solution = column[UmlClassDiagram]("solution_json")


    //    override def pk = primaryKey("pk", (username, exerciseId, part))


    override def * = (username, exerciseId, part, solution, points, maxPoints) <> (UmlSolution.tupled, UmlSolution.unapply)

  }

}