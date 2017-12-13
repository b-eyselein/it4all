package model.xml

import controllers.exes.idExes.XmlToolObject
import model.Enums.ExerciseState
import model._
import play.api.mvc.Call
import play.twirl.api.Html

import scala.util.Try

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

  override def renderListRest: Html = new Html(
    s"""<td>$exerciseType</td>
       |<td>$rootNode</td>
       |<td>TODO!</td>
     """.stripMargin)

  override def exerciseRoutes: Map[Call, String] = XmlToolObject.exerciseRoutes(this)
}

trait XmlTableDefs extends TableDefs {
  self: play.api.db.slick.HasDatabaseConfigProvider[slick.jdbc.JdbcProfile] =>

  import profile.api._

  val xmlExercises = TableQuery[XmlExerciseTable]

  implicit val XmlExColumnType: BaseColumnType[XmlExType] =
    MappedColumnType.base[XmlExType, String](_.toString, str => XmlExType.byString(str) getOrElse XmlExType.XML_DTD)

  class XmlExerciseTable(tag: Tag) extends HasBaseValuesTable[XmlExercise](tag, "xml_exercises") {

    def rootNode = column[String]("root_node")

    def exerciseType = column[XmlExType]("exercise_type")

    def refFileContent = column[String]("ref_file_content")

    def * = (id, title, author, text, state, exerciseType, rootNode, refFileContent) <> (XmlExercise.tupled, XmlExercise.unapply)
  }

}