package model.html.result;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import model.html.task.MultiNameTask;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class MultiNameResult extends ElementResultWithAttributes<MultiNameTask> {

  private List<String> defining_Attributes = new LinkedList<String>();
  private HashMap<String, WebElement> singleResults;

  public MultiNameResult(MultiNameTask task, String commonAttributes, String differentAttributes) {
    super(task);

    for(String attribute: differentAttributes.split(";"))
      if(attribute.contains("="))
        defining_Attributes.add(attribute);

    // TODO: Defining Attributes must not be empty (or size >= 2?
    // [___MULTI___ElementResult])
    if(defining_Attributes.size() < 2)
      throw new IllegalArgumentException("Es müssen mindestens 2 definierende Attribute vorhanden sein!");

    singleResults = new HashMap<String, WebElement>();
  }

  @Override
  public Success evaluate(WebDriver driver) {
    List<WebElement> foundElements = driver.findElements(By.name(task.elemName));
    if(foundElements.isEmpty())
      return Success.NONE;

    foundElements = filterForTagName(foundElements, task.tagName);
    if(foundElements.isEmpty())
      return Success.NONE;

    // TODO: Stelle sicher, dass alle gefundenen Elemente alle Common-Attribute
    // besitzen
    for(WebElement element: foundElements)
      checkAttributes(element);

    // Stelle sicher, dass alle Elemente jeweils ein Different-Attribut
    // enthalten
    // for(String attribute: defining_Attributes) {
    // for(WebElement element: foundElements) {
    // String key = attribute.split("=")[0], value = attribute.split("=")[1];
    // AttributeResult attributeResult = new AttributeResult(element, key,
    // value);
    // if(attributeResult.isFound()) {
    // attrs.add(new AttributeResult(element, key, value));
    // singleResults.put(attribute, element);
    // }
    // }
    // }

    if(singleResults.size() == defining_Attributes.size())
      return Success.COMPLETE;
    else if(singleResults.size() > 0)
      return Success.PARTIALLY;
    else
      return Success.NONE;

  }

  @Override
  protected List<String> getAttributesAsJson() {
    // TODO Auto-generated method stub
    List<String> retList = new LinkedList<String>();
    return retList;
  }

  @Override
  protected List<String> getMessagesAsJson() {
    // TODO Auto-generated method stub
    return Collections.emptyList();
  }

}
