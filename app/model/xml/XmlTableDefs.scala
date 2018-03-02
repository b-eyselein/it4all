package model.xml

import javax.inject.Inject
import model.Enums.ExerciseState
import model._
import model.persistence.SingleExerciseTableDefs
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class XmlCompleteExWrapper(val compEx: XmlExercise) extends CompleteExWrapper {

  override type Ex = XmlExercise

  override type CompEx = XmlExercise

}

case class XmlExercise(override val id: Int, override val title: String, override val author: String, override val text: String, override val state: ExerciseState,
                       grammarDescription: String, sampleGrammar: String, rootNode: String)
  extends Exercise with PartsCompleteEx[XmlExercise, XmlExPart] {

  def this(baseValues: (Int, String, String, String, ExerciseState), grammarDescription: String, sampleGrammar: String, rootNode: String) =
    this(baseValues._1, baseValues._2, baseValues._3, baseValues._4, baseValues._5, grammarDescription, sampleGrammar, rootNode)

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

  override def wrapped: CompleteExWrapper = new XmlCompleteExWrapper(this)

}

case class XmlSolution(username: String, exerciseId: Int, part: XmlExPart, solution: String) extends PartSolution {

  override type PartType = XmlExPart

}

// Table defs

class XmlTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] with SingleExerciseTableDefs[XmlExercise, XmlExercise, XmlSolution, XmlExPart] {

  import profile.api._

  // Abstract types

  override type ExTableDef = XmlExercisesTable

  override type SolTableDef = XmlSolutionsTable

  override val solTable = TableQuery[XmlSolutionsTable]

  override val exTable = TableQuery[XmlExercisesTable]

  // Column Types

  override implicit val partTypeColumnType: BaseColumnType[XmlExPart] =
    MappedColumnType.base[XmlExPart, String](_.urlName, str => XmlExParts.values.find(_.urlName == str) getOrElse DocumentCreationXmlPart)

  // Reading

  override def completeExForEx(ex: XmlExercise)(implicit ec: ExecutionContext): Future[XmlExercise] = Future(ex)

  def readXmlSolution(username: String, exerciseId: Int, part: XmlExPart): Future[Option[XmlSolution]] =
    db.run(solTable.filter(sol => sol.exerciseId === exerciseId && sol.username === username).result.headOption)

  // Saving

  override def saveExerciseRest(compEx: XmlExercise)(implicit ec: ExecutionContext): Future[Boolean] = Future(true)

  def saveXmlSolution(solution: XmlSolution)(implicit ec: ExecutionContext): Future[Boolean] =
    db.run(solTable insertOrUpdate solution) map (_ => true) recover { case _: Exception => false }

  // Actual table defs

  class XmlExercisesTable(tag: Tag) extends HasBaseValuesTable[XmlExercise](tag, "xml_exercises") {

    def rootNode = column[String]("root_node")

    def grammarDescription = column[String]("grammar_description")

    def sampleGrammar = column[String]("sample_grammar")


    def * = (id, title, author, text, state, grammarDescription, sampleGrammar, rootNode) <> (XmlExercise.tupled, XmlExercise.unapply)

  }

  class XmlSolutionsTable(tag: Tag) extends PartSolutionsTable[XmlSolution](tag, "xml_solutions") {

    def solution = column[String]("solution")


    override def pk = primaryKey("pk", (exerciseId, username, part))


    def * = (username, exerciseId, part, solution) <> (XmlSolution.tupled, XmlSolution.unapply)

  }


}