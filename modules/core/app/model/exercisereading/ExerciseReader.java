package model.exercisereading;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import model.exercise.Exercise;
import play.Logger;

public abstract class ExerciseReader<T extends Exercise> {
  
  public abstract List<T> readExercises(Path jsonFile, Path jsonSchemaFile);
  
  protected boolean validateJson(JsonNode exercisesNode, JsonNode exercisesSchemaNode) {
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
