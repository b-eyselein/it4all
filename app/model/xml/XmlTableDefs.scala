package model.xml

import javax.inject.Inject
import model.persistence.SingleExerciseTableDefs
import model.xml.dtd.{DocTypeDef, DocTypeDefParser}
import model.{ExerciseState, _}
import play.api.Logger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import scala.util.{Failure, Success}

// FIXME: save sampleGrammar as DocTypeDef!
case class XmlExercise(override val id: Int, override val title: String, override val author: String, override val text: String, override val state: ExerciseState,
                       grammarDescription: String, sampleGrammar: DocTypeDef, rootNode: String)
  extends Exercise with PartsCompleteEx[XmlExercise, XmlExPart] {

  override def ex: XmlExercise = this

  override def preview: Html = // FIXME: move to toolMain!
    views.html.idExercises.xml.xmlPreview(this)

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

case class XmlSolution(username: String, exerciseId: Int, part: XmlExPart, solution: String) extends PartSolution {

  override type PartType = XmlExPart

}

// Table defs

class XmlTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(override implicit val executionContext: _root_.scala.concurrent.ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] with SingleExerciseTableDefs[XmlExercise, XmlExercise, XmlSolution, XmlExPart] {

  import profile.api._

  // Abstract types

  override protected type ExTableDef = XmlExercisesTable

  override protected type SolTableDef = XmlSolutionsTable

  override protected val solTable = TableQuery[XmlSolutionsTable]
  override protected val exTable  = TableQuery[XmlExercisesTable]

  // Column Types

  override protected implicit val partTypeColumnType: BaseColumnType[XmlExPart] =
    MappedColumnType.base[XmlExPart, String](_.urlName, str => XmlExParts.values.find(_.urlName == str) getOrElse DocumentCreationXmlPart)

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

  override def completeExForEx(ex: XmlExercise): Future[XmlExercise] = Future(ex)

  // Saving

  override def saveExerciseRest(compEx: XmlExercise): Future[Boolean] = Future(true)

  // Actual table defs

  class XmlExercisesTable(tag: Tag) extends HasBaseValuesTable[XmlExercise](tag, "xml_exercises") {

    def rootNode = column[String]("root_node")

    def grammarDescription = column[String]("grammar_description")

    def sampleGrammar = column[DocTypeDef]("sample_grammar")


    override def * = (id, title, author, text, state, grammarDescription, sampleGrammar, rootNode) <> (XmlExercise.tupled, XmlExercise.unapply)

  }

  class XmlSolutionsTable(tag: Tag) extends PartSolutionsTable[XmlSolution](tag, "xml_solutions") {

    def solution = column[String]("solution")


    override def pk = primaryKey("pk", (exerciseId, username, part))


    override def * = (username, exerciseId, part, solution) <> (XmlSolution.tupled, XmlSolution.unapply)

  }

}