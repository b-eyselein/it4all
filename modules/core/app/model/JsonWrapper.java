package model;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

public class JsonWrapper {

  private static final JsonSchemaFactory FACTORY = JsonSchemaFactory.byDefault();

  private JsonWrapper() {

  }

  public static List<String> parseJsonArrayNode(JsonNode node) {
    return StreamSupport.stream(node.spliterator(), true).map(JsonNode::asText).collect(Collectors.toList());
  }

  public static Map<String, String> readKeyValueMap(JsonNode methodsNode) {
    return StreamSupport.stream(methodsNode.spliterator(), true).collect(
        Collectors.toMap(n -> n.get(StringConsts.KEY_NAME).asText(), n -> n.get(StringConsts.VALUE_NAME).asText()));
  }

  public static Optional<ProcessingReport> validateJson(JsonNode exercisesNode, JsonNode exercisesSchemaNode) {
    try {
      // FIXME: Exception!
      return Optional.of(FACTORY.getJsonSchema(exercisesSchemaNode).validate(exercisesNode));
    } catch (ProcessingException e) {
      return Optional.empty();
    }
  }

}
