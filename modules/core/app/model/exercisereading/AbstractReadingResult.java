package model.exercisereading;

public abstract class AbstractReadingResult {

  private String json;

  public AbstractReadingResult(String theJson) {
    json = theJson;
  }

  public String getJson() {
    return json;
  }

  public abstract boolean isSuccess();

}
