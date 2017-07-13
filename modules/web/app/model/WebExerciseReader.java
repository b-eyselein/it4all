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
  
  private static final String XPATH_NAME = "xpathQuery";
  private static final String ATTRS_NAME = "attributes";
  
  private static final WebExerciseReader INSTANCE = new WebExerciseReader();
  
  private WebExerciseReader() {
    super("web");
  }
  
  public static WebExerciseReader getInstance() {
    return INSTANCE;
  }
  
  private static String readAttributes(JsonNode attributesNode) {
    return StreamSupport.stream(attributesNode.spliterator(), true)
        .map(attrNode -> Json.fromJson(attrNode, Attribute.class)).map(Attribute::forDB)
        .collect(Collectors.joining(";"));
  }
  
  private static Condition readCondition(JsonNode conditionNode) {
    JsConditionKey key = Json.fromJson(conditionNode.get("key"), JsConditionKey.class);
    
    Condition condition = Condition.finder.byId(key);
    if(condition == null)
      return Json.fromJson(conditionNode, Condition.class);
    
    condition.xpathQuery = conditionNode.get(XPATH_NAME).asText();
    condition.awaitedValue = conditionNode.get("awaitedValue").asText();
    condition.isPrecondition = conditionNode.get("isPrecondition").asBoolean();
    
    return condition;
  }
  
  private static List<Condition> readConditions(JsonNode conditionsNode) {
    return StreamSupport.stream(conditionsNode.spliterator(), true).map(WebExerciseReader::readCondition)
        .collect(Collectors.toList());
  }
  
  private static HtmlTask readHtmlTask(JsonNode htmlTaskNode) {
    WebTaskKey key = Json.fromJson(htmlTaskNode.get(StringConsts.KEY_NAME), WebTaskKey.class);
    
    HtmlTask task = HtmlTask.finder.byId(key);
    if(task == null)
      task = new HtmlTask(key);
    
    task.text = JsonWrapper.readTextArray(htmlTaskNode.get(StringConsts.TEXT_NAME), "");
    task.xpathQuery = htmlTaskNode.get(XPATH_NAME).asText();
    task.attributes = readAttributes(htmlTaskNode.get(ATTRS_NAME));
    task.textContent = htmlTaskNode.get("textContent").asText();
    
    return task;
  }
  
  private static List<HtmlTask> readHtmlTasks(JsonNode htmlTasksNode) {
    return StreamSupport.stream(htmlTasksNode.spliterator(), true).map(WebExerciseReader::readHtmlTask)
        .collect(Collectors.toList());
  }
  
  private static JsWebTask readJsTask(JsonNode jsTaskNode) {
    WebTaskKey key = Json.fromJson(jsTaskNode.get(StringConsts.KEY_NAME), WebTaskKey.class);
    
    JsWebTask task = JsWebTask.finder.byId(key);
    if(task == null)
      task = new JsWebTask(key);
    
    JsonNode actionNode = jsTaskNode.get("action");
    JsonNode conditionsNode = jsTaskNode.get("conditions");
    
    task.text = JsonWrapper.readTextArray(jsTaskNode.get(StringConsts.TEXT_NAME), "");
    task.xpathQuery = jsTaskNode.get(XPATH_NAME).asText();
    task.action = actionNode != null ? Json.fromJson(actionNode, Action.class) : null;
    task.conditions = conditionsNode != null ? readConditions(conditionsNode) : Collections.emptyList();
    
    return task;
  }
  
  private static List<JsWebTask> readJsTasks(JsonNode jsTasksNode) {
    return StreamSupport.stream(jsTasksNode.spliterator(), true).map(WebExerciseReader::readJsTask)
        .collect(Collectors.toList());
  }
  
  @Override
  public void saveRead(WebExercise exercise) {
    exercise.save();
    exercise.htmlTasks.forEach(HtmlTask::save);
    exercise.jsTasks.forEach(JsWebTask::saveInDB);
  }
  
  @Override
  protected WebExercise read(JsonNode exerciseNode) {
    int id = exerciseNode.get(StringConsts.ID_NAME).asInt();

    String title = exerciseNode.get(StringConsts.TITLE_NAME).asText();
    String author = exerciseNode.get(StringConsts.AUTHOR_NAME).asText();
    String text = JsonWrapper.readTextArray(exerciseNode.get(StringConsts.TEXT_NAME), "");
    
    String htmlText = JsonWrapper.readTextArray(exerciseNode.get("htmlText"), "");
    List<HtmlTask> htmlTasks = readHtmlTasks(exerciseNode.get("htmlTasks"));
    
    String jsText = JsonWrapper.readTextArray(exerciseNode.get("jsText"), "");
    List<JsWebTask> jsTasks = readJsTasks(exerciseNode.get("jsTasks"));

    WebExercise exercise = WebExercise.finder.byId(id);
    if(exercise == null)
      return new WebExercise(id, title, author, text, htmlText, htmlTasks, jsText, jsTasks);
    else
      return exercise.updateValues(id, title, author, text, htmlText, htmlTasks, jsText, jsTasks);
  }
  
}
