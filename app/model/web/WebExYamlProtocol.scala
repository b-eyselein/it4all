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

  implicit object WebExYamlFormat extends MyYamlObjectFormat[WebCompleteEx] {

    override protected def readObject(yamlObject: YamlObject): Try[WebCompleteEx] = yamlObject.optForgivingStringField("extern") match {
      case None           => readRest(yamlObject)
      case Some(fileName) => readExtern(fileName)
    }

    def readRest(yamlObject: YamlObject): Try[WebCompleteEx] = for {
      id <- yamlObject.intField(idName)
      title <- yamlObject.stringField(titleName)
      author <- yamlObject.stringField(authorName)
      text <- yamlObject.stringField(textName)
      state <- yamlObject.enumField(stateName, ExerciseState.byString(_) getOrElse ExerciseState.CREATED)

      htmlTaskTries <- yamlObject.optArrayField(HTML_TASKS_NAME, HtmlCompleteTaskYamlFormat(id).read)
      jsTaskTries <- yamlObject.optArrayField(JS_TASKS_NAME, JsCompleteTaskYamlFormat(id).read)
      phpTasksTries <- yamlObject.optArrayField(PHP_TASKS_NAME, PhpCompleteTaskYamlFormat(id).read)
    } yield {
      val htmlText = yamlObject.optStringField(HTML_TEXT_NAME) map (_ getOrElse "")
      val jsText = yamlObject.optStringField(JS_TEXT_NAME) map (_ getOrElse "")
      val phpText = yamlObject.optStringField(PHP_TEXT_NAME) map (_ getOrElse "")

      for (htmlTaskFailure <- htmlTaskTries._2)
      // FIXME: return...
        Logger.error("Could not read html task", htmlTaskFailure.exception)

      for (jsTaskFailure <- jsTaskTries._2)
      // FIXME: return...
        Logger.error("Could not read js task", jsTaskFailure.exception)

      for (phpTaskFailure <- phpTasksTries._2)
      // FIXME: return...
        Logger.error("Could not read php task", phpTaskFailure.exception)

      WebCompleteEx(
        WebExercise(id, title, author, text, state, htmlText, jsText, phpText),
        htmlTaskTries._1, jsTaskTries._1, phpTasksTries._1
      )
    }

    def readExtern(fileName: String): Try[WebCompleteEx] = {
      println(fileName)
      ???
    }

    protected def writeRest(completeEx: WebCompleteEx): Map[YamlValue, YamlValue] = {

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


    override def write(obj: WebCompleteEx): YamlValue = ???
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
          YamlString(textName) -> htmlCompTask.task.text,
          YamlString(XPATH_NAME) -> htmlCompTask.task.xpathQuery
        ) ++ tcOpt ++ yamlAttrs
      )
    }

    override def readObject(yamlObject: YamlObject): Try[HtmlCompleteTask] = for {
      taskId <- yamlObject.intField(idName)
      text <- yamlObject.stringField(textName)
      xpathQuery <- yamlObject.stringField(XPATH_NAME)
      attributeTries <- yamlObject.optArrayField(attrsName, TaskAttributeYamlFormat(taskId, exerciseId).read)
    } yield {

      val textContent: Option[String] = yamlObject.optForgivingStringField(TEXT_CONTENT_NAME)

      for (attributeFailure <- attributeTries._2)
      // FIXME: return...
        Logger.error("Could not read html attribute", attributeFailure.exception)

      HtmlCompleteTask(HtmlTask(taskId, exerciseId, text, xpathQuery, textContent), attributeTries._1)
    }
  }

  case class TaskAttributeYamlFormat(taskId: Int, exerciseId: Int) extends MyYamlObjectFormat[Attribute] {

    override def readObject(yamlObject: YamlObject): Try[Attribute] = for {
      key <- yamlObject.stringField(keyName)
      value <- yamlObject.stringField(valueName)
    } yield Attribute(key, taskId, exerciseId, value)

    override def write(attr: Attribute): YamlValue = YamlObj(keyName -> attr.key, valueName -> attr.value)

  }

  case class JsCompleteTaskYamlFormat(exerciseId: Int) extends MyYamlObjectFormat[JsCompleteTask] {

    override def write(jsTask: JsCompleteTask): YamlValue = {
      val yamlConds = YamlArr(jsTask.conditions map JsConditionYamlFormat(jsTask.task.id, jsTask.task.exerciseId).write)

      YamlObj(
        idName -> jsTask.task.id,
        textName -> jsTask.task.text,
        XPATH_NAME -> jsTask.task.xpathQuery,
        ACTION_TYPE_NAME -> jsTask.task.actionType.name,
        KEYS_TO_SEND_NAME -> jsTask.task.keysToSend.map(YamlString).getOrElse(YamlNull),
        CONDITIONS_NAME -> yamlConds
      )
    }

    override def readObject(yamlObject: YamlObject): Try[JsCompleteTask] = for {
      taskId <- yamlObject.intField(idName)
      text <- yamlObject.stringField(textName)
      xpathQuery <- yamlObject.stringField(XPATH_NAME)
      actionType <- yamlObject.enumField(ACTION_TYPE_NAME, JsActionType.valueOf)
      conditionTries <- yamlObject.arrayField(CONDITIONS_NAME, JsConditionYamlFormat(taskId, exerciseId).read)
    } yield {
      val keysToSend = yamlObject.optForgivingStringField(KEYS_TO_SEND_NAME)

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

  case class PhpCompleteTaskYamlFormat(exerciseId: Int) extends MyYamlObjectFormat[PHPCompleteTask] {

    override protected def readObject(yamlObject: YamlObject): Try[PHPCompleteTask] = ???

    override def write(obj: PHPCompleteTask): YamlValue = ???

  }

}