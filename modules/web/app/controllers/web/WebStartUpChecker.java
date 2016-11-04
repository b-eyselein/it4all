package controllers.web;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import controllers.core.StartUpChecker;
import model.html.HtmlExercise;
import model.javascript.JsExercise;
import model.javascript.JsExercise.JsDataType;
import model.javascript.JsTest;
import model.javascript.JsTestKey;

public class WebStartUpChecker extends StartUpChecker {
  
  private static String res = "conf/resources";
  
  private static void handleJsExercise(JsonNode exerciseNode) {
    int id = exerciseNode.get("id").asInt();
    JsExercise exercise = JsExercise.finder.byId(id);
    
    if(exercise == null)
      exercise = new JsExercise(id);
    
    exercise.title = exerciseNode.get("title").asText();
    exercise.text = exerciseNode.get("text").asText();
    exercise.declaration = exerciseNode.get("declaration").asText();
    exercise.functionname = exerciseNode.get("functionname").asText();
    exercise.sampleSolution = exerciseNode.get("samplesolution").asText();
    exercise.inputtypes = exerciseNode.get("inputtypes").asText();
    exercise.inputcount = exerciseNode.get("inputcount").asInt();
    exercise.returntype = JsDataType.valueOf(exerciseNode.get("returntype").asText());
    exercise.save();
    
    for(final Iterator<JsonNode> testIter = exerciseNode.get("tests").elements(); testIter.hasNext();)
      handleTest(exercise, testIter.next());
  }
  
  private static void handleTest(JsExercise exercise, JsonNode testNode) {
    int testId = testNode.get("id").asInt();
    JsTestKey key = new JsTestKey(exercise.id, testId);
    JsTest test = JsTest.finder.byId(key);
    
    if(test == null)
      test = new JsTest(key);
    
    test.inputs = testNode.get("input").asText();
    test.output = testNode.get("output").asText();
    test.save();
  }
  
  @Override
  protected void performStartUpCheck() {
    theLogger.info("Running startup checks for Web");
    
    // FIXME: read Html + Css exercises from JSON-FILE?!?
    
    // Assert that there is at least one exercise for all types
    List<HtmlExercise> exercises = HtmlExercise.finder.all();
    if(exercises.isEmpty())
      theLogger.error("\t- No exercises found for Html!");
    else
      for(HtmlExercise exercise: exercises)
        if(exercise.htmlTasks.isEmpty())
          theLogger.error("\t- Html-Aufgabe " + exercise.id + " hat keine Tasks!");
        
    // Js - normal exercises
    Path file = Paths.get(res, "javascript", "nashorn.json");
    JsonNode content = null;
    try {
      content = readJsonFile(file);
    } catch (IOException e) {
      theLogger.error("There was an error while reading the JSON-File " + file, e);
    }
    if(content == null)
      return;
    
    for(final Iterator<JsonNode> iter = content.elements(); iter.hasNext();)
      handleJsExercise(iter.next());
  }
  
}
