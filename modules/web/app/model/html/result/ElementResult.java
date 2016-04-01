package model.html.result;

import java.util.List;

import model.html.task.Task;

public abstract class ElementResult<TaskType extends Task> {

  protected TaskType task;
  protected Success success = Success.NONE;
  protected List<AttributeResult> attributeResults;

  public ElementResult(TaskType theTask, Success theSuccess, List<AttributeResult> theAttributeResults) {
    task = theTask;
    success = theSuccess;
    attributeResults = theAttributeResults;
  }

  public int getPoints() {
    if(success == Success.NONE)
      return 0;
    else if(success == Success.COMPLETE)
      return 2;
    else
      return 1;
  }

  public Success getSuccess() {
    return success;
  }

  public Task getTask() {
    return task;
  }

  public void setResult(Success suc) {
    success = suc;
  }

  public String toJSON() {
    String json = "{";

    // Success
    json += "\n\t" + "\"suc\": \"" + success.getJsonRepresentant() + "\"" + ", ";

    // Exercise
    json += "\n\t" + "\"ex\": " + task.key.exerciseId + ",";

    // Task
    json += " " + "\"task\": " + task.key.id + ",";

    // Messages
    List<String> messages = getMessagesAsJson();
    json = addMessagesToJson(json, messages);

    // Attributes

    List<String> attrs = getAttributesAsJson();
    json += ",";
    if(!attrs.isEmpty()) {
      json += "\n\t\"attrs\": [\n\t\t" + String.join(",\n\t\t", attrs) + "\n\t]";
    } else
      json += "\n\t\"attrs\": []";

    json += "\n}";

    return json;
  }

  private String addMessagesToJson(String json, List<String> messages) {
    json += "\n\t";
    json += "\"messages\": [";
    for(int i = 0; i < messages.size(); i++) {
      json += "\n\t\t";
      json += messages.get(i);

      if(i == messages.size() - 1)
        json += "\n\t";
      else
        json += ",";
    }
    json += "]";
    return json;
  }

  protected abstract List<String> getAttributesAsJson();

  protected abstract List<String> getMessagesAsJson();

}
