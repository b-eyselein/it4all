package model.web

import model.MyYamlProtocol._
import model.web.WebConsts._
import model.{BaseValues, MyYamlProtocol}
import net.jcazevedo.moultingyaml._

import scala.language.{implicitConversions, postfixOps}

object WebExYamlProtocol extends MyYamlProtocol {

  implicit object WebExYamlFormat extends ExYamlFormat[WebCompleteEx] {

    override def readRest(yamlObject: YamlObject, baseValues: BaseValues): WebCompleteEx = {
      val htmlTasks = yamlObject.optArrayField(HTML_TASKS_NAME, _ convertTo[HtmlCompleteTask] HtmlCompleteTaskYamlFormat(baseValues.id))
      val jsTasks = yamlObject.optArrayField(JS_TASKS_NAME, _ convertTo[JsCompleteTask] JsCompleteTaskYamlFormat(baseValues.id))

      WebCompleteEx(
        WebExercise(baseValues, yamlObject.optStringField(HTML_TEXT_NAME), htmlTasks.nonEmpty, yamlObject.optStringField(JS_TEXT_NAME), jsTasks.nonEmpty),
        htmlTasks, jsTasks)
    }

    override protected def writeRest(completeEx: WebCompleteEx): Map[YamlValue, YamlValue] = Map(
      YamlString(HTML_TEXT_NAME) -> (completeEx.ex.htmlText map YamlString getOrElse YamlNull),
      YamlString(JS_TEXT_NAME) -> (completeEx.ex.jsText map YamlString getOrElse YamlNull),

      YamlString(HTML_TASKS_NAME) -> YamlArray(completeEx.htmlTasks map (_ toYaml HtmlCompleteTaskYamlFormat(completeEx.ex.id)) toVector),
      YamlString(JS_TASKS_NAME) -> YamlArray(completeEx.jsTasks map (_ toYaml JsCompleteTaskYamlFormat(completeEx.ex.id)) toVector)
    )
  }

  case class HtmlCompleteTaskYamlFormat(exerciseId: Int) extends MyYamlFormat[HtmlCompleteTask] {

    override def write(htmlCompTask: HtmlCompleteTask): YamlValue = {
      val yamlAttrs = YamlArray(htmlCompTask.attributes map (_ toYaml TaskAttributeYamlFormat(htmlCompTask.task.id, htmlCompTask.task.exerciseId)) toVector)
      YamlObject(
        YamlString(ID_NAME) -> htmlCompTask.task.id,
        YamlString(TEXT_NAME) -> htmlCompTask.task.text,
        YamlString(XPATH_NAME) -> htmlCompTask.task.xpathQuery,
        YamlString(TEXT_CONTENT_NAME) -> YamlString(htmlCompTask.task.textContent.getOrElse("")),

        YamlString(ATTRS_NAME) -> yamlAttrs
      )
    }

    override def readObject(yamlObject: YamlObject): HtmlCompleteTask = {
      val taskId = yamlObject.intField(ID_NAME)
      val attributes = yamlObject.optArrayField(ATTRS_NAME, _ convertTo[Attribute] TaskAttributeYamlFormat(taskId, exerciseId))

      HtmlCompleteTask(
        HtmlTask(
          taskId, exerciseId,
          text = yamlObject.stringField(TEXT_NAME),
          xpathQuery = yamlObject.stringField(XPATH_NAME),
          textContent = yamlObject.optForgivingStringField(TEXT_CONTENT_NAME)),
        attributes)
    }
  }

  case class TaskAttributeYamlFormat(taskId: Int, exerciseId: Int) extends MyYamlFormat[Attribute] {

    override def readObject(yamlObject: YamlObject): Attribute = Attribute(yamlObject.stringField(KEY_NAME), taskId, exerciseId, yamlObject.stringField(VALUE_NAME))

    override def write(attr: Attribute): YamlValue = YamlObject(YamlString(KEY_NAME) -> attr.key, YamlString(VALUE_NAME) -> attr.value)

  }

  case class JsCompleteTaskYamlFormat(exerciseId: Int) extends MyYamlFormat[JsCompleteTask] {

    override def write(jsTask: JsCompleteTask): YamlValue = {
      val yamlConds = YamlArray(jsTask.conditions map (_ toYaml JsConditionYamlFormat(jsTask.task.id, jsTask.task.exerciseId)) toVector)
      YamlObject(
        YamlString(ID_NAME) -> jsTask.task.id,
        YamlString(TEXT_NAME) -> jsTask.task.text,
        YamlString(XPATH_NAME) -> jsTask.task.xpathQuery,
        YamlString(ACTION_TYPE_NAME) -> jsTask.task.actionType.name,
        YamlString(KEYS_TO_SEND_NAME) -> jsTask.task.keysToSend.map(YamlString).getOrElse(YamlNull),

        YamlString(CONDITIONS_NAME) -> yamlConds
      )
    }

    override def readObject(yamlObject: YamlObject): JsCompleteTask = {
      val taskId = yamlObject.intField(ID_NAME)
      JsCompleteTask(
        JsTask(
          taskId, exerciseId: Int,
          yamlObject.stringField(TEXT_NAME),
          yamlObject.stringField(XPATH_NAME),
          yamlObject.enumField(ACTION_TYPE_NAME, JsActionType.valueOf, JsActionType.CLICK),
          yamlObject.optStringField(KEYS_TO_SEND_NAME)),
        yamlObject.arrayField(CONDITIONS_NAME, _ convertTo[JsCondition] JsConditionYamlFormat(taskId, exerciseId))
      )
    }

  }

  case class JsConditionYamlFormat(taskId: Int, exerciseId: Int) extends MyYamlFormat[JsCondition] {

    override def readObject(yamlObject: YamlObject): JsCondition = JsCondition(
      yamlObject.intField(ID_NAME), taskId, exerciseId,
      yamlObject.stringField(XPATH_NAME),
      yamlObject.boolField(IS_PRECOND_NAME),
      yamlObject.forgivingStringField(AWAITED_VALUE_NAME)
    )

    override def write(jsCond: JsCondition): YamlValue = YamlObject(
      YamlString(ID_NAME) -> jsCond.id,
      YamlString(XPATH_NAME) -> jsCond.xpathQuery,
      YamlString(IS_PRECOND_NAME) -> jsCond.isPrecondition,
      YamlString(AWAITED_VALUE_NAME) -> jsCond.awaitedValue
    )

  }

}