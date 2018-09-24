package model.xml

import javax.inject.Inject
import model.SemanticVersion
import model.persistence.SingleExerciseTableDefs
import model.xml.dtd.{DocTypeDef, DocTypeDefParser}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted.{ForeignKeyQuery, PrimaryKey, ProvenShape}

import scala.concurrent.{ExecutionContext, Future}
import model.xml.XmlConsts._

class XmlTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with SingleExerciseTableDefs[XmlExercise, XmlCompleteExercise, String, XmlSolution, XmlExPart, XmlExerciseReview] {

  import profile.api._

  // Abstract types

  override protected type ExTableDef = XmlExercisesTable
  override protected type SolTableDef = XmlSolutionsTable
  override protected type ReviewsTableDef = XmlExerciseReviewsTable

  override protected val exTable      = TableQuery[XmlExercisesTable]
  override protected val solTable     = TableQuery[XmlSolutionsTable]
  override protected val reviewsTable = TableQuery[XmlExerciseReviewsTable]

  private val samplesTable = TableQuery[XmlSamplesTable]

  // Column Types

  override protected implicit val partTypeColumnType: BaseColumnType[XmlExPart] =
    MappedColumnType.base[XmlExPart, String](_.entryName, XmlExParts.withNameInsensitive)

  private implicit val docTypeDefColumnType: BaseColumnType[DocTypeDef] =
    MappedColumnType.base[DocTypeDef, String](_.asString, str => DocTypeDefParser.parseDTD(str).dtd)

  // Reading

  //  override protected def futureResultForUserExAndPart(username: String, exerciseId: Int, part: XmlExPart): Future[Option[XmlResultForPart]] =
  //    db.run(resultsForPartsTable.filter(r => r.username === username && r.exerciseId === exerciseId && r.part === part).result.headOption)

  override def completeExForEx(ex: XmlExercise): Future[XmlCompleteExercise] = for {
    samples <- db.run(samplesTable.filter(_.exerciseId === ex.id).result)
  } yield XmlCompleteExercise(ex, samples)

  override def futureUserCanSolvePartOfExercise(username: String, exerciseId: Int, part: XmlExPart): Future[Boolean] = part match {
    case XmlExParts.GrammarCreationXmlPart  => Future(true)
    case XmlExParts.DocumentCreationXmlPart => futureOldSolution(username, exerciseId, XmlExParts.GrammarCreationXmlPart).map(_.exists(r => r.points == r.maxPoints))
  }

  // Saving

  override def saveExerciseRest(compEx: XmlCompleteExercise): Future[Boolean] = for {
    samplesSaved <- saveSeq[XmlSample](compEx.sampleGrammars, xsg => db.run(samplesTable += xsg))
  } yield samplesSaved

  // Actual table defs

  class XmlExercisesTable(tag: Tag) extends ExerciseTableDef(tag, "xml_exercises") {

    def rootNode: Rep[String] = column[String]("root_node")

    def grammarDescription: Rep[String] = column[String]("grammar_description")


    override def * : ProvenShape[XmlExercise] = (id, semanticVersion, title, author, text, state, grammarDescription, rootNode) <> (XmlExercise.tupled, XmlExercise.unapply)

  }

  class XmlSamplesTable(tag: Tag) extends Table[XmlSample](tag, "xml_samples") {

    def id: Rep[Int] = column[Int](idName)

    def exerciseId: Rep[Int] = column[Int]("exercise_id")

    def exSemVer: Rep[SemanticVersion] = column[SemanticVersion]("ex_sem_ver")

    def sampleGrammar: Rep[DocTypeDef] = column[DocTypeDef]("sample_grammar")

    def sampleDocument: Rep[String] = column[String]("sample_document")


    def pk: PrimaryKey = primaryKey("pk", (id, exerciseId, exSemVer))

    def exerciseFk: ForeignKeyQuery[XmlExercisesTable, XmlExercise] = foreignKey("exercise_fk", (exerciseId, exSemVer), exTable)(ex => (ex.id, ex.semanticVersion))


    override def * : ProvenShape[XmlSample] = (id, exerciseId, exSemVer, sampleGrammar, sampleDocument) <> (XmlSample.tupled, XmlSample.unapply)

  }

  class XmlSolutionsTable(tag: Tag) extends PartSolutionsTable(tag, "xml_solutions") {

    def solution: Rep[String] = column[String]("solution")


    override def * : ProvenShape[XmlSolution] = (username, exerciseId, exSemVer, part, solution, points, maxPoints) <> (XmlSolution.tupled, XmlSolution.unapply)

  }

  class XmlExerciseReviewsTable(tag: Tag) extends ExerciseReviewsTable(tag, "xml_exercise_reviews") {

    override def * : ProvenShape[XmlExerciseReview] = (username, exerciseId, exerciseSemVer, exercisePart, difficulty, maybeDuration.?) <> (XmlExerciseReview.tupled, XmlExerciseReview.unapply)

  }

}