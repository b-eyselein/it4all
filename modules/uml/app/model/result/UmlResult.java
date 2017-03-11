package model.result;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import model.UmlExercise;

public abstract class UmlResult {

  private UmlExercise exercise;
  private String title;

  public UmlResult(UmlExercise theExercise, String theTitle) {
    exercise = theExercise;
    title = theTitle;
  }

  protected static List<String> parseJSONArray(JsonNode jsonArrayNode) {
    List<String> ret = new LinkedList<>();
    for(JsonNode jsonNode: jsonArrayNode)
      ret.add(jsonNode.asText());
    return ret;
  }

  public UmlExercise getExercise() {
    return exercise;
  }

  public String getTitle() {
    return title;
  }

}
