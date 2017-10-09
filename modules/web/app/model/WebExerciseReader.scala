package model

import java.util.Collections

import scala.collection.JavaConverters._

import com.fasterxml.jackson.databind.JsonNode

import model.StringConsts._
import model.exercisereading.{ ExerciseReader, JsonReader }
import model.task.{ Action, Condition, HtmlTask, JsConditionKey, JsWebTask, WebTaskKey }
import play.data.DynamicForm
import play.libs.Json

object WebExerciseReader extends ExerciseReader[WebExercise]("web", WebExercise.finder, classOf[Array[WebExercise]]) {

  def readCondition(conditionNode: JsonNode): Condition = {
    val key = Json.fromJson(conditionNode.get(KEY_NAME), classOf[JsConditionKey])
    val condition = Option(Condition.finder.byId(key)).getOrElse(new Condition(key))

    condition.xpathQuery = conditionNode.get(XPATH_NAME).asText
    condition.awaitedValue = conditionNode.get(AWAITED_VALUE_NAME).asText
    condition.isPrecondition = conditionNode.get("isPrecondition").asBoolean

    condition
  }

  def readHtmlTask(htmlTaskNode: JsonNode) = {
    val key = Json.fromJson(htmlTaskNode.get(KEY_NAME), classOf[WebTaskKey])
    val task = Option(HtmlTask.finder.byId(key)).getOrElse(new HtmlTask(key))

    task.text = JsonReader.readAndJoinTextArray(htmlTaskNode.get(TEXT_NAME))
    task.xpathQuery = htmlTaskNode.get(XPATH_NAME).asText
    task.attributes = readAttributes(htmlTaskNode.get(ATTRS_NAME))
    task.textContent = htmlTaskNode.get(TEXT_CONTENT_NAME).asText

    task
  }

  def readJsTask(jsTaskNode: JsonNode) = {
    val key = Json.fromJson(jsTaskNode.get(KEY_NAME), classOf[WebTaskKey])

    val task = Option(JsWebTask.finder.byId(key)).getOrElse(new JsWebTask(key))

    val actionNode = jsTaskNode.get("action")
    val conditionsNode = jsTaskNode.get("conditions")

    task.text = JsonReader.readAndJoinTextArray(jsTaskNode.get(TEXT_NAME))
    task.xpathQuery = jsTaskNode.get(XPATH_NAME).asText

    task.action = if (actionNode != null) Json.fromJson(actionNode, classOf[Action]) else null

    task.conditions =
      if (conditionsNode != null)
        ExerciseReader.readArray(conditionsNode, WebExerciseReader.readCondition(_))
      else
        Collections.emptyList()

    task
  }

  def readAttribute(attrNode: JsonNode): Attribute = new Attribute(attrNode.get("key").asText, attrNode.get("value").asText)

  def readAttributes(attributesNode: JsonNode): String = {
    ExerciseReader.readArray(attributesNode, readAttribute(_)).asScala.map(_.forDB).mkString(HtmlTask.ATTRS_JOIN_STR)
  }

  override def initRemainingExFromForm(exercise: WebExercise, form: DynamicForm) {
    exercise.htmlText = form.get(HTML_TEXT_NAME)
    exercise.jsText = form.get(JS_TEXT_NAME)
  }

  override def instantiate(id: Int) = new WebExercise(id)

  override def save(exercise: WebExercise) {
    exercise.save
    exercise.htmlTasks.forEach(_.save)
    exercise.jsTasks.forEach(_.saveInDB)
  }

  override def updateExercise(exercise: WebExercise, exerciseNode: JsonNode) {
    exercise.htmlText = JsonReader.readAndJoinTextArray(exerciseNode.get(HTML_TEXT_NAME))
    exercise.htmlTasks = ExerciseReader.readArray(exerciseNode.get("htmlTasks"), readHtmlTask(_))

    exercise.jsText = JsonReader.readAndJoinTextArray(exerciseNode.get(JS_TEXT_NAME))
    exercise.jsTasks = ExerciseReader.readArray(exerciseNode.get("jsTasks"), readJsTask(_))
  }

}
