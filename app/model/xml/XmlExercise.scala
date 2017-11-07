package model.xml

import model.Enums.ExerciseState
import model.core.StringConsts._
import model.core.{CompleteEx, ExTag}
import model.{Exercise, TableDefs}
import net.jcazevedo.moultingyaml._
import play.api.db.slick.HasDatabaseConfigProvider
import play.twirl.api.{Html, HtmlFormat}
import slick.jdbc.JdbcProfile

import scala.language.implicitConversions

object XmlExYamlProtocol extends DefaultYamlProtocol {

  implicit def string2YamlString(str: String): YamlString = YamlString(str)

  implicit object XmlExYamlFormat extends YamlFormat[XmlCompleteEx] {
    override def write(completeEx: XmlCompleteEx): YamlValue = {
      val ex = completeEx.ex
      YamlObject(
        YamlString(ID_NAME) -> YamlNumber(ex.id),
        YamlString(TITLE_NAME) -> YamlString(ex.title),
        YamlString(AUTHOR_NAME) -> YamlString(ex.author),
        YamlString(TEXT_NAME) -> YamlString(ex.text),
        YamlString(STATE_NAME) -> YamlString(ex.state.name),

        // Exercise specific values
        YamlString(EXERCISE_TYPE) -> YamlString(ex.exerciseType.name),
        YamlString(ROOT_NODE_NAME) -> YamlString(ex.rootNode),
        YamlString(REF_FILE_CONTENT_NAME) -> YamlString(ex.refFileContent)

      )
    }

    override def read(yaml: YamlValue): XmlCompleteEx =
      yaml.asYamlObject.getFields(ID_NAME, TITLE_NAME, AUTHOR_NAME, TEXT_NAME, STATE_NAME, EXERCISE_TYPE, ROOT_NODE_NAME, REF_FILE_CONTENT_NAME) match {
        case Seq(
        YamlNumber(id), YamlString(title), YamlString(author), YamlString(text), YamlString(state),
        YamlString(exerciseType), YamlString(rootNode), YamlString(refFileContent)) =>
          XmlCompleteEx(
            XmlExercise(id.intValue, title, author, text, ExerciseState.valueOf(state), XmlExType.valueOf(exerciseType), rootNode, refFileContent)
          )
        case other                                                                  =>
          /* FIXME: Fehlerbehandlung... */
          println(other)
          deserializationError("XmlExercise expected!")
      }
  }

}

case class XmlCompleteEx(ex: XmlExercise) extends CompleteEx[XmlExercise] {

  override val tags: List[ExTag] = List(ex.exerciseType)

}

case class XmlExercise(i: Int, ti: String, a: String, te: String, s: ExerciseState,
                       exerciseType: XmlExType, rootNode: String, refFileContent: String = "")
  extends Exercise(i, ti, a, te, s) {

  val fixedStart: String = if (exerciseType != XmlExType.XML_DTD) "" else
    s"""<?xml version="1.0" encoding="UTF-8"?>
       |<!DOCTYPE $rootNode SYSTEM "$rootNode.dtd">""".stripMargin

  override val renderRest: Html = new Html(
    s"""<td>$exerciseType</td>
       |<td>$rootNode</td>
       |<td>${
      views.html.core.helperTemplates.modal.render("Referenzdatei " + id, new Html("<pre>" + HtmlFormat.escape(refFileContent) + "</pre>"), "Referenzdatei" + id)
    }
       |</td>""".stripMargin)

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