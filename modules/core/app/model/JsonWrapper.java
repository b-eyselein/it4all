package model;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import play.Logger;

public class JsonWrapper {
  
  private static final JsonSchemaFactory FACTORY = JsonSchemaFactory.byDefault();
  
  private JsonWrapper() {
    
  }
  
  public static boolean validateJson(JsonNode exercisesNode, JsonNode exercisesSchemaNode) {
    try {
      ProcessingReport report = FACTORY.getJsonSchema(exercisesSchemaNode).validate(exercisesNode);
      
      if(!report.isSuccess()) {
        // report errors
        List<String> messages = new LinkedList<>();
        report.forEach(mes -> messages.add(mes.toString()));
        Logger.error("There have been errors validating a JSON file:\n" + String.join("\n", messages));
      }
      
      return report.isSuccess();
    } catch (ProcessingException e) {
      Logger.error("There has been an error validating a JSON file!", e);
      return false;
    }
  }
  
}
