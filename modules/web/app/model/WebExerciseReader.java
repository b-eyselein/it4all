package model;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;

import model.exercisereading.ExerciseReader;
import model.task.Action;
import model.task.Condition;
import model.task.HtmlTask;
import model.task.JsConditionKey;
import model.task.JsWebTask;
import model.task.WebTaskKey;
import play.libs.Json;

public class WebExerciseReader extends ExerciseReader<WebExercise> {

  private static final WebExerciseReader INSTANCE = new WebExerciseReader();

  private WebExerciseReader() {
    super("web", WebExercise.finder, WebExercise[].class);
  }

  public static WebExerciseReader getInstance() {
    return INSTANCE;
  }

  public static Condition readCondition(JsonNode conditionNode) {
    JsConditionKey key = Json.fromJson(conditionNode.get("key"), JsConditionKey.class);

    Condition condition = Condition.finder.byId(key);
    if(condition == null)
      return Json.fromJson(conditionNode, Condition.class);

    condition.xpathQuery = conditionNode.get(StringConsts.XPATH_NAME).asText();
    condition.awaitedValue = conditionNode.get("awaitedValue").asText();
    condition.isPrecondition = conditionNode.get("isPrecondition").asBoolean();

    return condition;
  }

  public static HtmlTask readHtmlTask(JsonNode htmlTaskNode) {
    WebTaskKey key = Json.fromJson(htmlTaskNode.get(StringConsts.KEY_NAME), WebTaskKey.class);

    HtmlTask task = HtmlTask.finder.byId(key);
    if(task == null)
      task = new HtmlTask(key);

    task.text = readTextArray(htmlTaskNode.get(StringConsts.TEXT_NAME), "");
    task.xpathQuery = htmlTaskNode.get(StringConsts.XPATH_NAME).asText();
    task.attributes = readAttributes(htmlTaskNode.get(StringConsts.ATTRS_NAME));
    task.textContent = htmlTaskNode.get("textContent").asText();

    return task;
  }

  public static JsWebTask readJsTask(JsonNode jsTaskNode) {
    WebTaskKey key = Json.fromJson(jsTaskNode.get(StringConsts.KEY_NAME), WebTaskKey.class);

    JsWebTask task = JsWebTask.finder.byId(key);
    if(task == null)
      task = new JsWebTask(key);

    JsonNode actionNode = jsTaskNode.get("action");
    JsonNode conditionsNode = jsTaskNode.get("conditions");

    task.text = readTextArray(jsTaskNode.get(StringConsts.TEXT_NAME), "");
    task.xpathQuery = jsTaskNode.get(StringConsts.XPATH_NAME).asText();
    task.action = actionNode != null ? Json.fromJson(actionNode, Action.class) : null;
    task.conditions = conditionsNode != null ? readArray(conditionsNode, WebExerciseReader::readCondition)
        : Collections.emptyList();

    return task;
  }

  private static String readAttributes(JsonNode attributesNode) {
    return StreamSupport.stream(attributesNode.spliterator(), true)
        .map(attrNode -> Json.fromJson(attrNode, Attribute.class)).map(Attribute::forDB)
        .collect(Collectors.joining(";"));
  }

  @Override
  public void saveRead(WebExercise exercise) {
    exercise.save();
    exercise.htmlTasks.forEach(HtmlTask::save);
    exercise.jsTasks.forEach(JsWebTask::saveInDB);
  }

  @Override
  protected WebExercise instantiateExercise(int id, String title, String author, String text, JsonNode exerciseNode) {
    String htmlText = readTextArray(exerciseNode.get("htmlText"), "");
    List<HtmlTask> htmlTasks = readArray(exerciseNode.get("htmlTasks"), WebExerciseReader::readHtmlTask);

    String jsText = readTextArray(exerciseNode.get("jsText"), "");
    List<JsWebTask> jsTasks = readArray(exerciseNode.get("jsTasks"), WebExerciseReader::readJsTask);

    return new WebExercise(id, title, author, text, htmlText, htmlTasks, jsText, jsTasks);
  }

}
