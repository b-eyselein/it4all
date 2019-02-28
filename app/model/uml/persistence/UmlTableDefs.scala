package model.uml.persistence

import model.SemanticVersion
import model.persistence.IdExerciseTableDefs
import model.uml._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.{JsError, JsSuccess, Json}
import slick.jdbc.JdbcProfile
import slick.lifted.{PrimaryKey, ProvenShape}

import scala.concurrent.{ExecutionContext, Future}
import scala.language.{implicitConversions, postfixOps}

class UmlTableDefs @javax.inject.Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with IdExerciseTableDefs[UmlExercise, UmlExPart, UmlClassDiagram, UmlSampleSolution, UmlUserSolution, UmlExerciseReview] {

  import profile.api._

  // Abstract types

  override protected type DbExType = DbUmlExercise

  override protected type ExTableDef = UmlExercisesTable


  override protected type DbSampleSolType = DbUmlSampleSolution

  override protected type DbSampleSolTable = UmlSampleSolutionsTable


  override protected type DbUserSolType = DbUmlUserSolution

  override protected type DbUserSolTable = UmlSolutionsTable


  override protected type ReviewsTableDef = UmlExerciseReviewsTable

  // Table Queries

  override protected val exTable      = TableQuery[UmlExercisesTable]
  override protected val solTable     = TableQuery[UmlSolutionsTable]
  override protected val reviewsTable = TableQuery[UmlExerciseReviewsTable]

  private val umlToIgnore = TableQuery[UmlToIgnoreTable]
  private val umlMappings = TableQuery[UmlMappingsTable]
  private val umlSamples  = TableQuery[UmlSampleSolutionsTable]

  // Helper methods

  override protected val dbModels = UmlDbModels

  override protected def copyDbUserSolType(oldSol: DbUmlUserSolution, newId: Int): DbUmlUserSolution = oldSol.copy(id = newId)

  override protected def exDbValuesFromExercise(collId: Int, compEx: UmlExercise): DbUmlExercise =
    dbModels.dbExerciseFromExercise(collId, compEx)

  // Reading

  override def completeExForEx(collId: Int, ex: DbUmlExercise): Future[UmlExercise] = for {
    toIgnore <- db.run(umlToIgnore filter (i => i.exerciseId === ex.id && i.exSemVer === ex.semanticVersion) result)
    mappings <- db.run(umlMappings filter (m => m.exerciseId === ex.id && m.exSemVer === ex.semanticVersion) result)
    samples <- db.run(umlSamples filter (s => s.exerciseId === ex.id && s.exSemVer === ex.semanticVersion) result) map (_ map dbModels.sampleSolFromDbSampleSol)
  } yield dbModels.exerciseFromDbExercise(ex, toIgnore map (_._3), mappings map (m => m._3 -> m._4) toMap, samples)


  override def futureSampleSolutionsForExercisePart(exerciseId: Int, part: UmlExPart): Future[Seq[String]] = ???

  // Saving

  override protected def saveExerciseRest(collId: Int, compEx: UmlExercise): Future[Boolean] = {
    val dbSamples = compEx.sampleSolutions map (s => dbModels.dbSampleSolFromSampleSol(compEx.id, compEx.semanticVersion, collId, s))

    for {
      toIngoreSaved <- saveSeq[String](compEx.toIgnore, i => db.run(umlToIgnore += ((compEx.id, compEx.semanticVersion, i))))

      mappingsSaved <- saveSeq[(String, String)](compEx.mappings toSeq, {
        case (key, value) => db.run(umlMappings += ((compEx.id, compEx.semanticVersion, key, value)))
      })

      sampleSolutionsSaved <- saveSeq[DbUmlSampleSolution](dbSamples, sample => db.run(umlSamples += sample))
    } yield toIngoreSaved && mappingsSaved && sampleSolutionsSaved
  }

  // Implicit column types

  override protected implicit val partTypeColumnType: BaseColumnType[UmlExPart] =
    MappedColumnType.base[UmlExPart, String](_.entryName, UmlExParts.withNameInsensitive)

  //  override protected
  override protected implicit val solTypeColumnType: BaseColumnType[UmlClassDiagram] =
  // FIXME: refactor!
    MappedColumnType.base[UmlClassDiagram, String](UmlClassDiagramJsonFormat.umlSolutionJsonFormat.writes(_).toString(),
      str => UmlClassDiagramJsonFormat.umlSolutionJsonFormat.reads(Json.parse(str)) match {
        case JsSuccess(sol, _) => sol
        case JsError(_)        => ???
      })

  // Table definitions

  class UmlExercisesTable(tag: Tag) extends ExerciseTableDef(tag, "uml_exercises") {

    def markedText: Rep[String] = column[String]("marked_text")


    override def * : ProvenShape[DbUmlExercise] = (id, semanticVersion, title, author, text, state, markedText) <> (DbUmlExercise.tupled, DbUmlExercise.unapply)

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

  class UmlSampleSolutionsTable(tag: Tag) extends ASampleSolutionsTable(tag, "uml_sample_solutions") {

    def pk: PrimaryKey = primaryKey("pk", (id, exerciseId, exSemVer))


    def * : ProvenShape[DbUmlSampleSolution] = (id, exerciseId, exSemVer, collectionId, sample) <> (DbUmlSampleSolution.tupled, DbUmlSampleSolution.unapply)

  }

  class UmlSolutionsTable(tag: Tag) extends AUserSolutionsTable(tag, "uml_solutions") {

    override def * : ProvenShape[DbUmlUserSolution] = (id, exerciseId, exSemVer, collectionId, username,
      part, solution, points, maxPoints) <> (DbUmlUserSolution.tupled, DbUmlUserSolution.unapply)

  }

  class UmlExerciseReviewsTable(tag: Tag) extends ExerciseReviewsTable(tag, "uml_exercise_reviews") {

    def * : ProvenShape[UmlExerciseReview] = (username, exerciseId, exerciseSemVer, exercisePart, difficulty, maybeDuration.?) <> (UmlExerciseReview.tupled, UmlExerciseReview.unapply)

  }

}
