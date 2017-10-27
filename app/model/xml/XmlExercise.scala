package model.xml

import java.nio.file.Path

import com.fasterxml.jackson.annotation.{JsonIgnore, JsonProperty}
import play.api.db.slick.HasDatabaseConfigProvider
import play.twirl.api.Html
import slick.jdbc.JdbcProfile
import model.core.ExTag
import model.TableDefs
import model.Exercise
import model.Enums.ExerciseState
import play.api.libs.json.{JsPath, Reads}
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsPath, Reads}
import model.core.StringConsts._

import scala.util.Try

object XmlExerciseReads {
  implicit def xmlExerciseReads: Reads[XmlExercise] = (
    (JsPath \ ID_NAME).read[Int] and
      (JsPath \ TITLE_NAME).read[String] and
      (JsPath \ AUTHOR_NAME).read[String] and
      (JsPath \ TEXT_NAME).read[List[String]] and
      (JsPath \ STATE_NAME).read[String] and
      (JsPath \ "rootNode").read[String] and
      (JsPath \ "exerciseType").read[String]
    ) ((i, ti, a, te, s, rn, et) => XmlExercise(i, ti, a, te.mkString, ExerciseState.valueOf(s), rn, XmlExType.valueOf(et)))
}

case class XmlExercise(i: Int, ti: String, a: String, te: String, s: ExerciseState,
                       @JsonProperty(required = true) rootNode: String,
                       @JsonProperty(required = true) exerciseType: XmlExType)
  extends Exercise(i, ti, a, te, s) {


  val fixedStart: String = if (exerciseType != XmlExType.XML_DTD) "" else
    s"""<?xml version="1.0" encoding="UTF-8"?>
       |<!DOCTYPE $rootNode SYSTEM "$rootNode.dtd">""".stripMargin

  @JsonIgnore
  override def getTags: List[ExTag] = List(exerciseType)

  @JsonIgnore
  override def renderRest(/*fileResults: List[Try[Path]]*/): Html = new Html(s"<td>$exerciseType</td>\n<td>$rootNode</td>")

}

trait XmlExercises extends TableDefs {
  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import profile.api._

  implicit val XmlExColumnType: BaseColumnType[XmlExType] =
    MappedColumnType.base[XmlExType, String](_.toString, str => Option(XmlExType.valueOf(str)).getOrElse(XmlExType.XML_DTD))

  class XmlExerciseTable(tag: Tag) extends HasBaseValuesTable[XmlExercise](tag, "xml_exercises") {

    def exerciseType = column[XmlExType]("exercise_type")

    def rootNode = column[String]("root_node")

    def * = (id, title, author, text, state, rootNode, exerciseType) <> (XmlExercise.tupled, XmlExercise.unapply)
  }

  lazy val xmlExercises = TableQuery[XmlExerciseTable]

}