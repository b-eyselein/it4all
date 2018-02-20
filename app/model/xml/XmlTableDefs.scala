package model.xml

import javax.inject.Inject

import controllers.exes.idPartExes.XmlToolObject
import model.Enums.ExerciseState
import model._
import model.xml.XmlEnums.XmlExType
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.mvc.Call
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

object XmlExercise {

  def tupled(t: (Int, String, String, String, ExerciseState, XmlExType, String, String, String)): XmlExercise =
    XmlExercise(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9)

  def apply(id: Int, title: String, author: String, text: String, state: ExerciseState, exerciseType: XmlExType,
            grammarDescription: String, rootNode: String, refFileContent: String): XmlExercise =
    new XmlExercise(BaseValues(id, title, author, text, state), exerciseType, grammarDescription, rootNode, refFileContent)

  def unapply(arg: XmlExercise): Option[(Int, String, String, String, ExerciseState, XmlExType, String, String, String)] =
    Some((arg.id, arg.title, arg.author, arg.text, arg.state, arg.exerciseType, arg.grammarDescription, arg.rootNode, arg.refFileContent))

}

case class XmlExercise(override val baseValues: BaseValues, exerciseType: XmlExType, grammarDescription: String, rootNode: String, refFileContent: String)
  extends Exercise with PartsCompleteEx[XmlExercise, XmlExPart] {

  val fixedStart: String = if (exerciseType != XmlExType.XML_DTD) "" else
    s"""<?xml version="1.0" encoding="UTF-8"?>
       |<!DOCTYPE $rootNode SYSTEM "$rootNode.dtd">""".stripMargin

  override def ex: XmlExercise = this

  override val tags: Seq[ExTag] = Seq(ex.exerciseType)

  override def preview: Html = views.html.xml.xmlPreview.render(this)

  override def exerciseRoutes: Map[Call, String] = XmlToolObject.exerciseRoutes(this)

  override def hasPart(partType: XmlExPart): Boolean = true

  def getTemplate(part: XmlExPart): String = part match {
    case DocumentCreationXmlPart => fixedStart
    case GrammarCreationXmlPart  => s"<!ELEMENT $rootNode ()>"
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

  def readXmlSolution(username: String, exerciseId: Int, part: XmlExPart): Future[Option[String]] = {
    val table: TableQuery[_ <: XmlSolutionsTable[_]] = part match {
      case DocumentCreationXmlPart => xmlDocumentSolutions
      case GrammarCreationXmlPart  => xmlGrammarSolutions
    }

    db.run(table.filter(sol => sol.exerciseId === exerciseId && sol.username === username).map(_.solution).result.headOption)
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

  // Deletion

  class XmlExercisesTable(tag: Tag) extends HasBaseValuesTable[XmlExercise](tag, "xml_exercises") {

    def rootNode = column[String]("root_node")

    def exerciseType = column[XmlExType]("exercise_type")

    def grammarDescription = column[String]("grammar_description")

    def refFileContent = column[String]("ref_file_content")


    def * = (id, title, author, text, state, exerciseType, grammarDescription, rootNode, refFileContent) <> (XmlExercise.tupled, XmlExercise.unapply)

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