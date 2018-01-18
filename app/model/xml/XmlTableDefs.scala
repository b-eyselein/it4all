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

  def tupled(t: (Int, String, String, String, ExerciseState, XmlExType, String, String)): XmlExercise = XmlExercise(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8)

  def apply(id: Int, title: String, author: String, text: String, state: ExerciseState, exerciseType: XmlExType, rootNode: String, refFileContent: String): XmlExercise =
    new XmlExercise(BaseValues(id, title, author, text, state), exerciseType, rootNode, refFileContent)

  def unapply(arg: XmlExercise): Option[(Int, String, String, String, ExerciseState, XmlExType, String, String)] =
    Some((arg.id, arg.title, arg.author, arg.text, arg.state, arg.exerciseType, arg.rootNode, arg.refFileContent))

}

case class XmlExercise(override val baseValues: BaseValues, exerciseType: XmlExType, rootNode: String, refFileContent: String) extends Exercise with CompleteEx[XmlExercise] {

  val fixedStart: String = if (exerciseType != XmlExType.XML_DTD) "" else
    s"""<?xml version="1.0" encoding="UTF-8"?>
       |<!DOCTYPE $rootNode SYSTEM "$rootNode.dtd">""".stripMargin

  override def ex: XmlExercise = this

  override val tags: Seq[ExTag] = Seq(ex.exerciseType)

  override val preview: Html = views.html.xml.xmlPreview.render(this)

  override def exerciseRoutes: Map[Call, String] = XmlToolObject.exerciseRoutes(this)

}

case class XmlSolution(exerciseId: Int, username: String, solution: String)

class XmlTableDefs @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] with ExerciseTableDefs[XmlExercise, XmlExercise] {

  import profile.api._

  val xmlExercises = TableQuery[XmlExercisesTable]

  val xmlSolutions = TableQuery[XmlSolutionsTable]

  override type ExTableDef = XmlExercisesTable

  override val exTable = xmlExercises

  // Column Types

  implicit val XmlExColumnType: BaseColumnType[XmlExType] =
    MappedColumnType.base[XmlExType, String](_.toString, str => XmlExType.byString(str) getOrElse XmlExType.XML_DTD)

  // Reading

  override def completeExForEx(ex: XmlExercise)(implicit ec: ExecutionContext): Future[XmlExercise] = Future(ex)

  def readXmlSolution(username: String, exerciseId: Int): Future[Option[XmlSolution]] =
    db.run(xmlSolutions.filter(sol => sol.exerciseId === exerciseId && sol.username === username).result.headOption)

  // Saving

  override def saveExerciseRest(compEx: XmlExercise)(implicit ec: ExecutionContext): Future[Boolean] = Future(true)

  def saveXmlSolution(solution: XmlSolution)(implicit ec: ExecutionContext): Future[Boolean] =
    db.run(xmlSolutions insertOrUpdate solution) map (_ => true) recover { case e: Exception => false }

  // Deletion

  class XmlExercisesTable(tag: Tag) extends HasBaseValuesTable[XmlExercise](tag, "xml_exercises") {

    def rootNode = column[String]("root_node")

    def exerciseType = column[XmlExType]("exercise_type")

    def refFileContent = column[String]("ref_file_content")

    def * = (id, title, author, text, state, exerciseType, rootNode, refFileContent) <> (XmlExercise.tupled, XmlExercise.unapply)
  }

  class XmlSolutionsTable(tag: Tag) extends Table[XmlSolution](tag, "xml_solutions") {

    def exerciseId = column[Int]("exercise_id")

    def username = column[String]("username")

    def solution = column[String]("solution")


    def pk = primaryKey("pk", (exerciseId, username))

    def exerciseFk = foreignKey("exercise_fk", exerciseId, xmlExercises)(_.id)

    def userFk = foreignKey("user_fk", username, users)(_.username)


    def * = (exerciseId, username, solution) <> (XmlSolution.tupled, XmlSolution.unapply)

  }

}