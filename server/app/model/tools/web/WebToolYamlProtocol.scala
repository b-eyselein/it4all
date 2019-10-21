package model.tools.web

import de.uniwue.webtester._
import model.MyYamlProtocol._
import model._
import model.tools.web.WebConsts._
import net.jcazevedo.moultingyaml._
import play.api.Logger

import scala.language.{implicitConversions, postfixOps}
import scala.util.{Success, Try}

object WebToolYamlProtocol extends MyYamlProtocol {

  private val logger = Logger("model.tools.web.WebExYamlProtocol")


  object WebExYamlFormat extends MyYamlObjectFormat[WebExercise] {

    def readObject(yamlObject: YamlObject): Try[WebExercise] = for {
      baseValues <- readBaseValues(yamlObject)

      htmlText <- yamlObject.optStringField(htmlTextName)
      jsText <- yamlObject.optStringField(jsTextName)

      fileName <- yamlObject.stringField(filenameName)

      htmlTaskTries <- yamlObject.optArrayField(htmlTasksName, HtmlCompleteTaskYamlFormat.read)
      jsTaskTries <- yamlObject.optArrayField(jsTasksName, JsCompleteTaskYamlFormat.read)

      filesName <- yamlObject.arrayField("files", ExerciseFileYamlFormat.read)

      sampleSolutionTries <- yamlObject.arrayField(samplesName, WebSampleSolutionYamlFormat.read)
    } yield {

      for (htmlTaskFailure <- htmlTaskTries._2)
      // FIXME: return...
        logger.error("Could not read html task", htmlTaskFailure.exception)

      for (jsTaskFailure <- jsTaskTries._2)
      // FIXME: return...
        logger.error("Could not read js task", jsTaskFailure.exception)

      for (sampleSolFailure <- sampleSolutionTries._2)
        logger.error("Could not read web sample solution", sampleSolFailure.exception)

      WebExercise(
        baseValues.id, baseValues.semanticVersion, baseValues.title, baseValues.author, baseValues.text, baseValues.state,
        htmlText, jsText, SiteSpec(1, fileName, htmlTaskTries._1, jsTaskTries._1), filesName._1, sampleSolutionTries._1
      )
    }

    protected def writeRest(exercise: WebExercise): Map[YamlValue, YamlValue] = {

      val htmlTasks: Option[(YamlValue, YamlValue)] = exercise.siteSpec.htmlTasks match {
        case Nil                => None
        case hts: Seq[HtmlTask] => Some(YamlString(htmlTasksName) -> YamlArr(hts map HtmlCompleteTaskYamlFormat.write))
      }

      val jsTasks: Option[(YamlValue, YamlValue)] = exercise.siteSpec.jsTasks match {
        case Nil => None
        case jts => Some(YamlString(jsTasksName) -> YamlArr(jts map JsCompleteTaskYamlFormat.write))
      }

      Map[YamlValue, YamlValue]() ++
        (exercise.htmlText map (t => YamlString(htmlTextName) -> YamlString(t))).toList ++
        (exercise.jsText map (t => YamlString(jsTextName) -> YamlString(t))).toList ++
        htmlTasks.toList ++ jsTasks.toList

    }

    override def write(obj: WebExercise): YamlValue = ???

  }

  private object HtmlCompleteTaskYamlFormat extends MyYamlObjectFormat[HtmlTask] {

    override def readObject(yamlObject: YamlObject): Try[HtmlTask] = for {
      text <- yamlObject.stringField(textName)
      elementSpec <- yamlObject.objField(elementSpecName, HtmlElementSpecYamlFormat.read)
    } yield HtmlTask(text, elementSpec)

    override def write(htmlTask: HtmlTask): YamlValue = YamlObject(
      YamlString(textName) -> htmlTask.text,
      YamlString(elementSpecName) -> HtmlElementSpecYamlFormat.write(htmlTask.elementSpec),
    )

  }

  private object HtmlElementSpecYamlFormat extends MyYamlObjectFormat[HtmlElementSpec] {

    override protected def readObject(yamlObject: YamlObject): Try[HtmlElementSpec] = for {
      id <- yamlObject.intField(idName)
      xpathQuery <- yamlObject.stringField(xpathQueryName)
      awaitedTag <- yamlObject.stringField(awaitedTagName)
      awaitedTextContent <- yamlObject.optStringField(awaitedTextContentName)
      attributes <- yamlObject.optArrayField(attributesName, HtmlAttributeYamlFormat.read)
    } yield {
      for (attributeFailure <- attributes._2)
        logger.error("Error while reading html attribute", attributeFailure.exception)

      HtmlElementSpec(id, xpathQuery, awaitedTag, awaitedTextContent, attributes._1)
    }

    override def write(hes: HtmlElementSpec): YamlValue = YamlObj(
      idName -> hes.id,
      xpathQueryName -> hes.xpathQuery,
      awaitedTagName -> hes.awaitedTagName,
      awaitedName -> hes.awaitedTextContent.map[YamlValue](YamlString).getOrElse(YamlNull),
      attributesName -> YamlArray(hes.attributes.map(HtmlAttributeYamlFormat.write).toVector)
    )

  }

  private object HtmlAttributeYamlFormat extends MyYamlObjectFormat[HtmlAttribute] {

    override def readObject(yamlObject: YamlObject): Try[HtmlAttribute] = for {
      key <- yamlObject.stringField(keyName)
      value <- yamlObject.stringField(valueName)
    } yield HtmlAttribute(key, value)

    override def write(attr: HtmlAttribute): YamlValue = YamlObj(keyName -> attr.key, valueName -> attr.value)

  }

  private object JsCompleteTaskYamlFormat extends MyYamlObjectFormat[JsTask] {

    override def readObject(yamlObject: YamlObject): Try[JsTask] = for {
      taskId <- yamlObject.intField(idName)
      text <- yamlObject.stringField(textName)
      preConditionTries <- yamlObject.arrayField(preConditionsName, HtmlElementSpecYamlFormat.read)
      action <- yamlObject.objField(actionName, JsActionYamlFormat.read)
      postConditionTries <- yamlObject.arrayField(postConditionsName, HtmlElementSpecYamlFormat.read)
    } yield {

      for (preConditionFailure <- preConditionTries._2)
      // FIXME: return...
        logger.error("Could not read js condition", preConditionFailure.exception)


      for (postConditionFailure <- postConditionTries._2)
      // FIXME: return...
        logger.error("Could not read js condition", postConditionFailure.exception)

      JsTask(taskId, text, preConditionTries._1, action, postConditionTries._1)
    }

    override def write(jsTask: JsTask): YamlValue = YamlObj(
      idName -> jsTask.id,
      textName -> jsTask.text,
      preConditionsName -> YamlArray(jsTask.preConditions.map(HtmlElementSpecYamlFormat.write).toVector),
      actionName -> JsActionYamlFormat.write(jsTask.action),
      postConditionsName -> YamlArray(jsTask.postConditions.map(HtmlElementSpecYamlFormat.write).toVector)
    )

  }

  private object JsActionYamlFormat extends MyYamlObjectFormat[JsAction] {

    override protected def readObject(yamlObject: YamlObject): Try[JsAction] = for {
      xpathQuery <- yamlObject.stringField(xpathQueryName)
      actionType <- yamlObject.enumField(actionTypeName, JsActionType.withNameInsensitiveOption) map (_ getOrElse JsActionType.Click)
      keysToSend <- yamlObject.optField(keysToSendName, str => Success(str.forgivingStr))
    } yield JsAction(xpathQuery, actionType, keysToSend)

    override def write(action: JsAction): YamlValue = YamlObj(
      xpathQueryName -> action.xpathQuery,
      actionTypeName -> action.actionType.entryName,
      keysToSendName -> action.keysToSend.map[YamlValue](YamlString).getOrElse(YamlNull)
    )

  }

  private object WebSampleSolutionYamlFormat extends MyYamlObjectFormat[FilesSampleSolution] {

    override protected def readObject(yamlObject: YamlObject): Try[FilesSampleSolution] = for {
      id <- yamlObject.intField(idName)
      files <- yamlObject.arrayField(filesName, ExerciseFileYamlFormat.read)
    } yield {

      for (sampleFileReadError <- files._2)
        logger.error("Error while reading web sample", sampleFileReadError.exception)

      FilesSampleSolution(id, files._1)
    }

    override def write(obj: FilesSampleSolution): YamlValue = ???

  }

}