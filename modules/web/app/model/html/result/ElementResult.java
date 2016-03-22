package model.html.result;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import model.html.task.Task;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public abstract class ElementResult<TaskType extends Task> {

  public enum Success {
    COMPLETE("+"), PARTIALLY("o"), NONE("-");

    private String jsonRepresentant;

    private Success(String jsonRep) {
      jsonRepresentant = jsonRep;
    }

    public String getJsonRepresentant() {
      return this.jsonRepresentant;
    }

  }

  protected TaskType task;

  // protected String tag;
  protected Success success = Success.NONE;

  protected String message = "";
  protected List<String> attributesToFind = new LinkedList<String>();

  protected List<AttributeResult> attrs = new LinkedList<AttributeResult>();

  public ElementResult(TaskType task, String tagName, String attributes) {
    this.task = task;
    for(String attribute: attributes.split(";"))
      if(!attribute.isEmpty() && attribute.contains("="))
        attributesToFind.add(attribute);
    // else
    // FIXME: Log failure!!
    // System.out.println("Fehler bei den Attributen!");
  }

  public boolean allAttributesFound() {
    return attrs.stream().filter(attr -> attr.isFound()).collect(Collectors.counting()) == attrs.size();
  }

  public abstract void evaluate(WebDriver driver);

  public List<AttributeResult> getAttributes() {
    return attrs;
  }

  public String getMessage() {
    return message;
  }

  public int getPoints() {
    if(success == Success.NONE)
      return 0;
    else if(success == Success.COMPLETE)
      return 2;
    else
      return 1;
  }

  public int getPointsMax() {
    return 2;
  }

  public Success getSuccess() {
    return success;
  }

  public Task getTask() {
    return task;
  }

  public void setResult(Success suc, String mes) {
    success = suc;
    message += mes;
  }

  public String toJSON() {
    String json = "{";

    // Success
    json += "\"suc\":\"" + success.getJsonRepresentant() + "\", ";

    // Exercise and Task
    json += "\"ex\":\"" + task.exercise.id + "\", \"task\":\"" + task.id + "\", ";

    // Tag!!
    json += "\"tag\":\"" + task.tagName + "\", ";

    // Message
    json += "\"message\":\"" + message + "\"";

    // Attributes
    if(!attrs.isEmpty()) {
      List<String> attrJSONs = attrs.stream().map(attr -> attr.toJSON()).collect(Collectors.toList());
      json += ",\n\t\"attrs\": [\n\t\t" + String.join(",\n\t\t", attrJSONs) + "\n\t]";
    } else
      json += ",\n\t\"attrs\": []";

    json += "}";

    return json;
  }

  protected boolean checkAttributes(WebElement element) {
    boolean attributesFound = true;
    for(String att: attributesToFind) {
      String key = att.split("=")[0], value = att.split("=")[1];
      AttributeResult result = new AttributeResult(element, key, value);
      if(!result.isFound())
        attributesFound = false;
      attrs.add(new AttributeResult(element, key, value));
    }
    return attributesFound;
  }

  protected List<WebElement> filterForTagName(List<WebElement> foundElements, String tagName) {
    return foundElements.parallelStream().filter(element -> element.getTagName().equals(tagName))
        .collect(Collectors.toList());
  }

}
