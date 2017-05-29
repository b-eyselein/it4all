package model.exercisereading;

import model.exercise.Exercise;

public abstract class AbstractReadingResult<T extends Exercise> {

  private String json;
  private String jsonSchema;

  public AbstractReadingResult(String theJson, String theJsonSchema) {
    json = theJson;
    jsonSchema = theJsonSchema;
  }

  public String getJson() {
    return json;
  }

  public String getJsonSchema() {
    return jsonSchema;
  }

  public abstract boolean isSuccess();

}
