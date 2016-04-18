package model.html.result;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import model.html.task.NameTask;

public class NameResult extends ElementResult<NameTask> {

  private List<ChildResult> childResults = new LinkedList<ChildResult>();

  public NameResult(NameTask task, Success success, List<AttributeResult> attributeResults,
      List<ChildResult> theChildResults) {
    super(task, success, attributeResults);
    childResults = theChildResults;
  }

  protected List<String> getMessagesAsJson() {
    // FIXME: childResults!
    if(success == Success.NONE)
      return Arrays.asList("{\"suc\": \"-\", \"mes\": \"Element wurde nicht gefunden!\"}");

    List<String> messages = new LinkedList<String>();
    messages.add("{\"suc\": \"+\", \"mes\": \"Element wurde gefunden.\"}");
    childResults.forEach(childRes -> messages.add(childRes.toJson()));
    return messages;
  }

}
