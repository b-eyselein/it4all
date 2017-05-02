package model;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;

import model.exercisereading.ExerciseReader;
import model.task.Action;
import model.task.Condition;
import model.task.HtmlTask;
import model.task.JsConditionKey;
import model.task.JsWebTask;
import model.task.TaskKey;
import play.libs.Json;

public class WebExerciseReader extends ExerciseReader<WebExercise> {
  
  private static final String XPATH_NAME = "xpathQuery";
  private static final String ATTRS_NAME = "attributes";
  
  public WebExerciseReader() {
    super("web");
  }
  
  private static String readAttributes(JsonNode attributesNode) {
    List<Attribute> attributes = new LinkedList<>();
    
    final Iterator<JsonNode> attributeNodesIter = attributesNode.elements();
    
    while(attributeNodesIter.hasNext())
      attributes.add(Json.fromJson(attributeNodesIter.next(), Attribute.class));
    
    return attributes.stream().map(Attribute::forDB).collect(Collectors.joining(";"));
  }
  
  private static Condition readCondition(JsonNode conditionNode) {
    JsConditionKey key = Json.fromJson(conditionNode.get("key"), JsConditionKey.class);
    
    Condition condition = Condition.finder.byId(key);
    
    if(condition == null)
      return Json.fromJson(conditionNode, Condition.class);
    
    JsonNode xpathNode = conditionNode.get(XPATH_NAME);
    JsonNode awaitedValueNode = conditionNode.get("awaitedValue");
    JsonNode isPrecondNode = conditionNode.get("isPrecondition");
    
    condition.xpathQuery = xpathNode.asText();
    condition.awaitedValue = awaitedValueNode.asText();
    condition.isPrecondition = isPrecondNode.asBoolean();
    
    return condition;
  }
  
  private static List<Condition> readConditions(JsonNode conditionsNode) {
    List<Condition> conditions = new LinkedList<>();
    
    for(final Iterator<JsonNode> conditionsNodeIter = conditionsNode.elements(); conditionsNodeIter.hasNext();)
      conditions.add(readCondition(conditionsNodeIter.next()));
    
    return conditions;
  }
  
  private static HtmlTask readHtmlTask(TaskKey key, JsonNode htmlTaskNode) {
    JsonNode textNode = htmlTaskNode.get(TEXT_NAME);
    JsonNode xpathNode = htmlTaskNode.get(XPATH_NAME);
    JsonNode attributesNode = htmlTaskNode.get(ATTRS_NAME);
    JsonNode contentNode = htmlTaskNode.get("textContent");
    
    HtmlTask task = HtmlTask.finder.byId(key);
    if(task == null)
      task = new HtmlTask(key);
    
    task.text = textNode.asText();
    task.xpathQuery = xpathNode.asText();
    task.attributes = attributesNode != null ? readAttributes(attributesNode) : "";
    task.textContent = contentNode.isNull() ? null : contentNode.asText();
    
    return task;
  }
  
  private static List<HtmlTask> readHtmlTasks(JsonNode htmlTasksNode, int exerciseId) {
    List<HtmlTask> tasks = new LinkedList<>();
    
    int taskId = 1;
    for(final Iterator<JsonNode> taskNodeIter = htmlTasksNode.elements(); taskNodeIter.hasNext();)
      tasks.add(readHtmlTask(new TaskKey(taskId++, exerciseId), taskNodeIter.next()));
    
    return tasks;
  }
  
  private static JsWebTask readJsTask(TaskKey taskKey, JsonNode jsTaskNode) {
    JsonNode textNode = jsTaskNode.get(TEXT_NAME);
    JsonNode xpathNode = jsTaskNode.get(XPATH_NAME);
    JsonNode actionNode = jsTaskNode.get("action");
    JsonNode conditionsNode = jsTaskNode.get("conditions");
    
    JsWebTask task = JsWebTask.finder.byId(taskKey);
    if(task == null)
      task = new JsWebTask(taskKey);
    
    task.text = textNode.asText();
    task.xpathQuery = xpathNode.asText();
    task.action = actionNode != null ? Json.fromJson(actionNode, Action.class) : null;
    task.conditions = conditionsNode != null ? readConditions(conditionsNode) : Collections.emptyList();
    
    return task;
  }
  
  private static List<JsWebTask> readJsTasks(JsonNode jsTasksNode, int exerciseId) {
    List<JsWebTask> tasks = new LinkedList<>();
    
    int taskId = 1;
    for(final Iterator<JsonNode> taskNodeIter = jsTasksNode.elements(); taskNodeIter.hasNext();)
      tasks.add(readJsTask(new TaskKey(taskId++, exerciseId), taskNodeIter.next()));
    
    return tasks;
  }
  
  @Override
  protected WebExercise readExercise(JsonNode exerciseNode) {
    JsonNode idNode = exerciseNode.get(ID_NAME);
    JsonNode titleNode = exerciseNode.get(TITLE_NAME);
    JsonNode textNode = exerciseNode.get(TEXT_NAME);
    
    JsonNode htmlTextNode = exerciseNode.get("htmlText");
    JsonNode jsTextNode = exerciseNode.get("jsText");
    
    JsonNode htmlTasksNode = exerciseNode.get("htmlTasks");
    JsonNode jsTasksNode = exerciseNode.get("jsTasks");
    
    int exerciseId = idNode.asInt();
    WebExercise exercise = WebExercise.finder.byId(exerciseId);
    if(exercise == null)
      exercise = new WebExercise(exerciseId);
    
    exercise.title = titleNode.asText();
    exercise.text = String.join("", JsonWrapper.parseJsonArrayNode(textNode));
    
    exercise.htmlText = String.join("", JsonWrapper.parseJsonArrayNode(htmlTextNode));
    exercise.jsText = String.join("", JsonWrapper.parseJsonArrayNode(jsTextNode));
    
    exercise.htmlTasks = htmlTasksNode != null ? readHtmlTasks(htmlTasksNode, exerciseId) : Collections.emptyList();
    exercise.jsTasks = jsTasksNode != null ? readJsTasks(jsTasksNode, exerciseId) : Collections.emptyList();
    
    return exercise;
  }
  
}
