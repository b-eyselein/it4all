package model.web

import com.google.common.base.Splitter
import model.Enums.ExerciseState
import model.core.StringConsts._
import model.web.HtmlTaskHelper.ATTRS_JOIN_STR
import net.jcazevedo.moultingyaml._

import scala.language.implicitConversions

object WebExYamlProtocol extends DefaultYamlProtocol {

  private val ATTR_SPLITTER: Splitter = Splitter.on(ATTRS_JOIN_STR).omitEmptyStrings()

  implicit def string2YamlString(str: String): YamlString = YamlString(str)

  implicit object WebExYamlFormat extends YamlFormat[WebCompleteEx] {

    val htmlTaskYamlFormat: YamlFormat[HtmlTask] = HtmlTaskYamlFormat

    val jsTaskYamlFormat: YamlFormat[JsTask] = JsTaskYamlFormat

    override def read(yaml: YamlValue): WebCompleteEx =
      yaml.asYamlObject.getFields(ID_NAME, TITLE_NAME, AUTHOR_NAME, TEXT_NAME, STATE_NAME, HTML_TEXT_NAME, JS_TEXT_NAME, HTML_TASKS_NAME, JS_TASKS_NAME) match {
        case Seq(YamlNumber(id), YamlString(title), YamlString(author), YamlString(text), YamlString(state),
        YamlString(htmlText), YamlString(jsText), YamlArray(htmlTasks), YamlArray(jsTasks)) =>

          WebCompleteEx(WebExercise(id.intValue, title, author, text, ExerciseState.valueOf(state), htmlText, jsText),
            htmlTasks.map(_.convertTo[HtmlTask]), jsTasks.map(_.convertTo[JsTask]))

        case other =>
          /* FIXME: Fehlerbehandlung... */
          other.foreach(value => println(value + "\n"))
          deserializationError("WebExercise expected!")
      }

    override def write(completeEx: WebCompleteEx): YamlValue = {
      val ex = completeEx.ex

      val yamlHtmlTasks = YamlArray(completeEx.htmlTasks.map(_.toYaml).toVector)
      println(yamlHtmlTasks.elements.size)

      YamlObject(
        YamlString(ID_NAME) -> YamlNumber(ex.id),
        YamlString(TITLE_NAME) -> YamlString(ex.title),
        YamlString(AUTHOR_NAME) -> YamlString(ex.author),
        YamlString(TEXT_NAME) -> YamlString(ex.text.grouped(100).mkString("\n")),
        YamlString(STATE_NAME) -> YamlString(ex.state.name),

        // Exercise specific values
        YamlString(HTML_TEXT_NAME) -> YamlString(ex.htmlText),
        YamlString(JS_TEXT_NAME) -> YamlString(ex.jsText),

        YamlString(HTML_TASKS_NAME) -> YamlArray(completeEx.htmlTasks.map(_.toYaml).toVector),
        YamlString(JS_TASKS_NAME) -> YamlArray(completeEx.jsTasks.map(_.toYaml).toVector)
      )
    }

  }

  implicit object HtmlTaskYamlFormat extends YamlFormat[HtmlTask] {

    val attributeYamlFormat: YamlFormat[Attribute] = TaskAttributeYamlFormat

    override def read(yaml: YamlValue): HtmlTask = yaml.asYamlObject.getFields(ID_NAME, EXERCISE_ID_NAME, TEXT_NAME, XPATH_NAME, ATTRS_NAME, TEXT_CONTENT_NAME) match {
      case Seq(YamlNumber(id), YamlNumber(exId), YamlString(text), YamlString(xpathQuery), YamlArray(attributesYaml), YamlString(textContent)) =>
        new HtmlTask(id.intValue, exId.intValue, text, xpathQuery, attributesYaml.map(_.convertTo[Attribute]), textContent)
      case other                                                                                                                               =>
        /* FIXME: Fehlerbehandlung... */
        other.foreach(value => println(value + "\n"))
        deserializationError("HtmlTask expected!")
    }

    override def write(htmlTask: HtmlTask): YamlValue = YamlObject(
      // FIXME: implement!
      YamlString(ID_NAME) -> YamlNumber(htmlTask.id),
      YamlString(EXERCISE_ID_NAME) -> YamlNumber(htmlTask.exId),
      YamlString(TEXT_NAME) -> YamlString(htmlTask.text),
      YamlString(XPATH_NAME) -> YamlString(htmlTask.xpathQuery),
      YamlString(ATTRS_NAME) -> YamlArray(htmlTask.getAttributes.map(_.toYaml).toVector),
      YamlString(TEXT_CONTENT_NAME) -> YamlString(htmlTask.textContent)
    )

  }

  implicit object TaskAttributeYamlFormat extends YamlFormat[Attribute] {

    override def read(yaml: YamlValue): Attribute = yaml.asYamlObject.getFields(KEY_NAME, VALUE_NAME) match {
      case Seq(YamlString(key), YamlString(value)) => new Attribute(key, value)
      case _                                       => deserializationError("Attribute expected!")
    }

    override def write(attr: Attribute): YamlValue = YamlObject(
      YamlString(KEY_NAME) -> YamlString(attr.key),
      YamlString(VALUE_NAME) -> YamlString(attr.value)
    )

  }

  implicit object JsTaskYamlFormat extends YamlFormat[JsTask] {

    override def read(yaml: YamlValue): JsTask = ???

    override def write(jsTask: JsTask): YamlValue = ???

  }

}