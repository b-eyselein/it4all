package model.tools.uml.persistence

import model.SemanticVersion
import model.core.CoreConsts.{sampleName, solutionName}
import model.persistence.ExerciseTableDefs
import model.tools.uml._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted.{PrimaryKey, ProvenShape}

import scala.concurrent.{ExecutionContext, Future}

class UmlTableDefs @javax.inject.Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile]
    with ExerciseTableDefs[UmlExPart, UmlExercise, UmlCollection, UmlClassDiagram, UmlSampleSolution, UmlUserSolution, UmlExerciseReview] {

  import profile.api._

  // Abstract types

  override protected type DbExType = DbUmlExercise

  override protected type ExTableDef = UmlExercisesTable

  override protected type CollTableDef = UmlCollectionsTable


  override protected type DbSampleSolType = DbUmlSampleSolution

  override protected type DbSampleSolTable = UmlSampleSolutionsTable


  override protected type DbUserSolType = DbUmlUserSolution

  override protected type DbUserSolTable = UmlSolutionsTable


  override protected type DbReviewType = DbUmlExerciseReview

  override protected type ReviewsTable = UmlExerciseReviewsTable

  // Table Queries

  override protected val collTable: TableQuery[UmlCollectionsTable] = TableQuery[UmlCollectionsTable]
  override protected val exTable  : TableQuery[UmlExercisesTable]   = TableQuery[UmlExercisesTable]

  override protected val sampleSolutionsTableQuery: TableQuery[UmlSampleSolutionsTable] = TableQuery[UmlSampleSolutionsTable]
  override protected val userSolutionsTableQuery  : TableQuery[UmlSolutionsTable]       = TableQuery[UmlSolutionsTable]

  override protected val reviewsTable: TableQuery[UmlExerciseReviewsTable] = TableQuery[UmlExerciseReviewsTable]

  private val umlToIgnore = TableQuery[UmlToIgnoreTable]
  private val umlMappings = TableQuery[UmlMappingsTable]

  // Helper methods

  override protected val dbModels               = UmlDbModels
  override protected val exerciseReviewDbModels = UmlExerciseReviewDbModels

  override protected def copyDbUserSolType(oldSol: DbUmlUserSolution, newId: Int): DbUmlUserSolution = oldSol.copy(id = newId)

  override protected def exDbValuesFromExercise(collId: Int, compEx: UmlExercise): DbUmlExercise =
    dbModels.dbExerciseFromExercise(collId, compEx)

  // Queries

  override def completeExForEx(collId: Int, ex: DbUmlExercise): Future[UmlExercise] = for {
    dbToIgnore <- db.run(umlToIgnore.filter { i => i.exerciseId === ex.id && i.exSemVer === ex.semanticVersion }.result)
    dbMappings <- db.run(umlMappings.filter { m => m.exerciseId === ex.id && m.exSemVer === ex.semanticVersion }.result)
    samples <- db.run(sampleSolutionsTableQuery.filter(s => s.exerciseId === ex.id && s.exSemVer === ex.semanticVersion).result).map(_.map(UmlSolutionDbModels.sampleSolFromDbSampleSol))
  } yield {

    val toIgnore = dbToIgnore.map {
      case (_, _, _, toIgnoreWord) => toIgnoreWord
    }

    val mappings = dbMappings.map {
      case (_, _, _, key, value) => key -> value
    }.toMap

    dbModels.exerciseFromDbExercise(ex, toIgnore, mappings, samples)
  }

  override def futureSampleSolutionsForExPart(collId: Int, exerciseId: Int, part: UmlExPart): Future[Seq[UmlSampleSolution]] = ???


  override protected def saveExerciseRest(collId: Int, compEx: UmlExercise): Future[Boolean] = {
    val dbSamples = compEx.sampleSolutions.map(s => UmlSolutionDbModels.dbSampleSolFromSampleSol(compEx.id, compEx.semanticVersion, collId, s))

    for {
      toIngoreSaved <- saveSeq[String](compEx.toIgnore, i => db.run(umlToIgnore += ((compEx.id, compEx.semanticVersion, collId, i))))

      mappingsSaved <- saveSeq[(String, String)](compEx.mappings.toSeq, {
        case (key, value) => db.run(umlMappings += ((compEx.id, compEx.semanticVersion, collId, key, value)))
      })

      sampleSolutionsSaved <- saveSeq[DbUmlSampleSolution](dbSamples, sample => db.run(sampleSolutionsTableQuery += sample))
    } yield toIngoreSaved && mappingsSaved && sampleSolutionsSaved
  }


  def futureMaybeOldSolution(username: String, scenarioId: Int, exerciseId: Int, part: UmlExPart): Future[Option[UmlUserSolution]] =
    Future.successful(None) // ???

  def futureSaveUserSolution(exId: Int, exSemVer: SemanticVersion, collId: Int, username: String, sol: UmlUserSolution): Future[Boolean] =
    Future.successful(false) // ???

  // Implicit column types

  override protected implicit val partTypeColumnType: BaseColumnType[UmlExPart] =
    MappedColumnType.base[UmlExPart, String](_.entryName, UmlExParts.withNameInsensitive)

  //  private implicit val umlClassDiagramColumnType: BaseColumnType[UmlClassDiagram] =
  //  // FIXME: refactor!
  //    MappedColumnType.base[UmlClassDiagram, String](UmlClassDiagramJsonFormat.umlSolutionJsonFormat.writes(_).toString(),
  //      str => UmlClassDiagramJsonFormat.umlSolutionJsonFormat.reads(Json.parse(str)) match {
  //        case JsSuccess(sol, _) => sol
  //        case JsError(_)        => ???
  //      })

  // Table definitions

  class UmlCollectionsTable(tag: Tag) extends ExerciseCollectionTable(tag, "uml_collections") {

    def * : ProvenShape[UmlCollection] = (id, title, author, text, state, shortName) <> (UmlCollection.tupled, UmlCollection.unapply)

  }

  class UmlExercisesTable(tag: Tag) extends ExerciseInCollectionTable(tag, "uml_exercises") {

    def markedText: Rep[String] = column[String]("marked_text")


    override def * : ProvenShape[DbUmlExercise] = (id, semanticVersion, collectionId, title, author, text, state, markedText) <> (DbUmlExercise.tupled, DbUmlExercise.unapply)

  }

  class UmlToIgnoreTable(tag: Tag) extends ExForeignKeyTable[(Int, SemanticVersion, Int, String)](tag, "uml_to_ignore") {

    def toIgnore: Rep[String] = column[String]("to_ignore")


    def pk: PrimaryKey = primaryKey("pk", (exerciseId, exSemVer, collectionId, toIgnore))


    def * : ProvenShape[(Int, SemanticVersion, Int, String)] = (exerciseId, exSemVer, collectionId, toIgnore)

  }

  class UmlMappingsTable(tag: Tag) extends ExForeignKeyTable[(Int, SemanticVersion, Int, String, String)](tag, "uml_mappings") {

    def mappingKey: Rep[String] = column[String]("mapping_key")

    def mappingValue: Rep[String] = column[String]("mapping_value")


    def pk: PrimaryKey = primaryKey("pk", (exerciseId, exSemVer, collectionId, mappingKey))


    override def * : ProvenShape[(Int, SemanticVersion, Int, String, String)] = (exerciseId, exSemVer, collectionId, mappingKey, mappingValue)

  }

  class UmlSampleSolutionsTable(tag: Tag) extends ASampleSolutionsTable(tag, "uml_sample_solutions") {

    def sample: Rep[UmlClassDiagram] = column[UmlClassDiagram](sampleName)(umlClassDiagramColumnType)


    def pk: PrimaryKey = primaryKey("pk", (id, exerciseId, exSemVer))


    def * : ProvenShape[DbUmlSampleSolution] = (id, exerciseId, exSemVer, collectionId, sample) <> (DbUmlSampleSolution.tupled, DbUmlSampleSolution.unapply)

  }

  class UmlSolutionsTable(tag: Tag) extends AUserSolutionsTable(tag, "uml_user_solutions") {

    def solution: Rep[UmlClassDiagram] = column[UmlClassDiagram](solutionName)(umlClassDiagramColumnType)


    override def * : ProvenShape[DbUmlUserSolution] = (id, exerciseId, exSemVer, collectionId, username,
      part, solution, points, maxPoints) <> (DbUmlUserSolution.tupled, DbUmlUserSolution.unapply)

  }

  class UmlExerciseReviewsTable(tag: Tag) extends ExerciseReviewsTable(tag, "uml_exercise_reviews") {

    def * : ProvenShape[DbUmlExerciseReview] = (username, collectionId, exerciseId, exercisePart, difficulty, maybeDuration.?) <> (DbUmlExerciseReview.tupled, DbUmlExerciseReview.unapply)

  }

}
