package model.web

import model.Enums.ExerciseState
import model.MyYamlProtocol._
import model.web.WebConsts._
import model.web.WebEnums._
import model.{MyYamlProtocol, YamlArr, YamlObj}
import net.jcazevedo.moultingyaml._
import play.api.Logger

import scala.language.{implicitConversions, postfixOps}
import scala.util.Try

object WebExYamlProtocol extends MyYamlProtocol {

  implicit object WebExYamlFormat extends HasBaseValuesYamlFormat[WebCompleteEx] {

    override def readRest(yamlObject: YamlObject, baseValues: (Int, String, String, String, ExerciseState)): Try[WebCompleteEx] = for {
      htmlText <- yamlObject.optStringField(HTML_TEXT_NAME)
      jsText <- yamlObject.optStringField(JS_TEXT_NAME)
      htmlTaskTries <- yamlObject.optArrayField(HTML_TASKS_NAME, HtmlCompleteTaskYamlFormat(baseValues._1).read)
      jsTaskTries <- yamlObject.optArrayField(JS_TASKS_NAME, JsCompleteTaskYamlFormat(baseValues._1).read)
    } yield {
      for (htmlTaskFailure <- htmlTaskTries._2)
      // FIXME: return...
        Logger.error("Could not read html task", htmlTaskFailure.exception)

      for (jsTaskFailure <- jsTaskTries._2)
      // FIXME: return...
        Logger.error("Could not read js task", jsTaskFailure.exception)

      WebCompleteEx(new WebExercise(baseValues, htmlText, htmlTaskTries._1.nonEmpty, jsText, jsTaskTries._1.nonEmpty), htmlTaskTries._1, jsTaskTries._1)
    }

    override protected def writeRest(completeEx: WebCompleteEx): Map[YamlValue, YamlValue] = {

      val htmlTasks: Option[(YamlValue, YamlValue)] = completeEx.htmlTasks match {
        case Nil                        => None
        case hts: Seq[HtmlCompleteTask] => Some(YamlString(HTML_TASKS_NAME) -> YamlArr(hts map HtmlCompleteTaskYamlFormat(completeEx.ex.id).write))
      }

      val jsTasks: Option[(YamlValue, YamlValue)] = completeEx.jsTasks match {
        case Nil => None
        case jts => Some(YamlString(JS_TASKS_NAME) -> YamlArr(jts map JsCompleteTaskYamlFormat(completeEx.ex.id).write))
      }

      Map.empty ++
        (completeEx.ex.htmlText map (t => YamlString(HTML_TEXT_NAME) -> YamlString(t))) ++
        (completeEx.ex.jsText map (t => YamlString(JS_TEXT_NAME) -> YamlString(t))) ++
        htmlTasks ++ jsTasks

    }
  }

  case class HtmlCompleteTaskYamlFormat(exerciseId: Int) extends MyYamlObjectFormat[HtmlCompleteTask] {

    override def write(htmlCompTask: HtmlCompleteTask): YamlValue = {
      val yamlAttrs: Option[(YamlString, YamlArray)] = htmlCompTask.attributes match {
        case Nil   => None
        case attrs => Some(YamlString(attrsName) -> YamlArr(attrs map TaskAttributeYamlFormat(htmlCompTask.task.id, htmlCompTask.task.exerciseId).write))
      }

      val tcOpt: Option[(YamlValue, YamlValue)] = htmlCompTask.task.textContent map (tc => YamlString(TEXT_CONTENT_NAME) -> YamlString(tc))

      new YamlObject(
        Map[YamlValue, YamlValue](
          YamlString(idName) -> htmlCompTask.task.id,
          YamlString(TEXT_NAME) -> htmlCompTask.task.text,
          YamlString(XPATH_NAME) -> htmlCompTask.task.xpathQuery
        ) ++ tcOpt ++ yamlAttrs
      )
    }

    override def readObject(yamlObject: YamlObject): Try[HtmlCompleteTask] = for {
      taskId <- yamlObject.intField(idName)
      text <- yamlObject.stringField(TEXT_NAME)
      xpathQuery <- yamlObject.stringField(XPATH_NAME)
      textContent <- yamlObject.optForgivingStringField(TEXT_CONTENT_NAME)
      attributeTries <- yamlObject.optArrayField(attrsName, TaskAttributeYamlFormat(taskId, exerciseId).read)
    } yield {
      for (attributeFailure <- attributeTries._2)
      // FIXME: return...
        Logger.error("Could not read html attribute", attributeFailure.exception)

      HtmlCompleteTask(HtmlTask(taskId, exerciseId, text, xpathQuery, textContent), attributeTries._1)
    }
  }

  case class TaskAttributeYamlFormat(taskId: Int, exerciseId: Int) extends MyYamlObjectFormat[Attribute] {

    override def readObject(yamlObject: YamlObject): Try[Attribute] = for {
      key <- yamlObject.stringField(keyName)
      value <- yamlObject.stringField(VALUE_NAME)
    } yield Attribute(key, taskId, exerciseId, value)

    override def write(attr: Attribute): YamlValue = YamlObj(keyName -> attr.key, VALUE_NAME -> attr.value)

  }

  case class JsCompleteTaskYamlFormat(exerciseId: Int) extends MyYamlObjectFormat[JsCompleteTask] {

    override def write(jsTask: JsCompleteTask): YamlValue = {
      val yamlConds = YamlArr(jsTask.conditions map JsConditionYamlFormat(jsTask.task.id, jsTask.task.exerciseId).write)

      YamlObj(
        idName -> jsTask.task.id,
        TEXT_NAME -> jsTask.task.text,
        XPATH_NAME -> jsTask.task.xpathQuery,
        ACTION_TYPE_NAME -> jsTask.task.actionType.name,
        KEYS_TO_SEND_NAME -> jsTask.task.keysToSend.map(YamlString).getOrElse(YamlNull),
        CONDITIONS_NAME -> yamlConds
      )
    }

    override def readObject(yamlObject: YamlObject): Try[JsCompleteTask] = for {
      taskId <- yamlObject.intField(idName)
      text <- yamlObject.stringField(TEXT_NAME)
      xpathQuery <- yamlObject.stringField(XPATH_NAME)
      actionType <- yamlObject.enumField(ACTION_TYPE_NAME, JsActionType.valueOf)
      keysToSend <- yamlObject.optForgivingStringField(KEYS_TO_SEND_NAME)
      conditionTries <- yamlObject.arrayField(CONDITIONS_NAME, JsConditionYamlFormat(taskId, exerciseId).read)
    } yield {
      for (conditionFailure <- conditionTries._2)
      // FIXME: return...
        Logger.error("Could not read js condition", conditionFailure.exception)

      JsCompleteTask(JsTask(taskId, exerciseId, text, xpathQuery, actionType, keysToSend), conditionTries._1)
    }

  }

  case class JsConditionYamlFormat(taskId: Int, exerciseId: Int) extends MyYamlObjectFormat[JsCondition] {

    override def readObject(yamlObject: YamlObject): Try[JsCondition] = for {
      id <- yamlObject.intField(idName)
      xpathQuery <- yamlObject.stringField(XPATH_NAME)
      isPrecondition <- yamlObject.boolField(IS_PRECOND_NAME)
      awaitedValue <- yamlObject.forgivingStringField(AWAITED_VALUE_NAME)
    } yield JsCondition(id, taskId, exerciseId, xpathQuery, isPrecondition, awaitedValue)

    override def write(jsCond: JsCondition): YamlValue = YamlObj(
      idName -> jsCond.id,
      XPATH_NAME -> jsCond.xpathQuery,
      IS_PRECOND_NAME -> jsCond.isPrecondition,
      AWAITED_VALUE_NAME -> jsCond.awaitedValue
    )

  }

}