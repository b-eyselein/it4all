package model.xml

import model.Enums.ExerciseState
import model.core.ExTag
import model.core.StringConsts._
import model.{DbExercise, TableDefs}
import net.jcazevedo.moultingyaml._
import play.api.db.slick.HasDatabaseConfigProvider
import play.twirl.api.Html
import slick.jdbc.JdbcProfile

import scala.language.implicitConversions

object XmlExYamlProtocol extends DefaultYamlProtocol {

  implicit object XmlExYamlFormat extends YamlFormat[XmlExercise] {
    override def write(ex: XmlExercise): YamlValue = YamlObject(
      YamlString(ID_NAME) -> YamlNumber(ex.id),
      YamlString(TITLE_NAME) -> YamlString(ex.title),
      YamlString(AUTHOR_NAME) -> YamlString(ex.author),
      YamlString(TEXT_NAME) -> YamlString(ex.text),
      YamlString(STATE_NAME) -> YamlString(ex.state.name),
      YamlString(EXERCISE_TYPE) -> YamlString(ex.state.name),
      YamlString(ROOT_NODE_NAME) -> YamlString(ex.rootNode),
      YamlString("referenceFile") -> YamlString(ex.refernenceFile)
    )

    override def read(yaml: YamlValue): XmlExercise = yaml.asYamlObject.getFields(
      YamlString(ID_NAME), YamlString(TITLE_NAME), YamlString(AUTHOR_NAME), YamlString(TEXT_NAME), YamlString(STATE_NAME),
      YamlString(EXERCISE_TYPE), YamlString(ROOT_NODE_NAME), YamlString("referenceFile")) match {
      case Seq(
      YamlNumber(id), YamlString(title), YamlString(author), YamlString(text), YamlString(state),
      YamlString(exerciseType), YamlString(rootNode), YamlString(refFileContent))
             => XmlExercise(id.intValue, title, author, text, ExerciseState.valueOf(state), XmlExType.valueOf(exerciseType), rootNode, refFileContent)
      case _ => /* FIXME: Fehlerbehandlung... */ deserializationError("XmlExercise expected!")

    }
  }

}

case class XmlExercise(i: Int, ti: String, a: String, te: String, s: ExerciseState,
                       exerciseType: XmlExType, rootNode: String, refernenceFile: String = "")
  extends DbExercise(i, ti, a, te, s) {

  val fixedStart: String = if (exerciseType != XmlExType.XML_DTD) "" else
    s"""<?xml version="1.0" encoding="UTF-8"?>
       |<!DOCTYPE $rootNode SYSTEM "$rootNode.dtd">""".stripMargin

  override def getTags: List[ExTag] = List(exerciseType)

  override def renderRest: Html = new Html(
    s"""<td>$exerciseType</td>
       |<td>$rootNode</td>""".stripMargin)

}

trait XmlExercises extends TableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  val xmlExercises = TableQuery[XmlExerciseTable]

  implicit val XmlExColumnType: BaseColumnType[XmlExType] =
    MappedColumnType.base[XmlExType, String](_.toString, str => Option(XmlExType.valueOf(str)).getOrElse(XmlExType.XML_DTD))

  class XmlExerciseTable(tag: Tag) extends HasBaseValuesTable[XmlExercise](tag, "xml_exercises") {

    def rootNode = column[String]("root_node")

    def exerciseType = column[XmlExType]("exercise_type")

    def refFileContent = column[String]("ref_file_content")

    def * = (id, title, author, text, state, exerciseType, rootNode, refFileContent) <> (XmlExercise.tupled, XmlExercise.unapply)
  }

}