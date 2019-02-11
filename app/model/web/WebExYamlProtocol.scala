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

      htmlTaskTries <- yamlObject.optArrayField(htmlTasksName, HtmlCompleteTaskYamlFormat(baseValues.id, baseValues.semanticVersion).read)
      jsTaskTries <- yamlObject.optArrayField(jsTasksName, JsCompleteTaskYamlFormat(baseValues.id, baseValues.semanticVersion).read)

      sampleSolutionTries <- yamlObject.arrayField(samplesName, WebSampleSolutionYamlFormat(baseValues.id, baseValues.semanticVersion).read)
    } yield {

      for (htmlTaskFailure <- htmlTaskTries._2)
      // FIXME: return...
        Logger.error("Could not read html task", htmlTaskFailure.exception)

      for (jsTaskFailure <- jsTaskTries._2)
      // FIXME: return...
        Logger.error("Could not read js task", jsTaskFailure.exception)

      WebCompleteEx(
        WebExercise(baseValues.id, baseValues.semanticVersion, baseValues.title, baseValues.author, baseValues.text, baseValues.state, htmlText, jsText),
        htmlTaskTries._1, jsTaskTries._1,
        sampleSolutionTries._1
      )
    }

    def readExtern(fileName: String): Try[WebCompleteEx] = {
      println(fileName)
      ???
    }

    protected def writeRest(completeEx: WebCompleteEx): Map[YamlValue, YamlValue] = {

      val htmlCompleteTaskYamlFormat = HtmlCompleteTaskYamlFormat(completeEx.ex.id, completeEx.ex.semanticVersion)
      val jsCompleteTaskYamlFormat = JsCompleteTaskYamlFormat(completeEx.ex.id, completeEx.ex.semanticVersion)

      val htmlTasks: Option[(YamlValue, YamlValue)] = completeEx.htmlTasks match {
        case Nil                        => None
        case hts: Seq[HtmlCompleteTask] => Some(YamlString(htmlTasksName) -> YamlArr(hts map htmlCompleteTaskYamlFormat.write))
      }

      val jsTasks: Option[(YamlValue, YamlValue)] = completeEx.jsTasks match {
        case Nil => None
        case jts => Some(YamlString(jsTasksName) -> YamlArr(jts map jsCompleteTaskYamlFormat.write))
      }

      Map[YamlValue, YamlValue]() ++
        (completeEx.ex.htmlText map (t => YamlString(htmlTextName) -> YamlString(t))).toList ++
        (completeEx.ex.jsText map (t => YamlString(jsTextName) -> YamlString(t))).toList ++
        htmlTasks.toList ++ jsTasks.toList

    }


    override def write(obj: WebCompleteEx): YamlValue = ???
  }

  final case class HtmlCompleteTaskYamlFormat(exId: Int, exSemVer: SemanticVersion) extends MyYamlObjectFormat[HtmlCompleteTask] {

    override def write(htmlCompTask: HtmlCompleteTask): YamlValue = {
      val yamlAttrs: Option[(YamlString, YamlArray)] = htmlCompTask.attributes match {
        case Nil   => None
        case attrs => Some(YamlString(attributesName) -> YamlArr(attrs map TaskAttributeYamlFormat(htmlCompTask.task.id, exId, exSemVer).write))
      }

      val tcOpt: Option[(YamlValue, YamlValue)] = htmlCompTask.task.textContent map (tc => YamlString(textContentName) -> YamlString(tc))

      new YamlObject(
        Map[YamlValue, YamlValue](
          YamlString(idName) -> htmlCompTask.task.id,
          YamlString(textName) -> htmlCompTask.task.text,
          YamlString(xpathQueryName) -> htmlCompTask.task.xpathQuery
        ) ++ tcOpt.toList ++ yamlAttrs.toList
      )
    }

    override def readObject(yamlObject: YamlObject): Try[HtmlCompleteTask] = for {
      taskId <- yamlObject.intField(idName)
      text <- yamlObject.stringField(textName)
      xpathQuery <- yamlObject.stringField(xpathQueryName)
      textContent <- yamlObject.optField(textContentName, str => Success(str.forgivingStr))
      attributeTries <- yamlObject.optArrayField(attributesName, TaskAttributeYamlFormat(taskId, exId, exSemVer).read)
    } yield {


      for (attributeFailure <- attributeTries._2)
      // FIXME: return...
        Logger.error("Could not read html attribute", attributeFailure.exception)

      HtmlCompleteTask(HtmlTask(taskId, exId, exSemVer, text, xpathQuery, textContent), attributeTries._1)
    }
  }

  final case class TaskAttributeYamlFormat(taskId: Int, exId: Int, exSemVer: SemanticVersion) extends MyYamlObjectFormat[Attribute] {

    override def readObject(yamlObject: YamlObject): Try[Attribute] = for {
      key <- yamlObject.stringField(keyName)
      value <- yamlObject.stringField(valueName)
    } yield Attribute(key, taskId, exId, exSemVer, value)

    override def write(attr: Attribute): YamlValue = YamlObj(keyName -> attr.key, valueName -> attr.value)

  }

  final case class JsCompleteTaskYamlFormat(exId: Int, exSemVer: SemanticVersion) extends MyYamlObjectFormat[JsCompleteTask] {

    override def write(jsTask: JsCompleteTask): YamlValue = {
      val yamlConds = YamlArr(jsTask.conditions map JsConditionYamlFormat(jsTask.task.id, exId, exSemVer).write)

      YamlObj(
        idName -> jsTask.task.id,
        textName -> jsTask.task.text,
        xpathQueryName -> jsTask.task.xpathQuery,
        actionTypeName -> jsTask.task.actionType.entryName,
        keysToSendName -> jsTask.task.keysToSend.map[YamlValue](YamlString).getOrElse(YamlNull),
        conditionsName -> yamlConds
      )
    }

    override def readObject(yamlObject: YamlObject): Try[JsCompleteTask] = for {
      taskId <- yamlObject.intField(idName)
      text <- yamlObject.stringField(textName)
      xpathQuery <- yamlObject.stringField(xpathQueryName)
      actionType <- yamlObject.enumField(actionTypeName, JsActionType.withNameInsensitiveOption) map (_ getOrElse JsActionType.CLICK)
      keysToSend <- yamlObject.optField(keysToSendName, str => Success(str.forgivingStr))
      conditionTries <- yamlObject.arrayField(conditionsName, JsConditionYamlFormat(taskId, exId, exSemVer).read)
    } yield {

      for (conditionFailure <- conditionTries._2)
      // FIXME: return...
        Logger.error("Could not read js condition", conditionFailure.exception)

      JsCompleteTask(JsTask(taskId, exId, exSemVer, text, xpathQuery, actionType, keysToSend), conditionTries._1)
    }

  }

  final case class JsConditionYamlFormat(taskId: Int, exId: Int, exSemVer: SemanticVersion) extends MyYamlObjectFormat[JsCondition] {

    override def readObject(yamlObject: YamlObject): Try[JsCondition] = for {
      id <- yamlObject.intField(idName)
      xpathQuery <- yamlObject.stringField(xpathQueryName)
      isPrecondition <- yamlObject.boolField(IS_PRECOND_NAME)
      awaitedValue <- yamlObject.forgivingStringField(awaitedName)
    } yield JsCondition(id, taskId, exId, exSemVer, xpathQuery, isPrecondition, awaitedValue)

    override def write(jsCond: JsCondition): YamlValue = YamlObj(
      idName -> jsCond.id,
      xpathQueryName -> jsCond.xpathQuery,
      IS_PRECOND_NAME -> jsCond.isPrecondition,
      awaitedName -> jsCond.awaitedValue
    )

  }

  final case class WebSampleSolutionYamlFormat(exId: Int, exSemVer: SemanticVersion) extends MyYamlObjectFormat[WebSampleSolution] {

    override protected def readObject(yamlObject: YamlObject): Try[WebSampleSolution] = for {
      id <- yamlObject.intField(idName)
      htmlSample <- yamlObject.optStringField(htmlSampleName)
      jsSample <- yamlObject.optStringField(jsSampleName)
    } yield WebSampleSolution(id, exId, exSemVer, htmlSample, jsSample)

    override def write(obj: WebSampleSolution): YamlValue = ???

  }

}