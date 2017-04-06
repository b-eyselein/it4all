package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

public class JsonWrapper {
  
  private static final JsonSchemaFactory FACTORY = JsonSchemaFactory.byDefault();
  
  private JsonWrapper() {
    
  }
  
  public static List<String> parseJsonArrayNode(JsonNode node) {
    List<String> ret = new ArrayList<>(node.size());
    
    for(JsonNode str: node)
      ret.add(str.asText());
    
    return ret;
  }
  
  public static Map<String, String> readKeyValueMap(JsonNode methodsNode) {
    Map<String, String> ret = new HashMap<>(methodsNode.size());
    
    for(JsonNode n: methodsNode)
      ret.put(n.get("key").asText(), n.get("value").asText());
    
    return ret;
  }
  
  public static ProcessingReport validateJson(JsonNode exercisesNode, JsonNode exercisesSchemaNode)
      throws ProcessingException {
    return FACTORY.getJsonSchema(exercisesSchemaNode).validate(exercisesNode);
    
  }
  
}
