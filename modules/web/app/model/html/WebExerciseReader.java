package model.html;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import model.exercisereading.ExerciseReader;
import model.html.task.CssTask;
import model.html.task.HtmlTask;
import model.html.task.JsWebTask;
import model.html.task.Task;
import model.html.task.TaskKey;
import model.javascript.Action;
import model.javascript.Action.ActionType;
import model.javascript.Condition;
import model.javascript.JsConditionKey;

public class WebExerciseReader extends ExerciseReader<WebExercise> {

  private static final String XPATH_NAME = "xpath";
  private static final String ATTRS_NAME = "attributes";

  public WebExerciseReader() {
    super("web");
  }

  private static String readAttribute(JsonNode attributeNode) {
    JsonNode keyNode = attributeNode.get("key");
    JsonNode valueNode = attributeNode.get("value");

    return keyNode.asText() + "=" + valueNode.asText();
  }

  private static String readAttributes(JsonNode attributesNode) {
    List<String> attributes = new LinkedList<>();

    for(final Iterator<JsonNode> attributeNodesIter = attributesNode.elements(); attributeNodesIter.hasNext();)
      attributes.add(readAttribute(attributeNodesIter.next()));

    return String.join(Task.MULTIPLE_ATTRIBUTES_SPLIT_CHARACTER, attributes);
  }

  private static CssTask readCssTask(TaskKey taskKey, JsonNode cssTaskNode) {
    JsonNode textNode = cssTaskNode.get(TEXT_NAME);
    JsonNode xpathNode = cssTaskNode.get(XPATH_NAME);
    JsonNode attributesNode = cssTaskNode.get(ATTRS_NAME);

    CssTask cssTask = CssTask.finder.byId(taskKey);
    if(cssTask == null)
      cssTask = new CssTask(taskKey);

    cssTask.text = textNode.asText();
    cssTask.xpathQuery = xpathNode.asText();
    cssTask.attributes = readAttributes(attributesNode);

    return cssTask;
  }

  private static List<CssTask> readCssTasks(JsonNode cssTasksNode, int exerciseId) {
    List<CssTask> tasks = new LinkedList<>();

    int taskId = 1;
    for(final Iterator<JsonNode> taskNodeIter = cssTasksNode.elements(); taskNodeIter.hasNext();)
      tasks.add(readCssTask(new TaskKey(taskId++, exerciseId), taskNodeIter.next()));

    return tasks;
  }

  private static HtmlTask readHtmlTask(TaskKey key, JsonNode htmlTaskNode) {
    JsonNode textNode = htmlTaskNode.get(TEXT_NAME);
    JsonNode xpathNode = htmlTaskNode.get(XPATH_NAME);
    JsonNode attributesNode = htmlTaskNode.get(ATTRS_NAME);
    JsonNode contentNode = htmlTaskNode.get("content");

    HtmlTask task = HtmlTask.finder.byId(key);
    if(task == null)
      task = new HtmlTask(key);

    task.text = textNode.asText();
    task.xpathQuery = xpathNode.asText();
    task.attributes = attributesNode == null ? "" : readAttributes(attributesNode);
    task.textContent = contentNode == null ? null : contentNode.asText();

    return task;
  }

  private static List<HtmlTask> readHtmlTasks(JsonNode htmlTasksNode, int exerciseId) {
    List<HtmlTask> tasks = new LinkedList<>();

    int taskId = 1;
    for(final Iterator<JsonNode> taskNodeIter = htmlTasksNode.elements(); taskNodeIter.hasNext();)
      tasks.add(readHtmlTask(new TaskKey(taskId++, exerciseId), taskNodeIter.next()));

    return tasks;
  }

  private Condition readCondition(JsonNode conditionNode, JsWebTask task) {
    // TODO Auto-generated method stub
    JsonNode idNode = conditionNode.get("id");
    JsonNode xpathNode = conditionNode.get(XPATH_NAME);
    JsonNode awaitedValueNode = conditionNode.get("awaitedValue");
    JsonNode isPrecondNode = conditionNode.get("isPrecondition");

    JsConditionKey key = new JsConditionKey(idNode.asInt(), task.key.taskId, task.key.exerciseId);

    Condition condition = Condition.finder.byId(key);
    if(condition == null)
      condition = new Condition(key);
    condition.xpathquery = xpathNode.asText();
    condition.awaitedvalue = awaitedValueNode.asText();
    condition.isPrecond = isPrecondNode.asBoolean();

    return condition;
  }

  private List<Condition> readConditions(JsonNode conditionsNode, JsWebTask task) {
    // TODO Auto-generated method stub
    List<Condition> conditions = new LinkedList<>();

    for(final Iterator<JsonNode> conditionsNodeIter = conditionsNode.elements(); conditionsNodeIter.hasNext();)
      conditions.add(readCondition(conditionsNodeIter.next(), task));

    return conditions;
  }

  private Action readJsAction(JsonNode actionNode) {
    JsonNode actiontypeNode = actionNode.get("actiontype");
    JsonNode actionxpathNode = actionNode.get("actionxpath");
    JsonNode keysToSendNode = actionNode.get("keystosend");

    Action action = new Action();
    action.actiontype = ActionType.valueOf(actiontypeNode.asText());
    action.actionXpathQuery = actionxpathNode.asText();
    action.keysToSend = keysToSendNode != null ? keysToSendNode.asText() : "";
    return action;
  }

  private JsWebTask readJsTask(TaskKey taskKey, JsonNode jsTaskNode) {
    JsonNode textNode = jsTaskNode.get(TEXT_NAME);
    JsonNode xpathNode = jsTaskNode.get(XPATH_NAME);
    JsonNode attributesNode = jsTaskNode.get(ATTRS_NAME);
    JsonNode actionNode = jsTaskNode.get("action");
    JsonNode conditionsNode = jsTaskNode.get("conditions");

    JsWebTask task = JsWebTask.finder.byId(taskKey);
    if(task == null)
      task = new JsWebTask(taskKey);

    task.text = textNode.asText();
    task.xpathQuery = xpathNode.asText();
    task.attributes = attributesNode == null ? "" : readAttributes(attributesNode);
    task.action = actionNode != null ? readJsAction(actionNode) : null;
    task.conditions = conditionsNode != null ? readConditions(conditionsNode, task) : Collections.emptyList();

    return task;
  }

  private List<JsWebTask> readJsTasks(JsonNode jsTasksNode, int exerciseId) {
    List<JsWebTask> tasks = new LinkedList<>();

    int taskId = 1;
    for(final Iterator<JsonNode> taskNodeIter = jsTasksNode.elements(); taskNodeIter.hasNext();)
      tasks.add(readJsTask(new TaskKey(taskId++, exerciseId), taskNodeIter.next()));

    return tasks;
  }

  @Override
  protected WebExercise readExercise(JsonNode exerciseNode) {
    JsonNode idNode = exerciseNode.get("id");
    JsonNode titleNode = exerciseNode.get("title");
    JsonNode textNode = exerciseNode.get(TEXT_NAME);

    JsonNode htmlTextNode = exerciseNode.get("htmlText");
    JsonNode cssTextNode = exerciseNode.get("cssText");
    JsonNode jsTextNode = exerciseNode.get("jsText");

    JsonNode htmlTasksNode = exerciseNode.get("htmlTasks");
    JsonNode cssTasksNode = exerciseNode.get("cssTasks");
    JsonNode jsTasksNode = exerciseNode.get("jsTasks");

    int exerciseId = idNode.asInt();
    WebExercise exercise = WebExercise.finder.byId(exerciseId);
    if(exercise == null)
      exercise = new WebExercise(exerciseId);

    exercise.title = titleNode.asText();
    exercise.text = textNode.asText();

    exercise.htmlText = htmlTextNode != null ? htmlTextNode.asText() : "";
    exercise.cssText = cssTextNode != null ? cssTextNode.asText() : "";
    exercise.jsText = jsTextNode != null ? jsTextNode.asText() : "";

    exercise.htmlTasks = htmlTasksNode != null ? readHtmlTasks(htmlTasksNode, exerciseId) : Collections.emptyList();
    exercise.cssTasks = cssTasksNode != null ? readCssTasks(cssTasksNode, exerciseId) : Collections.emptyList();
    exercise.jsTasks = jsTasksNode != null ? readJsTasks(jsTasksNode, exerciseId) : Collections.emptyList();

    return exercise;
  }

}
