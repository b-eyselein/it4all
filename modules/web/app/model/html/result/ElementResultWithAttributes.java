package model.html.result;

import java.util.LinkedList;
import java.util.List;

import org.openqa.selenium.WebElement;

import model.html.task.Task;

public abstract class ElementResultWithAttributes<TaskType extends Task> extends ElementResult<TaskType> {

  protected List<AttributeResult> attributesToFind = new LinkedList<AttributeResult>();

  public ElementResultWithAttributes(TaskType theTask) {
    super(theTask);

    if(task.attributes.isEmpty())
      return;

    for(String attribute: task.attributes.split(";")) {
      if(!attribute.isEmpty() && attribute.contains("=")) {
        String[] valueAndKey = attribute.split("=");
        attributesToFind.add(new AttributeResult(valueAndKey[0], valueAndKey[1]));
      }

    }
  }

  protected boolean checkAttributes(WebElement element) {
    boolean attributesFound = true;
    for(AttributeResult result: attributesToFind)
      attributesFound &= result.evaluate(element);
    return attributesFound;
  }

}
