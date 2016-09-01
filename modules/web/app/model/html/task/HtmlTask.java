package model.html.task;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    String xpathQuery = buildXPathQuery();
    List<WebElement> foundElements = searchContext.findElements(By.xpath(xpathQuery));

    Pattern p = Pattern.compile("//?[a-zA-Z]+/");
    Matcher m = p.matcher(xpathQuery);
    List<String> parentsMissing = new LinkedList<>();
    while(foundElements.isEmpty() && m.find()) {
      // TODO: remove /
      String parentMissing = xpathQuery.substring(0, m.end() - 1);
      while(parentMissing.startsWith("/"))
        parentMissing = parentMissing.substring(1);
      parentsMissing.add(parentMissing);

      // Try to find element without parent element
      xpathQuery = xpathQuery.substring(m.end() - 1);
      foundElements = searchContext.findElements(By.xpath(xpathQuery));

      // update Matcher with new, shorter string
      m = p.matcher(xpathQuery);
    }

    if(foundElements.isEmpty() || foundElements.size() > 1)
      return new ElementResult(this, Success.NONE);

    // Nur noch ein passendes Element
    WebElement foundElement = foundElements.get(0);

    List<ChildResult> evaluatedChildResults = evaluateAllChildResults(foundElement);
    List<AttributeResult> evaluatedAttributeResults = evaluateAllAttributeResults(foundElement);

    if(Task.allResultsSuccessful(evaluatedAttributeResults) && Task.allResultsSuccessful(evaluatedChildResults)
        && parentsMissing.isEmpty())
      //@formatter:off
      return new ElementResult(this, Success.COMPLETE)
          .withAttributeResults(evaluatedAttributeResults)
          .withChildResults(evaluatedChildResults)
          .withParentsMissing(parentsMissing);
    else
      return new ElementResult(this, Success.PARTIALLY)
          .withAttributeResults(evaluatedAttributeResults)
          .withChildResults(evaluatedChildResults)
          .withParentsMissing(parentsMissing);
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
