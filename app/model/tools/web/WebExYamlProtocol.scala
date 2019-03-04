package model.tools.web

import model.MyYamlProtocol._
import model._
import model.tools.web.WebConsts._
import net.jcazevedo.moultingyaml._
import play.api.Logger

import scala.language.{implicitConversions, postfixOps}
import scala.util.{Success, Try}

object WebExYamlProtocol extends MyYamlProtocol {

  private val logger = Logger("model.tools.web.WebExYamlProtocol")

  object WebCollectionYamlFormat extends MyYamlObjectFormat[WebCollection] {

    override protected def readObject(yamlObject: YamlObject): Try[WebCollection] = for {
      id <- yamlObject.intField(idName)
      title <- yamlObject.stringField(titleName)
      author <- yamlObject.stringField(authorName)
      text <- yamlObject.stringField(textName)
      state <- yamlObject.enumField(statusName, ExerciseState.withNameInsensitiveOption) map (_ getOrElse ExerciseState.CREATED)
      shortName <- yamlObject.stringField(shortNameName)
    } yield WebCollection(id, title, author, text, state, shortName)

    override def write(obj: WebCollection): YamlValue = ???

  }

  object WebExYamlFormat extends MyYamlObjectFormat[WebExercise] {

    override protected def readObject(yamlObject: YamlObject): Try[WebExercise] = yamlObject.optField("extern", str => Success(str.forgivingStr)) match {
      case Success(Some(fileName)) => readExtern(fileName)
      case _                       => readRest(yamlObject)
    }

    def readRest(yamlObject: YamlObject): Try[WebExercise] = for {
      baseValues <- readBaseValues(yamlObject)

      htmlText <- yamlObject.optStringField(htmlTextName)
      jsText <- yamlObject.optStringField(jsTextName)

      htmlTaskTries <- yamlObject.optArrayField(htmlTasksName, HtmlCompleteTaskYamlFormat.read)
      jsTaskTries <- yamlObject.optArrayField(jsTasksName, JsCompleteTaskYamlFormat.read)

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
        htmlText, jsText, htmlTaskTries._1, jsTaskTries._1, sampleSolutionTries._1
      )
    }

    def readExtern(fileName: String): Try[WebExercise] = {
      println(fileName)
      ???
    }

    protected def writeRest(exercise: WebExercise): Map[YamlValue, YamlValue] = {

      val htmlTasks: Option[(YamlValue, YamlValue)] = exercise.htmlTasks match {
        case Nil                => None
        case hts: Seq[HtmlTask] => Some(YamlString(htmlTasksName) -> YamlArr(hts map HtmlCompleteTaskYamlFormat.write))
      }

      val jsTasks: Option[(YamlValue, YamlValue)] = exercise.jsTasks match {
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

    override def write(htmlCompTask: HtmlTask): YamlValue = {
      val yamlAttrs: Option[(YamlString, YamlArray)] = htmlCompTask.attributes match {
        case Nil   => None
        case attrs => Some(YamlString(attributesName) -> YamlArr(attrs map HtmlAttributeYamlFormat.write))
      }

      val tcOpt: Option[(YamlValue, YamlValue)] = htmlCompTask.textContent map (tc => YamlString(textContentName) -> YamlString(tc))

      new YamlObject(
        Map[YamlValue, YamlValue](
          YamlString(idName) -> htmlCompTask.id,
          YamlString(textName) -> htmlCompTask.text,
          YamlString(xpathQueryName) -> htmlCompTask.xpathQuery
        ) ++ tcOpt.toList ++ yamlAttrs.toList
      )
    }

    override def readObject(yamlObject: YamlObject): Try[HtmlTask] = for {
      taskId <- yamlObject.intField(idName)
      text <- yamlObject.stringField(textName)
      xpathQuery <- yamlObject.stringField(xpathQueryName)
      textContent <- yamlObject.optField(textContentName, str => Success(str.forgivingStr))
      attributeTries <- yamlObject.optArrayField(attributesName, HtmlAttributeYamlFormat.read)
    } yield {


      for (attributeFailure <- attributeTries._2)
      // FIXME: return...
        logger.error("Could not read html attribute", attributeFailure.exception)

      HtmlTask(taskId, text, xpathQuery, textContent, attributeTries._1)
    }
  }

  private object HtmlAttributeYamlFormat extends MyYamlObjectFormat[HtmlAttribute] {

    override def readObject(yamlObject: YamlObject): Try[HtmlAttribute] = for {
      key <- yamlObject.stringField(keyName)
      value <- yamlObject.stringField(valueName)
    } yield HtmlAttribute(key, value)

    override def write(attr: HtmlAttribute): YamlValue = YamlObj(keyName -> attr.key, valueName -> attr.value)

  }

  private object JsCompleteTaskYamlFormat extends MyYamlObjectFormat[JsTask] {

    override def write(jsTask: JsTask): YamlValue = {
      val yamlConds = YamlArr(jsTask.conditions map JsConditionYamlFormat.write)

      YamlObj(
        idName -> jsTask.id,
        textName -> jsTask.text,
        xpathQueryName -> jsTask.xpathQuery,
        actionTypeName -> jsTask.actionType.entryName,
        keysToSendName -> jsTask.keysToSend.map[YamlValue](YamlString).getOrElse(YamlNull),
        conditionsName -> yamlConds
      )
    }

    override def readObject(yamlObject: YamlObject): Try[JsTask] = for {
      taskId <- yamlObject.intField(idName)
      text <- yamlObject.stringField(textName)
      xpathQuery <- yamlObject.stringField(xpathQueryName)
      actionType <- yamlObject.enumField(actionTypeName, JsActionType.withNameInsensitiveOption) map (_ getOrElse JsActionType.CLICK)
      keysToSend <- yamlObject.optField(keysToSendName, str => Success(str.forgivingStr))
      conditionTries <- yamlObject.arrayField(conditionsName, JsConditionYamlFormat.read)
    } yield {

      for (conditionFailure <- conditionTries._2)
      // FIXME: return...
        logger.error("Could not read js condition", conditionFailure.exception)

      JsTask(taskId, text, xpathQuery, actionType, keysToSend, conditionTries._1)
    }

  }

  private object JsConditionYamlFormat extends MyYamlObjectFormat[JsCondition] {

    override protected def readObject(yamlObject: YamlObject): Try[JsCondition] = for {
      id <- yamlObject.intField(idName)
      xpathQuery <- yamlObject.stringField(xpathQueryName)
      isPrecondition <- yamlObject.boolField(IS_PRECOND_NAME)
      awaitedValue <- yamlObject.forgivingStringField(awaitedName)
    } yield JsCondition(id, xpathQuery, isPrecondition, awaitedValue)

    override def write(jsCond: JsCondition): YamlValue = YamlObj(
      idName -> jsCond.id,
      xpathQueryName -> jsCond.xpathQuery,
      IS_PRECOND_NAME -> jsCond.isPrecondition,
      awaitedName -> jsCond.awaitedValue
    )

  }

  private object WebSampleSolutionYamlFormat extends MyYamlObjectFormat[WebSampleSolution] {

    override protected def readObject(yamlObject: YamlObject): Try[WebSampleSolution] = for {
      id <- yamlObject.intField(idName)
      htmlSample <- yamlObject.stringField(htmlSampleName)
      jsSample <- yamlObject.optStringField(jsSampleName)
    } yield WebSampleSolution(id, WebSolution(htmlSample, jsSample))

    override def write(obj: WebSampleSolution): YamlValue = ???

  }

}
