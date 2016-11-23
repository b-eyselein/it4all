package model.html;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import model.exercisereading.ExerciseReader;
import model.html.task.CssTask;
import model.html.task.HtmlTask;
import model.html.task.Task;
import model.html.task.TaskKey;

public class WebExerciseReader extends ExerciseReader<WebExercise> {

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
    JsonNode textNode = cssTaskNode.get("text");
    JsonNode xpathNode = cssTaskNode.get("xpath");
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

  private static HtmlTask readHtmlTask(TaskKey key, JsonNode htmlTaskNode) {
    JsonNode textNode = htmlTaskNode.get("text");
    JsonNode xpathNode = htmlTaskNode.get("xpath");
    JsonNode attributesNode = htmlTaskNode.get("attributes");
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
  
  @Override
  protected WebExercise readExercise(JsonNode exerciseNode) {
    JsonNode idNode = exerciseNode.get("id");
    JsonNode titleNode = exerciseNode.get("title");
    JsonNode textNode = exerciseNode.get("text");
    JsonNode htmlTasksNode = exerciseNode.get("htmlTasks");
    JsonNode cssTasksNode = exerciseNode.get("cssTasks");

    int exerciseId = idNode.asInt();
    WebExercise exercise = WebExercise.finder.byId(exerciseId);
    if(exercise == null)
      exercise = new WebExercise(exerciseId);

    exercise.title = titleNode.asText();
    exercise.text = textNode.asText();
    exercise.htmlTasks = readHtmlTasks(htmlTasksNode, exerciseId);
    exercise.cssTasks = readCssTasks(cssTasksNode, exerciseId);

    return exercise;
  }

}
