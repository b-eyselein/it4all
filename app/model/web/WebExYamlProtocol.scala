package model.web

import model.MyYamlProtocol._
import model._
import model.web.WebConsts._
import net.jcazevedo.moultingyaml._
import play.api.Logger

import scala.language.{implicitConversions, postfixOps}
import scala.util.{Success, Try}

object WebExYamlProtocol extends MyYamlProtocol {

  implicit object WebExYamlFormat extends MyYamlObjectFormat[WebCompleteEx] {

    override protected def readObject(yamlObject: YamlObject): Try[WebCompleteEx] = yamlObject.optField("extern", str => Success(str.forgivingStr)) match {
      case Success(Some(fileName)) => readExtern(fileName)
      case _                       => readRest(yamlObject)
    }

    def readRest(yamlObject: YamlObject): Try[WebCompleteEx] = for {
      baseValues <- readBaseValues(yamlObject)

      htmlText <- yamlObject.optStringField(htmlTextName)
      jsText <- yamlObject.optStringField(jsTextName)
      phpText <- yamlObject.optStringField(phpTextName)

      htmlTaskTries <- yamlObject.optArrayField(htmlTasksName, HtmlCompleteTaskYamlFormat(baseValues).read)
      jsTaskTries <- yamlObject.optArrayField(jsTasksName, JsCompleteTaskYamlFormat(baseValues).read)
      phpTasksTries <- yamlObject.optArrayField(phpTasksName, PhpCompleteTaskYamlFormat(baseValues).read)
    } yield {

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
        WebExercise(baseValues.id, baseValues.semanticVersion, baseValues.title, baseValues.author, baseValues.text, baseValues.state, htmlText, jsText, phpText),
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
        case hts: Seq[HtmlCompleteTask] => Some(YamlString(htmlTasksName) -> YamlArr(hts map HtmlCompleteTaskYamlFormat(completeEx.ex.baseValues).write))
      }

      val jsTasks: Option[(YamlValue, YamlValue)] = completeEx.jsTasks match {
        case Nil => None
        case jts => Some(YamlString(jsTasksName) -> YamlArr(jts map JsCompleteTaskYamlFormat(completeEx.ex.baseValues).write))
      }

      Map.empty ++
        (completeEx.ex.htmlText map (t => YamlString(htmlTextName) -> YamlString(t))) ++
        (completeEx.ex.jsText map (t => YamlString(jsTextName) -> YamlString(t))) ++
        htmlTasks ++ jsTasks

    }


    override def write(obj: WebCompleteEx): YamlValue = ???
  }

  case class HtmlCompleteTaskYamlFormat(baseValues: BaseValues) extends MyYamlObjectFormat[HtmlCompleteTask] {

    override def write(htmlCompTask: HtmlCompleteTask): YamlValue = {
      val yamlAttrs: Option[(YamlString, YamlArray)] = htmlCompTask.attributes match {
        case Nil   => None
        case attrs => Some(YamlString(attributesName) -> YamlArr(attrs map TaskAttributeYamlFormat(htmlCompTask.task.id, baseValues).write))
      }

      val tcOpt: Option[(YamlValue, YamlValue)] = htmlCompTask.task.textContent map (tc => YamlString(textContentName) -> YamlString(tc))

      new YamlObject(
        Map[YamlValue, YamlValue](
          YamlString(idName) -> htmlCompTask.task.id,
          YamlString(textName) -> htmlCompTask.task.text,
          YamlString(xpathQueryName) -> htmlCompTask.task.xpathQuery
        ) ++ tcOpt ++ yamlAttrs
      )
    }

    override def readObject(yamlObject: YamlObject): Try[HtmlCompleteTask] = for {
      taskId <- yamlObject.intField(idName)
      text <- yamlObject.stringField(textName)
      xpathQuery <- yamlObject.stringField(xpathQueryName)
      textContent <- yamlObject.optField(textContentName, str => Success(str.forgivingStr))
      attributeTries <- yamlObject.optArrayField(attributesName, TaskAttributeYamlFormat(taskId, baseValues).read)
    } yield {


      for (attributeFailure <- attributeTries._2)
      // FIXME: return...
        Logger.error("Could not read html attribute", attributeFailure.exception)

      HtmlCompleteTask(HtmlTask(taskId, baseValues.id, baseValues.semanticVersion, text, xpathQuery, textContent), attributeTries._1)
    }
  }

  case class TaskAttributeYamlFormat(taskId: Int, baseValues: BaseValues) extends MyYamlObjectFormat[Attribute] {

    override def readObject(yamlObject: YamlObject): Try[Attribute] = for {
      key <- yamlObject.stringField(keyName)
      value <- yamlObject.stringField(valueName)
    } yield Attribute(key, taskId, baseValues.id, baseValues.semanticVersion, value)

    override def write(attr: Attribute): YamlValue = YamlObj(keyName -> attr.key, valueName -> attr.value)

  }

  case class JsCompleteTaskYamlFormat(baseValues: BaseValues) extends MyYamlObjectFormat[JsCompleteTask] {

    override def write(jsTask: JsCompleteTask): YamlValue = {
      val yamlConds = YamlArr(jsTask.conditions map JsConditionYamlFormat(jsTask.task.id, baseValues).write)

      YamlObj(
        idName -> jsTask.task.id,
        textName -> jsTask.task.text,
        xpathQueryName -> jsTask.task.xpathQuery,
        actionTypeName -> jsTask.task.actionType.entryName,
        KEYS_TO_SEND_NAME -> jsTask.task.keysToSend.map(YamlString).getOrElse(YamlNull),
        conditionsName -> yamlConds
      )
    }

    override def readObject(yamlObject: YamlObject): Try[JsCompleteTask] = for {
      taskId <- yamlObject.intField(idName)
      text <- yamlObject.stringField(textName)
      xpathQuery <- yamlObject.stringField(xpathQueryName)
      actionType <- yamlObject.enumField(actionTypeName, JsActionType.withNameInsensitiveOption) map (_ getOrElse JsActionType.CLICK)
      keysToSend <- yamlObject.optField(KEYS_TO_SEND_NAME, str => Success(str.forgivingStr))
      conditionTries <- yamlObject.arrayField(conditionsName, JsConditionYamlFormat(taskId, baseValues).read)
    } yield {

      for (conditionFailure <- conditionTries._2)
      // FIXME: return...
        Logger.error("Could not read js condition", conditionFailure.exception)

      JsCompleteTask(JsTask(taskId, baseValues.id, baseValues.semanticVersion, text, xpathQuery, actionType, keysToSend), conditionTries._1)
    }

  }

  case class JsConditionYamlFormat(taskId: Int, baseValues: BaseValues) extends MyYamlObjectFormat[JsCondition] {

    override def readObject(yamlObject: YamlObject): Try[JsCondition] = for {
      id <- yamlObject.intField(idName)
      xpathQuery <- yamlObject.stringField(xpathQueryName)
      isPrecondition <- yamlObject.boolField(IS_PRECOND_NAME)
      awaitedValue <- yamlObject.forgivingStringField(awaitedName)
    } yield JsCondition(id, taskId, baseValues.id, baseValues.semanticVersion, xpathQuery, isPrecondition, awaitedValue)

    override def write(jsCond: JsCondition): YamlValue = YamlObj(
      idName -> jsCond.id,
      xpathQueryName -> jsCond.xpathQuery,
      IS_PRECOND_NAME -> jsCond.isPrecondition,
      awaitedName -> jsCond.awaitedValue
    )

  }

  case class PhpCompleteTaskYamlFormat(baseValues: BaseValues) extends MyYamlObjectFormat[PHPCompleteTask] {

    override protected def readObject(yamlObject: YamlObject): Try[PHPCompleteTask] = ???

    override def write(obj: PHPCompleteTask): YamlValue = ???

  }

}