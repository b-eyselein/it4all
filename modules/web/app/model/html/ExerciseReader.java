package model.html;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import model.html.task.CssTask;
import model.html.task.HtmlTask;
import model.html.task.JsAction;
import model.html.task.JsCondition;
import model.html.task.JsTask;
import model.html.task.TaskKey;
import model.html.task.WebTask;
import play.Logger;
import play.libs.Json;

public class ExerciseReader {

  private static final String XPATH = "xpath";
  private static final File JSON_SCHEMA_FILE = Paths.get("conf", "resources", "html", "exercises-schema.json").toFile();

  private ExerciseReader() {
    // FIXME: check all JsonNodes for null!
  }

  public static List<WebExercise> readExercises(Path jsonFile) {
    List<WebExercise> exercises = new LinkedList<>();

    if(!Files.exists(jsonFile)) {
      Logger.error("The file " + jsonFile + " does not exist!");
      return exercises;
    }

    JsonNode exercisesNode;
    JsonNode exercisesSchemaNode;

    try {
      exercisesNode = Json.parse(new FileInputStream(jsonFile.toFile()));
      exercisesSchemaNode = Json.parse(new FileInputStream(JSON_SCHEMA_FILE));
    } catch (FileNotFoundException e) {
      Logger.error("There was an error reading the file " + jsonFile + ":", e);
      return exercises;
    }

    if(!validateJson(exercisesNode, exercisesSchemaNode))
      return exercises;

    for(final Iterator<JsonNode> exerciseNodeIter = exercisesNode.elements(); exerciseNodeIter.hasNext();)
      exercises.add(readExercise(exerciseNodeIter.next()));

    return exercises;
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

    return String.join(WebTask.MULTIPLE_ATTRIBUTES_SPLIT_CHARACTER, attributes);
  }

  private static CssTask readCssTask(TaskKey taskKey, JsonNode cssTaskNode) {
    JsonNode textNode = cssTaskNode.get("text");
    JsonNode xpathNode = cssTaskNode.get(XPATH);
    JsonNode attributesNode = cssTaskNode.get("attributes");

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

  private static WebExercise readExercise(JsonNode exerciseNode) {
    JsonNode idNode = exerciseNode.get("id");
    JsonNode titleNode = exerciseNode.get("title");
    JsonNode textNode = exerciseNode.get("text");
    JsonNode htmlTasksNode = exerciseNode.get("htmlTasks");
    JsonNode cssTasksNode = exerciseNode.get("cssTasks");
    JsonNode jsTasksNode = exerciseNode.get("jsTasks");

    int exerciseId = idNode.asInt();

    WebExercise exercise = WebExercise.finder.byId(exerciseId);
    if(exercise == null)
      exercise = new WebExercise(exerciseId);

    exercise.title = titleNode.asText();
    exercise.text = textNode.asText();
    exercise.htmlTasks = readHtmlTasks(htmlTasksNode, exerciseId);
    if(cssTasksNode != null)
      exercise.cssTasks = readCssTasks(cssTasksNode, exerciseId);
    if(jsTasksNode != null)
      exercise.jsTasks = readJsTasks(jsTasksNode, exerciseId);
    return exercise;
  }

  private static HtmlTask readHtmlTask(TaskKey taskKey, JsonNode htmlTaskNode) {
    JsonNode textNode = htmlTaskNode.get("text");
    JsonNode xpathNode = htmlTaskNode.get(XPATH);
    JsonNode attributesNode = htmlTaskNode.get("attributes");

    HtmlTask task = HtmlTask.finder.byId(taskKey);
    if(task == null)
      task = new HtmlTask(taskKey);

    task.text = textNode.asText();
    task.xpathQuery = xpathNode.asText();
    if(attributesNode != null)
      task.attributes = readAttributes(attributesNode);
    return task;
  }

  private static List<HtmlTask> readHtmlTasks(JsonNode htmlTasksNode, int exerciseId) {
    List<HtmlTask> tasks = new LinkedList<>();

    int taskId = 1;
    for(final Iterator<JsonNode> taskNodeIter = htmlTasksNode.elements(); taskNodeIter.hasNext();)
      tasks.add(readHtmlTask(new TaskKey(taskId++, exerciseId), taskNodeIter.next()));

    return tasks;
  }

  private static JsAction readJsAction(JsonNode actionNode) {
    // TODO Auto-generated method stub
    JsonNode actionTypeNode = actionNode.get("actionType");
    JsonNode xpathNode = actionNode.get("xpath");
    JsonNode keysToSendNode = actionNode.get("keysToSend");

    JsAction action = new JsAction();
    action.actiontype = JsAction.ActionType.valueOf(actionTypeNode.asText());
    action.xpathQuery = xpathNode.asText();
    if(keysToSendNode != null)
      action.keysToSend = keysToSendNode.asText();

    return action;
  }

  private static JsCondition readJsCondition(JsonNode conditionNode, int exerciseId) {
    // TODO Auto-generated method stub
    JsonNode xpathNode = conditionNode.get(XPATH);
    JsonNode awaitedNode = conditionNode.get("awaited");

    JsCondition condition = new JsCondition();
    condition.xpathQuery = xpathNode.asText();
    condition.awaitedValue = awaitedNode.asText();

    return condition;
  }

  private static List<JsCondition> readJsConditions(JsonNode conditionsNode) {
    // TODO Auto-generated method stub
    List<JsCondition> tasks = new LinkedList<>();

    int taskId = 1;
    for(final Iterator<JsonNode> conditionsNodeIter = conditionsNode.elements(); conditionsNodeIter.hasNext();)
      tasks.add(readJsCondition(conditionsNodeIter.next(), taskId++));

    return tasks;

  }

  private static JsTask readJsTask(TaskKey taskKey, JsonNode jsTaskNode) {
    // TODO Auto-generated method stub
    JsonNode textNode = jsTaskNode.get("text");
    JsonNode preconditionsNode = jsTaskNode.get("preconditions");
    JsonNode actionNode = jsTaskNode.get("action");
    JsonNode postconditionsNode = jsTaskNode.get("postconditions");

    JsTask task = JsTask.finder.byId(taskKey);
    if(task == null)
      task = new JsTask(taskKey);

    task.text = textNode.asText();
    task.preconditions = readJsConditions(preconditionsNode);
    task.action = readJsAction(actionNode);
    task.postconditions = readJsConditions(postconditionsNode);
    return task;
  }

  private static List<JsTask> readJsTasks(JsonNode jsTasksNode, int exerciseId) {
    // TODO Auto-generated method stub
    List<JsTask> tasks = new LinkedList<>();

    int taskId = 1;
    for(final Iterator<JsonNode> taskNodeIter = jsTasksNode.elements(); taskNodeIter.hasNext();)
      tasks.add(readJsTask(new TaskKey(taskId++, exerciseId), taskNodeIter.next()));

    return tasks;
  }

  private static boolean validateJson(JsonNode exercisesNode, JsonNode exercisesSchemaNode) {
    try {
      JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
      ProcessingReport report = factory.getJsonSchema(exercisesSchemaNode).validate(exercisesNode);

      if(!report.isSuccess()) {
        // FIXME: report error!

        List<String> messages = new LinkedList<>();
        Iterator<ProcessingMessage> iter = report.iterator();
        while(iter.hasNext())
          messages.add(iter.next().toString());

        Logger.error("There have been errors validating a JSON file:\n" + String.join("\n", messages));
      }

      return report.isSuccess();
    } catch (ProcessingException e) {
      Logger.error("There has been an error validating a JSON file!", e);
      return false;
    }
  }

}
