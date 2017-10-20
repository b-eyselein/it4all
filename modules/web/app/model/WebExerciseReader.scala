package model

import java.util.Collections

import com.fasterxml.jackson.databind.JsonNode
import model.StringConsts._
import model.exercisereading.{ExerciseReader, JsonReader}
import model.task._
import play.libs.Json

import scala.collection.JavaConverters._

object WebExerciseReader extends ExerciseReader[WebExercise]("web", WebExercise.finder, classOf[Array[WebExercise]]) {

  private def readCondition(conditionNode: JsonNode): Condition = {
    val key = Json.fromJson(conditionNode.get(KEY_NAME), classOf[JsConditionKey])
    val condition = Option(Condition.finder.byId(key)).getOrElse(new Condition(key))

    condition.xpathQuery = conditionNode.get(XPATH_NAME).asText
    condition.awaitedValue = conditionNode.get(AWAITED_VALUE_NAME).asText
    condition.isPrecondition = conditionNode.get("isPrecondition").asBoolean

    condition
  }

  private def readHtmlTask(htmlTaskNode: JsonNode): HtmlTask = {
    val key = Json.fromJson(htmlTaskNode.get(KEY_NAME), classOf[WebTaskKey])
    val task = Option(HtmlTask.finder.byId(key)).getOrElse(new HtmlTask(key))

    task.text = JsonReader.readAndJoinTextArray(htmlTaskNode.get(TEXT_NAME))
    task.xpathQuery = htmlTaskNode.get(XPATH_NAME).asText
    task.attributes = readAttributes(htmlTaskNode.get(ATTRS_NAME))
    task.textContent = htmlTaskNode.get(TEXT_CONTENT_NAME).asText

    task
  }

  private def readJsTask(jsTaskNode: JsonNode): JsWebTask = {
    val key = Json.fromJson(jsTaskNode.get(KEY_NAME), classOf[WebTaskKey])

    val task = Option(JsWebTask.finder.byId(key)).getOrElse(new JsWebTask(key))

    task.text = JsonReader.readAndJoinTextArray(jsTaskNode.get(TEXT_NAME))
    task.xpathQuery = jsTaskNode.get(XPATH_NAME).asText

    task.action = Option(jsTaskNode.get("action")) match {
      case None => null
      case Some(actionNode) => Json.fromJson(actionNode, classOf[Action])
    }

    task.conditions = Option(jsTaskNode.get("conditions")) match {
      case None => Collections.emptyList()
      case Some(conditionsNode) => ExerciseReader.readArray(conditionsNode, readCondition).asJava
    }

    task
  }

  private def readAttribute(attrNode: JsonNode): Attribute = new Attribute(attrNode.get("key").asText, attrNode.get("value").asText)

  private def readAttributes(attributesNode: JsonNode): String = {
    ExerciseReader.readArray(attributesNode, readAttribute).map(_.forDB).mkString(HtmlTask.ATTRS_JOIN_STR)
  }

//  override def initRemainingExFromForm(exercise: WebExercise, form: DynamicForm) {
  //    exercise.htmlText = form.get(HTML_TEXT_NAME)
  //    exercise.jsText = form.get(JS_TEXT_NAME)
  //  }

  override def instantiate(id: Int) = new WebExercise(id)

  override def save(exercise: WebExercise) {
    exercise.save()
    exercise.htmlTasks.forEach(_.save())
    exercise.jsTasks.forEach(_.saveInDB())
  }

  override def updateExercise(exercise: WebExercise, exerciseNode: JsonNode) {
    exercise.htmlText = JsonReader.readAndJoinTextArray(exerciseNode.get(HTML_TEXT_NAME))
    exercise.htmlTasks = ExerciseReader.readArray(exerciseNode.get("htmlTasks"), readHtmlTask).asJava

    exercise.jsText = JsonReader.readAndJoinTextArray(exerciseNode.get(JS_TEXT_NAME))
    exercise.jsTasks = ExerciseReader.readArray(exerciseNode.get("jsTasks"), readJsTask).asJava
  }

}
