package model.xml.persistence

import model.SemanticVersion
import model.persistence.IdExerciseTableDefs
import model.xml.XmlConsts._
import model.xml._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted.{PrimaryKey, ProvenShape}

import scala.concurrent.{ExecutionContext, Future}

class XmlTableDefs @javax.inject.Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with IdExerciseTableDefs[XmlExercise, XmlExPart, String, XmlSolution, XmlExerciseReview] {

  import profile.api._

  // Abstract types

  override protected type ExDbValues = DbXmlExercise
  override protected type ExTableDef = XmlExercisesTable
  override protected type SolTableDef = XmlSolutionsTable
  override protected type ReviewsTableDef = XmlExerciseReviewsTable

  // Table Queries

  override protected val exTable      = TableQuery[XmlExercisesTable]
  override protected val solTable     = TableQuery[XmlSolutionsTable]
  override protected val reviewsTable = TableQuery[XmlExerciseReviewsTable]

  private val samplesTable = TableQuery[XmlSamplesTable]

  // Helper methods

  override protected def exDbValuesFromExercise(compEx: XmlExercise): DbXmlExercise = XmlDbModels.dbExerciseFromExercise(compEx)

  override protected def copyDBSolType(oldSol: XmlSolution, newId: Int): XmlSolution = oldSol.copy(id = newId)

  // Reading

  override protected def completeExForEx(ex: DbXmlExercise): Future[XmlExercise] = for {
    samples: Seq[XmlSample] <- db.run(samplesTable.filter(e => e.exerciseId === ex.id && e.exSemVer === ex.semanticVersion).result)
  } yield XmlDbModels.exerciseFromDbValues(ex, samples)

  override def futureUserCanSolvePartOfExercise(username: String, exId: Int, exSemVer: SemanticVersion, part: XmlExPart): Future[Boolean] = part match {
    case XmlExParts.GrammarCreationXmlPart  => Future.successful(true)
    case XmlExParts.DocumentCreationXmlPart => futureOldSolution(username, exId, exSemVer, XmlExParts.GrammarCreationXmlPart).map(_.exists(r => r.points == r.maxPoints))
  }

  override def futureSampleSolutionsForExercisePart(exerciseId: Int, part: XmlExPart): Future[Seq[String]] =
    db.run(samplesTable.filter(_.exerciseId === exerciseId).map { sample: XmlSamplesTable =>
      part match {
        case XmlExParts.GrammarCreationXmlPart  => sample.sampleGrammar
        case XmlExParts.DocumentCreationXmlPart => sample.sampleDocument
      }
    }.result)

  // Saving

  override def saveExerciseRest(compEx: XmlExercise): Future[Boolean] = for {
    samplesSaved <- saveSeq[XmlSample](compEx.samples, xsg => db.run(samplesTable += xsg))
  } yield samplesSaved


  // Column Types

  override protected implicit val partTypeColumnType: BaseColumnType[XmlExPart] =
    MappedColumnType.base[XmlExPart, String](_.entryName, XmlExParts.withNameInsensitive)


  // Actual table defs

  class XmlExercisesTable(tag: Tag) extends ExerciseTableDef(tag, "xml_exercises") {

    def rootNode: Rep[String] = column[String]("root_node")

    def grammarDescription: Rep[String] = column[String]("grammar_description")


    override def * : ProvenShape[DbXmlExercise] = (id, semanticVersion, title, author, text, state, grammarDescription, rootNode) <> (DbXmlExercise.tupled, DbXmlExercise.unapply)

  }

  class XmlSamplesTable(tag: Tag) extends ExForeignKeyTable[XmlSample](tag, "xml_samples") {

    def id: Rep[Int] = column[Int](idName)

    def sampleGrammar: Rep[String] = column[String]("sample_grammar")

    def sampleDocument: Rep[String] = column[String]("sample_document")


    def pk: PrimaryKey = primaryKey("pk", (id, exerciseId, exSemVer))


    override def * : ProvenShape[XmlSample] = (id, exerciseId, exSemVer, sampleGrammar, sampleDocument) <> (XmlSample.tupled, XmlSample.unapply)

  }

  class XmlSolutionsTable(tag: Tag) extends PartSolutionsTable(tag, "xml_solutions") {

    def solution: Rep[String] = column[String]("solution")


    override def * : ProvenShape[XmlSolution] = (id, username, exerciseId, exSemVer, part, solution, points, maxPoints) <> (XmlSolution.tupled, XmlSolution.unapply)

  }

  class XmlExerciseReviewsTable(tag: Tag) extends ExerciseReviewsTable(tag, "xml_exercise_reviews") {

    override def * : ProvenShape[XmlExerciseReview] = (username, exerciseId, exerciseSemVer, exercisePart, difficulty, maybeDuration.?) <> (XmlExerciseReview.tupled, XmlExerciseReview.unapply)

  }

}
