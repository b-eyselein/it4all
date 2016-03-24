package model.html.result;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.LinkedList;
import java.util.stream.Collectors;

import model.html.task.NameTask;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class NameResult extends ElementResultWithAttributes<NameTask> {

  private boolean rightTagName = false;
  private List<ChildResult> childResults;

  public NameResult(NameTask task) {
    super(task);
    System.out.println(task.elemName + " :: " + task.childTasks.size());
    childResults = task.childTasks.stream().map(childTask -> childTask.getChildResult()).collect(Collectors.toList());
  }

  @Override
  public Success evaluate(WebDriver driver) {
    List<WebElement> foundElements = driver.findElements(By.name(task.elemName));
    if(foundElements.isEmpty())
      return Success.NONE;

    foundElements = filterForTagName(foundElements, task.tagName);
    if(foundElements.isEmpty())
      return Success.NONE;

    rightTagName = true;

    // FIXME: LOG?!
    if(foundElements.size() > 1)
      System.out.println("Mehrere Elemente gefunden, benutze erstes!");

    WebElement element = foundElements.get(0);

    // TODO: ChildResults!
    if(!task.childTasks.isEmpty())
      childResults.parallelStream().forEach(childRes -> childRes.setSuccess(childRes.evaluate(element)));

    if(checkAttributes(element))
      return Success.COMPLETE;
    else
      return Success.PARTIALLY;

  }

  public boolean rightTagNameWasUsed() {
    return rightTagName;
  }

  @Override
  protected List<String> getAttributesAsJson() {
    if(attributesToFind.isEmpty())
      return Collections.emptyList();
    else
      return attributesToFind.parallelStream().map(attrRes -> attrRes.toJSON()).collect(Collectors.toList());
  }

  @Override
  protected List<String> getMessagesAsJson() {
    if(success == Success.NONE)
      return Arrays.asList("{\"suc\": \"-\", \"mes\": \"Element wurde nicht gefunden!\"}");
    
    List<String> messages = new LinkedList<String>();
    messages.add("{\"suc\": \"+\", \"mes\": \"Element wurde gefunden.\"}");
    childResults.forEach(childRes -> messages.add(childRes.toJson()));
    return messages;
  }
}
