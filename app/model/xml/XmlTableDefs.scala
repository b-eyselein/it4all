package model.xml

import javax.inject.Inject
import model.persistence.SingleExerciseTableDefs
import model.xml.dtd.{DocTypeDef, DocTypeDefParser}
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class XmlTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with SingleExerciseTableDefs[XmlExercise, XmlCompleteExercise, String, XmlSolution, XmlExPart] {

  import profile.api._

  // Abstract types

  override protected type ExTableDef = XmlExercisesTable

  override protected type SolTableDef = XmlSolutionsTable

  //  override protected type PartResultType = XmlResultForPart

  override protected val exTable = TableQuery[XmlExercisesTable]

  override protected val solTable = TableQuery[XmlSolutionsTable]

  val sampleGrammarsTable = TableQuery[XmlSampleGrammarsTable]

  //  val resultsForPartsTable = TableQuery[XmlResultsTable]


  // Column Types

  override protected implicit val partTypeColumnType: BaseColumnType[XmlExPart] =
    MappedColumnType.base[XmlExPart, String](_.entryName, XmlExParts.withNameInsensitive)

  private implicit val docTypeDefColumnType: BaseColumnType[DocTypeDef] =
    MappedColumnType.base[DocTypeDef, String](_.asString, str => {
      DocTypeDefParser.parseDTD(str) match {
        case Success(grammar) => grammar
        case Failure(error)   =>
          Logger.error("Error while reading xml dtd from db: ", error)
          DocTypeDef(Seq.empty)
      }
    })

  // Reading

  //  override protected def futureResultForUserExAndPart(username: String, exerciseId: Int, part: XmlExPart): Future[Option[XmlResultForPart]] =
  //    db.run(resultsForPartsTable.filter(r => r.username === username && r.exerciseId === exerciseId && r.part === part).result.headOption)

  override def completeExForEx(ex: XmlExercise): Future[XmlCompleteExercise] = db.run(sampleGrammarsTable.filter(_.exerciseId === ex.id).result) map {
    sampleGrammars => XmlCompleteExercise(ex, sampleGrammars)
  }

  override def futureUserCanSolvePartOfExercise(username: String, exerciseId: Int, part: XmlExPart): Future[Boolean] = part match {
    case XmlExParts.GrammarCreationXmlPart  => Future(true)
    case XmlExParts.DocumentCreationXmlPart => futureOldSolution(username, exerciseId, XmlExParts.GrammarCreationXmlPart).map(_.exists(r => r.points == r.maxPoints))
  }

  // Saving

  //  override def futureSaveResult(username: String, exerciseId: Int, part: XmlExPart, points: Double, maxPoints: Double): Future[Boolean] =
  //    db.run(resultsForPartsTable insertOrUpdate XmlResultForPart(username, exerciseId, part, points, maxPoints)) map (_ => true) recover {
  //      case e: Throwable =>
  //        Logger.error("Error while updating result: ", e)
  //        false
  //    }

  override def saveExerciseRest(compEx: XmlCompleteExercise): Future[Boolean] =
    saveSeq[XmlSampleGrammar](compEx.sampleGrammars, xsg => db.run(sampleGrammarsTable += xsg))

  // Actual table defs

  class XmlExercisesTable(tag: Tag) extends HasBaseValuesTable[XmlExercise](tag, "xml_exercises") {

    def rootNode = column[String]("root_node")

    def grammarDescription = column[String]("grammar_description")


    override def * = (id, title, author, text, state, semanticVersion, grammarDescription, rootNode).mapTo[XmlExercise]

  }

  class XmlSampleGrammarsTable(tag: Tag) extends Table[XmlSampleGrammar](tag, "xml_sample_grammars") {

    def id = column[Int]("id")

    def exerciseId = column[Int]("exercise_id")

    def sampleGrammar = column[DocTypeDef]("sample_grammar")


    def pk = primaryKey("pk", (id, exerciseId))

    def exerciseFk = foreignKey("exercise_fk", exerciseId, exTable)(_.id)


    def * = (id, exerciseId, sampleGrammar).mapTo[XmlSampleGrammar]

  }

  class XmlSolutionsTable(tag: Tag) extends PartSolutionsTable(tag, "xml_solutions") {

    def solution = column[String]("solution")


    override def * = (username, exerciseId, part, solution, points, maxPoints).mapTo[XmlSolution]

  }

}