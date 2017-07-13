package model.exercisereading;

public abstract class AbstractReadingResult {

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
