package model.tools.collectionTools.xml.persistence

import javax.inject.Inject
import model.core.LongText
import model.persistence.ExerciseTableDefs
import model.tools.collectionTools.ExParts
import model.tools.collectionTools.xml._
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent.ExecutionContext

class XmlTableDefs @Inject()(override protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends ExerciseTableDefs[XmlExPart, XmlExercise, XmlSampleSolution, XmlUserSolution, XmlExerciseReview]
    with HasDatabaseConfigProvider[JdbcProfile]
    with XmlTableQueries {

  protected val logger = Logger(classOf[XmlTableDefs])

  import profile.api._

  // Abstract types

  override protected type DbExType = XmlExercise

  override protected type ExTableDef = XmlExercisesTable

  override protected type CollTableDef = XmlCollectionsTable


  override protected type DbUserSolType = DbXmlUserSolution

  override protected type DbUserSolTable = XmlSolutionsTable


  override protected type DbReviewType = DbXmlExerciseReview

  override protected type ReviewsTable = XmlExerciseReviewsTable

  // Table Queries

  override protected val exTable  : TableQuery[XmlExercisesTable]   = TableQuery[XmlExercisesTable]
  override protected val collTable: TableQuery[XmlCollectionsTable] = TableQuery[XmlCollectionsTable]

  override protected val userSolutionsTableQuery: TableQuery[XmlSolutionsTable] = TableQuery[XmlSolutionsTable]

  override protected val reviewsTable: TableQuery[XmlExerciseReviewsTable] = TableQuery[XmlExerciseReviewsTable]

  // Helper methods

  override protected val exParts: ExParts[XmlExPart] = XmlExParts

  override protected val dbModels               = XmlDbModels
  override protected val exerciseReviewDbModels = XmlExerciseReviewDbModels

  override protected def copyDbUserSolType(oldSol: DbXmlUserSolution, newId: Int): DbXmlUserSolution = oldSol.copy(id = newId)

  // Column types

  override protected implicit val partTypeColumnType: BaseColumnType[XmlExPart] = jsonColumnType(exParts.jsonFormat)

  // Actual table defs

  protected class XmlCollectionsTable(tag: Tag) extends ExerciseCollectionsTable(tag, "xml_collections")

  protected class XmlExercisesTable(tag: Tag) extends ExerciseInCollectionTable(tag, "xml_exercises") {

    private implicit val ltct2: BaseColumnType[LongText] = longTextColumnType

    private implicit val xssct: BaseColumnType[Seq[XmlSampleSolution]] = jsonSeqColumnType(XmlToolJsonProtocol.sampleSolutionFormat)


    def rootNode: Rep[String] = column[String]("root_node")

    def grammarDescription: Rep[LongText] = column[LongText]("grammar_description")

    def sampleSolutions: Rep[Seq[XmlSampleSolution]] = column[Seq[XmlSampleSolution]]("samples_json")


    override def * : ProvenShape[XmlExercise] = (
      id, collectionId, toolId, semanticVersion,
      title, author, text, state,
      grammarDescription, rootNode, sampleSolutions,
    ) <> (XmlExercise.tupled, XmlExercise.unapply)

  }

  protected class XmlSolutionsTable(tag: Tag) extends AUserSolutionsTable(tag, "xml_user_solutions") {

    def grammar: Rep[String] = column[String]("grammar")

    def document: Rep[String] = column[String]("document")


    override def * : ProvenShape[DbXmlUserSolution] = (id, exerciseId, exSemVer, collectionId, username, part,
      document, grammar, points, maxPoints) <> (DbXmlUserSolution.tupled, DbXmlUserSolution.unapply)

  }

  protected class XmlExerciseReviewsTable(tag: Tag) extends ExerciseReviewsTable(tag, "xml_exercise_reviews") {

    override def * : ProvenShape[DbXmlExerciseReview] = (username, collectionId, exerciseId, exercisePart, difficulty, maybeDuration.?) <> (DbXmlExerciseReview.tupled, DbXmlExerciseReview.unapply)

  }

}
