package model.web

import model.ExerciseState
import model.MyYamlProtocol._
import model.web.WebConsts._
import model.web.WebEnums._
import model.{MyYamlProtocol, YamlArr, YamlObj}
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
      id <- yamlObject.intField(idName)
      title <- yamlObject.stringField(titleName)
      author <- yamlObject.stringField(authorName)
      text <- yamlObject.stringField(textName)
      state <- yamlObject.enumField(stateName, ExerciseState.withNameInsensitiveOption(_) getOrElse ExerciseState.CREATED)


      htmlText <- yamlObject.optStringField(htmlTextName)
      jsText <- yamlObject.optStringField(jsTextName)
      phpText <- yamlObject.optStringField(phpTextName)

      htmlTaskTries <- yamlObject.optArrayField(htmlTasksName, HtmlCompleteTaskYamlFormat(id).read)
      jsTaskTries <- yamlObject.optArrayField(jsTasksName, JsCompleteTaskYamlFormat(id).read)
      phpTasksTries <- yamlObject.optArrayField(phpTasksName, PhpCompleteTaskYamlFormat(id).read)
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
        case hts: Seq[HtmlCompleteTask] => Some(YamlString(htmlTasksName) -> YamlArr(hts map HtmlCompleteTaskYamlFormat(completeEx.ex.id).write))
      }

      val jsTasks: Option[(YamlValue, YamlValue)] = completeEx.jsTasks match {
        case Nil => None
        case jts => Some(YamlString(jsTasksName) -> YamlArr(jts map JsCompleteTaskYamlFormat(completeEx.ex.id).write))
      }

      Map.empty ++
        (completeEx.ex.htmlText map (t => YamlString(htmlTextName) -> YamlString(t))) ++
        (completeEx.ex.jsText map (t => YamlString(jsTextName) -> YamlString(t))) ++
        htmlTasks ++ jsTasks

    }


    override def write(obj: WebCompleteEx): YamlValue = ???
  }

  case class HtmlCompleteTaskYamlFormat(exerciseId: Int) extends MyYamlObjectFormat[HtmlCompleteTask] {

    override def write(htmlCompTask: HtmlCompleteTask): YamlValue = {
      val yamlAttrs: Option[(YamlString, YamlArray)] = htmlCompTask.attributes match {
        case Nil   => None
        case attrs => Some(YamlString(attributesName) -> YamlArr(attrs map TaskAttributeYamlFormat(htmlCompTask.task.id, htmlCompTask.task.exerciseId).write))
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
      attributeTries <- yamlObject.optArrayField(attributesName, TaskAttributeYamlFormat(taskId, exerciseId).read)
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
        xpathQueryName -> jsTask.task.xpathQuery,
        actionTypeName -> jsTask.task.actionType.name,
        KEYS_TO_SEND_NAME -> jsTask.task.keysToSend.map(YamlString).getOrElse(YamlNull),
        conditionsName -> yamlConds
      )
    }

    override def readObject(yamlObject: YamlObject): Try[JsCompleteTask] = for {
      taskId <- yamlObject.intField(idName)
      text <- yamlObject.stringField(textName)
      xpathQuery <- yamlObject.stringField(xpathQueryName)
      actionType <- yamlObject.enumField(actionTypeName, JsActionType.valueOf)
      keysToSend <- yamlObject.optField(KEYS_TO_SEND_NAME, str => Success(str.forgivingStr))
      conditionTries <- yamlObject.arrayField(conditionsName, JsConditionYamlFormat(taskId, exerciseId).read)
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
      xpathQuery <- yamlObject.stringField(xpathQueryName)
      isPrecondition <- yamlObject.boolField(IS_PRECOND_NAME)
      awaitedValue <- yamlObject.forgivingStringField(awaitedName)
    } yield JsCondition(id, taskId, exerciseId, xpathQuery, isPrecondition, awaitedValue)

    override def write(jsCond: JsCondition): YamlValue = YamlObj(
      idName -> jsCond.id,
      xpathQueryName -> jsCond.xpathQuery,
      IS_PRECOND_NAME -> jsCond.isPrecondition,
      awaitedName -> jsCond.awaitedValue
    )

  }

  case class PhpCompleteTaskYamlFormat(exerciseId: Int) extends MyYamlObjectFormat[PHPCompleteTask] {

    override protected def readObject(yamlObject: YamlObject): Try[PHPCompleteTask] = ???

    override def write(obj: PHPCompleteTask): YamlValue = ???

  }

}