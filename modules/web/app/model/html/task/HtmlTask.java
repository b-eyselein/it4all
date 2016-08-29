package model.html.task;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import model.exercise.Success;
import model.html.result.AttributeResult;
import model.html.result.ChildResult;
import model.html.result.ElementResult;

@Entity
public class HtmlTask extends Task {

  @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
  @JsonManagedReference
  @JsonIgnore
  public List<ChildTask> childTasks;

  @Override
  public ElementResult evaluate(SearchContext searchContext) {
    // FIXME: Zerlegung XpathQuery, um Element evtl. unabhängig von
    // Elternelement zu suchen
    String xpathQuery = buildXPathQuery();
    List<WebElement> foundElements = searchContext.findElements(By.xpath(xpathQuery));

    if(foundElements.isEmpty() || foundElements.size() > 1)
      return new ElementResult(this, Success.NONE);

    // Nur noch ein passendes Element
    WebElement foundElement = foundElements.get(0);

    List<ChildResult> evaluatedChildResults = evaluateAllChildResults(foundElement);
    List<AttributeResult> evaluatedAttributeResults = evaluateAllAttributeResults(foundElement);

    if(Task.allResultsSuccessful(evaluatedAttributeResults) && Task.allResultsSuccessful(evaluatedChildResults))
      //@formatter:off
      return new ElementResult(this, Success.COMPLETE)
          .withAttributeResults(evaluatedAttributeResults)
          .withChildResults(evaluatedChildResults);
    else
      return new ElementResult(this, Success.PARTIALLY)
          .withAttributeResults(evaluatedAttributeResults)
          .withChildResults(evaluatedChildResults);
      //@formatter:on
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
    return childTasks.stream().map(childTask -> childTask.getChildResult()).collect(Collectors.toList());
  }

}
