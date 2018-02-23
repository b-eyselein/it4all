package model.xml

import javax.inject.Inject
import model.Enums.ExerciseState
import model.xml.XmlEnums.XmlExType
import model.{BaseValues, Exercise, ExerciseTableDefs, PartsCompleteEx}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

case class XmlExercise(override val id: Int, override val title: String, override val author: String, override val text: String, override val state: ExerciseState,
                       grammarDescription: String, sampleGrammar: String, rootNode: String)
  extends Exercise with PartsCompleteEx[XmlExercise, XmlExPart] {

  def this(baseValues: BaseValues, grammarDescription: String, sampleGrammar: String, rootNode: String) =
    this(baseValues.id, baseValues.title, baseValues.author, baseValues.text, baseValues.state, grammarDescription, sampleGrammar, rootNode)

  override def baseValues: BaseValues = BaseValues(id, title, author, text, state)

  override def ex: XmlExercise = this

  override def preview: Html = views.html.xml.xmlPreview.render(this)

  override def hasPart(partType: XmlExPart): Boolean = partType match {
    case DocumentCreationXmlPart => true
    case GrammarCreationXmlPart  => false
  }

  def getTemplate(part: XmlExPart): String = part match {
    case DocumentCreationXmlPart => s"""<?xml version="1.0" encoding="UTF-8"?>
                                       |<!DOCTYPE $rootNode SYSTEM "$rootNode.dtd">""".stripMargin
    case GrammarCreationXmlPart  => s"<!ELEMENT $rootNode (EMPTY)>"
  }

  override def textForPart(urlName: String): String = XmlExParts.values.find(_.urlName == urlName) match {
    case None                          => text
    case Some(DocumentCreationXmlPart) => "Erstellen Sie ein XML-Dokument zu folgender Grammatik:"
    case Some(GrammarCreationXmlPart)  => "Erstellen Sie eine DTD zu folgender Beschreibung. Benutzen Sie die in Klammern angegebenen Element- bzw. Attributnamen."
  }

}

sealed trait XmlSolution {
  val exerciseId: Int
  val username  : String
  val solution  : String
}

case class XmlDocumentSolution(exerciseId: Int, username: String, solution: String) extends XmlSolution

case class XmlGrammarSolution(exerciseId: Int, username: String, solution: String) extends XmlSolution

// Table defs

class XmlTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] with ExerciseTableDefs[XmlExercise, XmlExercise] {

  import profile.api._

  val xmlExercises = TableQuery[XmlExercisesTable]

  val xmlDocumentSolutions = TableQuery[XmlDocumentSolutionsTable]

  val xmlGrammarSolutions = TableQuery[XmlGrammarSolutionsTable]

  override type ExTableDef = XmlExercisesTable

  override val exTable = xmlExercises

  // Column Types

  implicit val XmlExColumnType: BaseColumnType[XmlExType] =
    MappedColumnType.base[XmlExType, String](_.toString, str => XmlExType.byString(str) getOrElse XmlExType.XML_DTD)

  // Reading

  override def completeExForEx(ex: XmlExercise)(implicit ec: ExecutionContext): Future[XmlExercise] = Future(ex)

  def readXmlSolution(username: String, exerciseId: Int, part: XmlExPart): Future[Option[XmlSolution]] = {
    val table: TableQuery[_ <: XmlSolutionsTable[_ <: XmlSolution]] = part match {
      case DocumentCreationXmlPart => xmlDocumentSolutions
      case GrammarCreationXmlPart  => xmlGrammarSolutions
    }

    db.run(table.filter(sol => sol.exerciseId === exerciseId && sol.username === username).result.headOption)
  }

  // Saving

  override def saveExerciseRest(compEx: XmlExercise)(implicit ec: ExecutionContext): Future[Boolean] = Future(true)

  def saveXmlSolution(solution: XmlSolution)(implicit ec: ExecutionContext): Future[Boolean] = {
    val action = solution match {
      case xgs: XmlGrammarSolution  => xmlGrammarSolutions insertOrUpdate xgs
      case xds: XmlDocumentSolution => xmlDocumentSolutions insertOrUpdate xds
    }

    db.run(action) map (_ => true) recover { case _: Exception => false }
  }

  // Actual table defs

  class XmlExercisesTable(tag: Tag) extends HasBaseValuesTable[XmlExercise](tag, "xml_exercises") {

    def rootNode = column[String]("root_node")

    def grammarDescription = column[String]("grammar_description")

    def sampleGrammar = column[String]("sample_grammar")


    def * = (id, title, author, text, state, grammarDescription, sampleGrammar, rootNode) <> (XmlExercise.tupled, XmlExercise.unapply)

  }

  abstract class XmlSolutionsTable[SolType <: XmlSolution](tag: Tag, tableName: String) extends Table[SolType](tag, tableName) {

    def exerciseId = column[Int]("exercise_id")

    def username = column[String]("username")

    def solution = column[String]("solution")


    def pk = primaryKey("pk", (exerciseId, username))

    def exerciseFk = foreignKey("exercise_fk", exerciseId, xmlExercises)(_.id)

    def userFk = foreignKey("user_fk", username, users)(_.username)

  }

  class XmlDocumentSolutionsTable(tag: Tag) extends XmlSolutionsTable[XmlDocumentSolution](tag, "xml_document_solutions") {

    def * = (exerciseId, username, solution) <> (XmlDocumentSolution.tupled, XmlDocumentSolution.unapply)

  }

  class XmlGrammarSolutionsTable(tag: Tag) extends XmlSolutionsTable[XmlGrammarSolution](tag, "xml_grammar_solutions") {

    def * = (exerciseId, username, solution) <> (XmlGrammarSolution.tupled, XmlGrammarSolution.unapply)

  }

}