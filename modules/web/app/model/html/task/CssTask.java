package model.html.task;

import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import model.exercise.Success;
import model.html.result.AttributeResult;
import model.html.result.ChildResult;
import model.html.result.ElementResult;

@Entity
public class CssTask extends Task {

  @Override
  public ElementResult evaluate(SearchContext searchContext) {
    String xpathQuery = buildXPathQuery();
    List<WebElement> foundElements = searchContext.findElements(By.xpath(xpathQuery));

    if(foundElements.isEmpty())
      return new ElementResult(this, Success.NONE);

    boolean allSuccesful = true;
    for(WebElement element: foundElements) {
      List<AttributeResult> evaluatedAttributeResults = evaluateAllAttributeResults(element);
      if(!allResultsSuccessful(evaluatedAttributeResults))
        allSuccesful = false;
    }

    if(allSuccesful)
      return new ElementResult(this, Success.COMPLETE);
    else
      return new ElementResult(this, Success.PARTIALLY);
  }

  @Override
  protected String buildXPathQuery() {
    String xpathQuery = xpathQueryName;

    // Kein Attribut, wenn Tag eindeutig
    if(definingAttribute == null || definingAttribute.isEmpty())
      return xpathQuery;

    String[] valueAndKey = definingAttribute.split(KEY_VALUE_CHARACTER);
    xpathQuery += "[@" + valueAndKey[0] + " = '" + valueAndKey[1] + "']";
    return xpathQuery;
  }

  @Override
  protected List<ChildResult> getChildResults() {
    return Collections.emptyList();
  }

}
