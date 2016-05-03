package model.xml.task;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import model.html.result.ElementResult;
import model.html.result.MultiNameResult;
import model.html.result.Success;

@Entity
@DiscriminatorValue("multiname")
public class MultiNameTask extends Task {

  @Column(name = "elemName")
  public String elemName;

  @Override
  public ElementResult<? extends Task> evaluate(SearchContext driver) {
    List<WebElement> foundElements = driver.findElements(By.name(elemName));
    if(foundElements.isEmpty())

      return new MultiNameResult(this, Success.NONE, attributes, attributes);

    foundElements = filterElementsForTagName(foundElements, tagName);
    if(foundElements.isEmpty())

      return new MultiNameResult(this, Success.NONE, attributes, attributes);

    // TODO: Stelle sicher, dass alle gefundenen Elemente alle Common-Attribute
    // besitzen
    for(WebElement element: foundElements)
      evaluateAllAttributes(element);

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

    // if(singleResults.size() == defining_Attributes.size())
    // return Success.COMPLETE;
    // else if(singleResults.size() > 0)
    // return Success.PARTIALLY;
    // else

    return new MultiNameResult(this, Success.PARTIALLY, attributes, attributes);

  }

}
