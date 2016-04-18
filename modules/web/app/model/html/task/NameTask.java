package model.html.task;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import model.html.result.AttributeResult;
import model.html.result.ChildResult;
import model.html.result.ElementResult;
import model.html.result.NameResult;
import model.html.result.Success;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import play.Logger;

@Entity
@DiscriminatorValue("name")
public class NameTask extends Task {

  @Column(name = "elemName")
  public String elemName;

  @Override
  public ElementResult<? extends Task> evaluate(SearchContext searchContext) {
    List<WebElement> foundElements = searchContext.findElements(By.name(elemName));
    if(foundElements.isEmpty())
      return new NameResult(this, Success.NONE, Collections.emptyList(), Collections.emptyList());

    foundElements = filterElementsForTagName(foundElements, tagName);
    if(foundElements.isEmpty())
      return new NameResult(this, Success.NONE, Collections.emptyList(), Collections.emptyList());

    if(foundElements.size() > 1)
      Logger.warn("Mehrere Elemente gefunden, benutze erstes!");

    WebElement element = foundElements.get(0);

    // FIXME: ChildResults!
    List<ChildResult> childResults = new LinkedList<ChildResult>();
    childTasks.stream().map(childTask -> childTask.getChildResult()).forEach(childResult -> {
      childResult.evaluate(element);
      childResults.add(childResult);
    });

    List<AttributeResult> attributeResults = evaluateAllAttributes(element);

    if(allAttributesFound(attributeResults) && allChildElementsFound(childResults))
      return new NameResult(this, Success.COMPLETE, attributeResults, childResults);
    else
      return new NameResult(this, Success.PARTIALLY, attributeResults, childResults);
  }

}
