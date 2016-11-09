package model.html;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import model.exercisereading.ExerciseReader;
import model.html.task.CssTask;
import model.html.task.HtmlTask;
import model.html.task.Task;
import model.html.task.TaskKey;
import play.Logger;
import play.libs.Json;

public class HtmlExerciseReader extends ExerciseReader<HtmlExercise> {
  
  private HtmlExerciseReader() {
    // FIXME: check all JsonNodes for null!
  }
  
  public static List<HtmlExercise> readExercises(Path jsonFile) {
    List<HtmlExercise> exercises = new LinkedList<>();
    
    if(!Files.exists(jsonFile)) {
      Logger.error("The file " + jsonFile + " does not exist!");
      return exercises;
    }
    
    JsonNode exercisesNode;
    
    // FIXME: validate JSON-File!!!!!

    try {
      exercisesNode = Json.parse(String.join("\n", Files.readAllLines(jsonFile)));
    } catch (IOException e) {
      Logger.error("There was an error reading the file " + jsonFile + ":", e);
      return exercises;
    }
    
    for(final Iterator<String> idIter = exercisesNode.fieldNames(); idIter.hasNext();) {
      String idAsStr = idIter.next();
      exercises.add(readExercise(Integer.parseInt(idAsStr), exercisesNode.get(idAsStr)));
    }
    
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
  
  private static HtmlExercise readExercise(int exerciseId, JsonNode exerciseNode) {
    JsonNode titleNode = exerciseNode.get("title");
    JsonNode textNode = exerciseNode.get("text");
    JsonNode htmlTasksNode = exerciseNode.get("tasks");
    JsonNode cssTasksNode = exerciseNode.get("cssTasks");
    
    List<HtmlTask> htmlTasks = readHtmlTasks(htmlTasksNode, exerciseId);
    List<CssTask> cssTasks = readCssTasks(cssTasksNode, exerciseId);
    
    HtmlExercise exercise = HtmlExercise.finder.byId(exerciseId);
    if(exercise == null)
      exercise = new HtmlExercise(exerciseId);
    
    exercise.title = titleNode.asText();
    exercise.text = textNode.asText();
    exercise.htmlTasks = htmlTasks;
    exercise.cssTasks = cssTasks;
    return exercise;
  }
  
  private static HtmlTask readHtmlTask(TaskKey key, JsonNode htmlTaskNode) {
    JsonNode textNode = htmlTaskNode.get("text");
    JsonNode xpathNode = htmlTaskNode.get("xpath");
    JsonNode attributesNode = htmlTaskNode.get("attributes");
    
    HtmlTask task = HtmlTask.finder.byId(key);
    if(task == null)
      task = new HtmlTask(key);
    
    task.text = textNode.asText();
    task.xpathQuery = xpathNode.asText();
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
  
  @Override
  public List<HtmlExercise> readExercises(Path jsonFile, Path jsonSchemaFile) {
    // TODO Auto-generated method stub
    return null;
  }
  
}
