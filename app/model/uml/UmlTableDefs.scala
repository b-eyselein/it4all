package model.uml

import javax.inject.Inject
import model.SemanticVersion
import model.persistence.SingleExerciseTableDefs
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.{JsError, JsSuccess, Json}
import slick.jdbc.JdbcProfile
import slick.lifted.{ForeignKeyQuery, PrimaryKey, ProvenShape}

import scala.concurrent.{ExecutionContext, Future}
import scala.language.{implicitConversions, postfixOps}
import model.uml.UmlConsts._

class UmlTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with SingleExerciseTableDefs[UmlExercise, UmlCompleteEx, UmlClassDiagram, UmlSolution, UmlExPart, UmlExerciseReview] {

  import profile.api._

  // Abstract types

  override protected type ExTableDef = UmlExercisesTable
  override protected type SolTableDef = UmlSolutionsTable
  override protected type ReviewsTableDef = UmlExerciseReviewsTable

  // Table Queries

  override protected val exTable      = TableQuery[UmlExercisesTable]
  override protected val solTable     = TableQuery[UmlSolutionsTable]
  override protected val reviewsTable = TableQuery[UmlExerciseReviewsTable]

  private val umlToIgnore = TableQuery[UmlToIgnoreTable]
  private val umlMappings = TableQuery[UmlMappingsTable]
  private val umlSamples  = TableQuery[UmlSampleSolutionsTable]

  // Reading

  override def completeExForEx(ex: UmlExercise): Future[UmlCompleteEx] = for {
    toIgnore <- db.run(umlToIgnore filter (i => i.exerciseId === ex.id && i.exSemVer === ex.semanticVersion) result)
    mappings <- db.run(umlMappings filter (m => m.exerciseId === ex.id && m.exSemVer === ex.semanticVersion) result)
    samples <- db.run(umlSamples filter (s => s.exerciseId === ex.id && s.exSemVer === ex.semanticVersion) result)
  } yield UmlCompleteEx(ex, toIgnore map (_._3), mappings map (m => m._3 -> m._4) toMap, samples)

  override protected def copyDBSolType(oldSol: UmlSolution, newId: Int): UmlSolution = oldSol.copy(id = newId)

  override def futureSampleSolutionsForExercisePart(exerciseId: Int, part: UmlExPart): Future[Seq[String]] = ???

  // Saving

  override protected def saveExerciseRest(compEx: UmlCompleteEx): Future[Boolean] = for {
    toIngoreSaved <- saveSeq[String](compEx.toIgnore, i => db.run(umlToIgnore += ((compEx.ex.id, compEx.ex.semanticVersion, i))))

    mappingsSaved <- saveSeq[(String, String)](compEx.mappings toSeq, {
      case (key, value) => db.run(umlMappings += ((compEx.ex.id, compEx.ex.semanticVersion, key, value)))
    })

    sampleSolutionsSaved <- saveSeq[UmlSampleSolution](compEx.sampleSolutions, sample => db.run(umlSamples += sample))
  } yield toIngoreSaved && mappingsSaved && sampleSolutionsSaved

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

    def markedText: Rep[String] = column[String]("marked_text")


    override def * : ProvenShape[UmlExercise] = (id, semanticVersion, title, author, text, state, markedText) <> (UmlExercise.tupled, UmlExercise.unapply)

  }

  class UmlToIgnoreTable(tag: Tag) extends ExForeignKeyTable[(Int, SemanticVersion, String)](tag, "uml_to_ignore") {

    def toIgnore: Rep[String] = column[String]("to_ignore")


    def pk: PrimaryKey = primaryKey("pk", (exerciseId, exSemVer, toIgnore))


    def * : ProvenShape[(Int, SemanticVersion, String)] = (exerciseId, exSemVer, toIgnore)

  }

  class UmlMappingsTable(tag: Tag) extends ExForeignKeyTable[(Int, SemanticVersion, String, String)](tag, "uml_mappings") {

    def mappingKey: Rep[String] = column[String]("mapping_key")

    def mappingValue: Rep[String] = column[String]("mapping_value")


    def pk: PrimaryKey = primaryKey("pk", (exerciseId, exSemVer, mappingKey))


    override def * : ProvenShape[(Int, SemanticVersion, String, String)] = (exerciseId, exSemVer, mappingKey, mappingValue)

  }

  class UmlSampleSolutionsTable(tag: Tag) extends ExForeignKeyTable[UmlSampleSolution](tag, "uml_sample_solutions") {

    def id: Rep[Int] = column[Int](idName)

    def sample: Rep[UmlClassDiagram] = column[UmlClassDiagram]("sample")


    def pk: PrimaryKey = primaryKey("pk", (id, exerciseId, exSemVer))


    def * : ProvenShape[UmlSampleSolution] = (id, exerciseId, exSemVer, sample) <> (UmlSampleSolution.tupled, UmlSampleSolution.unapply)

  }

  class UmlSolutionsTable(tag: Tag) extends PartSolutionsTable(tag, "uml_solutions") {

    def solution: Rep[UmlClassDiagram] = column[UmlClassDiagram]("solution")


    override def * : ProvenShape[UmlSolution] = (id, username, exerciseId, exSemVer, part, solution, points, maxPoints) <> (UmlSolution.tupled, UmlSolution.unapply)

  }

  class UmlExerciseReviewsTable(tag: Tag) extends ExerciseReviewsTable(tag, "uml_exercise_reviews") {

    def * : ProvenShape[UmlExerciseReview] = (username, exerciseId, exerciseSemVer, exercisePart, difficulty, maybeDuration.?) <> (UmlExerciseReview.tupled, UmlExerciseReview.unapply)

  }

}