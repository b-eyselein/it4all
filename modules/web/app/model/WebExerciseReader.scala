package model

import java.util.Collections

import scala.collection.JavaConverters.{ asScalaIteratorConverter, seqAsJavaListConverter }

import com.fasterxml.jackson.databind.JsonNode

import model.exercisereading.{ ExerciseReader, JsonReader }
import model.task.{ Action, Condition, HtmlTask, JsConditionKey, JsWebTask, WebTaskKey }
import play.data.DynamicForm
import play.libs.Json

object WebExerciseReader extends ExerciseReader[WebExercise]("web", WebExercise.finder, classOf[Array[WebExercise]]) {

  def readCondition(conditionNode: JsonNode): Condition = {
    val key = Json.fromJson(conditionNode.get("key"), classOf[JsConditionKey])

    Condition.finder.byId(key) match {
      case condition if condition == null ⇒ return Json.fromJson(conditionNode, classOf[Condition])
      case condition ⇒
        condition.xpathQuery = conditionNode.get(StringConsts.XPATH_NAME).asText
        condition.awaitedValue = conditionNode.get("awaitedValue").asText
        condition.isPrecondition = conditionNode.get("isPrecondition").asBoolean
        condition
    }
  }

  def readHtmlTask(htmlTaskNode: JsonNode): HtmlTask = {
    val key = Json.fromJson(htmlTaskNode.get(StringConsts.KEY_NAME), classOf[WebTaskKey])

    var task = HtmlTask.finder.byId(key)
    if (task == null)
      task = new HtmlTask(key)

    task.text = JsonReader.readAndJoinTextArray(htmlTaskNode.get(StringConsts.TEXT_NAME), "")
    task.xpathQuery = htmlTaskNode.get(StringConsts.XPATH_NAME).asText
    task.attributes = readAttributes(htmlTaskNode.get(StringConsts.ATTRS_NAME))
    task.textContent = htmlTaskNode.get("textContent").asText

    return task
  }

  def readJsTask(jsTaskNode: JsonNode): JsWebTask = {
    val key = Json.fromJson(jsTaskNode.get(StringConsts.KEY_NAME), classOf[WebTaskKey])

    var task = JsWebTask.finder.byId(key)
    if (task == null)
      task = new JsWebTask(key)

    val actionNode = jsTaskNode.get("action")
    val conditionsNode = jsTaskNode.get("conditions")

    task.text = JsonReader.readAndJoinTextArray(jsTaskNode.get(StringConsts.TEXT_NAME), "")
    task.xpathQuery = jsTaskNode.get(StringConsts.XPATH_NAME).asText

    task.action = if (actionNode != null) Json.fromJson(actionNode, classOf[Action]) else null

    task.conditions =
      if (conditionsNode != null)
        ExerciseReader.readArray(conditionsNode, WebExerciseReader.readCondition(_))
      else
        Collections.emptyList()

    return task
  }

  def readAttributes(attributesNode: JsonNode) =
    attributesNode.iterator.asScala.map(Json.fromJson(_, classOf[Attribute])).map(_.forDB).mkString

  override def initRemainingExFromForm(exercise: WebExercise, form: DynamicForm) {
    exercise.htmlText = form.get("htmlText")
    exercise.jsText = form.get("jsText")
  }

  override def instantiate(id: Int) = new WebExercise(id)

  override def save(exercise: WebExercise) {
    exercise.save
    exercise.htmlTasks.forEach(_.save)
    exercise.jsTasks.forEach(_.saveInDB)
  }

  override def updateExercise(exercise: WebExercise, exerciseNode: JsonNode) {
    exercise.htmlText = JsonReader.readAndJoinTextArray(exerciseNode.get("htmlText"), "")
    exercise.htmlTasks = ExerciseReader.readArray(exerciseNode.get("htmlTasks"), readHtmlTask(_))

    exercise.jsText = JsonReader.readAndJoinTextArray(exerciseNode.get("jsText"), "")
    exercise.jsTasks = ExerciseReader.readArray(exerciseNode.get("jsTasks"), readJsTask(_))
  }

}
