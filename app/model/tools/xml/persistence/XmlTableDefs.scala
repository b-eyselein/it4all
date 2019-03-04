package model.tools.xml.persistence

import model.persistence.ExerciseTableDefs
import model.tools.xml._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted.{PrimaryKey, ProvenShape}

import scala.concurrent.{ExecutionContext, Future}

class XmlTableDefs @javax.inject.Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile]
    with ExerciseTableDefs[XmlExPart, XmlExercise, XmlCollection, XmlSolution, XmlSampleSolution, XmlUserSolution, XmlExerciseReview] {

  import profile.api._

  // Abstract types

  override protected type DbExType = DbXmlExercise

  override protected type ExTableDef = XmlExercisesTable

  override protected type CollTableDef = XmlCollectionsTable


  override protected type DbSampleSolType = DbXmlSampleSolution

  override protected type DbSampleSolTable = XmlSamplesTable


  override protected type DbUserSolType = DbXmlUserSolution

  override protected type DbUserSolTable = XmlSolutionsTable


  override protected type DbReviewType = DbXmlExerciseReview

  override protected type ReviewsTable = XmlExerciseReviewsTable

  // Table Queries

  override protected val exTable  : TableQuery[XmlExercisesTable]   = TableQuery[XmlExercisesTable]
  override protected val collTable: TableQuery[XmlCollectionsTable] = TableQuery[XmlCollectionsTable]

  override protected val sampleSolutionsTableQuery: TableQuery[XmlSamplesTable]   = TableQuery[XmlSamplesTable]
  override protected val userSolutionsTableQuery  : TableQuery[XmlSolutionsTable] = TableQuery[XmlSolutionsTable]

  override protected val reviewsTable: TableQuery[XmlExerciseReviewsTable] = TableQuery[XmlExerciseReviewsTable]

  // Helper methods

  override protected val dbModels               = XmlDbModels
  override protected val solutionDbModels       = XmlSolutionDbModels
  override protected val exerciseReviewDbModels = XmlExerciseReviewDbModels

  override protected def copyDbUserSolType(oldSol: DbXmlUserSolution, newId: Int): DbXmlUserSolution = oldSol.copy(id = newId)

  override protected def exDbValuesFromExercise(collId: Int, compEx: XmlExercise): DbXmlExercise =
    dbModels.dbExerciseFromExercise(collId, compEx)

  // Reading

  override protected def completeExForEx(collId: Int, ex: DbXmlExercise): Future[XmlExercise] = for {
    samples <- db.run(sampleSolutionsTableQuery.filter(e => e.exerciseId === ex.id && e.exSemVer === ex.semanticVersion).result) map (_ map solutionDbModels.sampleSolFromDbSampleSol)
  } yield dbModels.exerciseFromDbValues(ex, samples)

  override def futureSampleSolutionsForExPart(collId: Int, exId: Int, part: XmlExPart): Future[Seq[String]] =
    db.run(sampleSolutionsTableQuery
      .filter { s => s.collectionId === collId && s.exerciseId === exId }
      .map { sampleSol =>
        part match {
          case XmlExParts.GrammarCreationXmlPart  => sampleSol.grammar
          case XmlExParts.DocumentCreationXmlPart => sampleSol.document
        }
      }
      .result)

  // Saving

  override def saveExerciseRest(collId: Int, compEx: XmlExercise): Future[Boolean] = {
    val dbSamples = compEx.samples map (s => solutionDbModels.dbSampleSolFromSampleSol(compEx.id, compEx.semanticVersion, collId, s))

    for {
      samplesSaved <- saveSeq[DbXmlSampleSolution](dbSamples, i => db.run(sampleSolutionsTableQuery += i), Some("XmlSample"))
    } yield samplesSaved
  }

  // Column Types

  override protected implicit val partTypeColumnType: BaseColumnType[XmlExPart] =
    MappedColumnType.base[XmlExPart, String](_.entryName, XmlExParts.withNameInsensitive)

  // Actual table defs

  class XmlCollectionsTable(tag: Tag) extends ExerciseCollectionTable(tag, "xml_collections") {

    override def * : ProvenShape[XmlCollection] = (id, title, author, text, state, shortName) <> (XmlCollection.tupled, XmlCollection.unapply)

  }

  class XmlExercisesTable(tag: Tag) extends ExerciseInCollectionTable(tag, "xml_exercises") {

    def rootNode: Rep[String] = column[String]("root_node")

    def grammarDescription: Rep[String] = column[String]("grammar_description")


    override def * : ProvenShape[DbXmlExercise] = (id, semanticVersion, collectionId, title, author, text, state, grammarDescription, rootNode) <> (DbXmlExercise.tupled, DbXmlExercise.unapply)

  }

  class XmlSamplesTable(tag: Tag) extends ASampleSolutionsTable(tag, "xml_samples") {

    def grammar: Rep[String] = column[String]("grammar")

    def document: Rep[String] = column[String]("document")


    def pk: PrimaryKey = primaryKey("pk", (id, exerciseId, exSemVer))


    override def * : ProvenShape[DbXmlSampleSolution] = (id, exerciseId, exSemVer, collectionId, document, grammar) <> (DbXmlSampleSolution.tupled, DbXmlSampleSolution.unapply)

  }

  class XmlSolutionsTable(tag: Tag) extends AUserSolutionsTable(tag, "xml_solutions") {

    def grammar: Rep[String] = column[String]("grammar")

    def document: Rep[String] = column[String]("document")


    override def * : ProvenShape[DbXmlUserSolution] = (id, exerciseId, exSemVer, collectionId, username, part,
      document, grammar, points, maxPoints) <> (DbXmlUserSolution.tupled, DbXmlUserSolution.unapply)

  }

  class XmlExerciseReviewsTable(tag: Tag) extends ExerciseReviewsTable(tag, "xml_exercise_reviews") {

    override def * : ProvenShape[DbXmlExerciseReview] = (username, collectionId, exerciseId, exercisePart, difficulty, maybeDuration.?) <> (DbXmlExerciseReview.tupled, DbXmlExerciseReview.unapply)

  }

}
