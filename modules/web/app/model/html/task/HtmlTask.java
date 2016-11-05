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

import com.avaje.ebean.Finder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import model.exercise.EvaluationResult;
import model.exercise.Success;
import model.html.result.AttributeResult;
import model.html.result.ChildResult;
import model.html.result.ElementResult;

@Entity
public class HtmlTask extends Task {

  private static Pattern pattern = Pattern.compile("//?[a-zA-Z]+/");

  public static final Finder<TaskKey, HtmlTask> finder = new Finder<>(HtmlTask.class);

  @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
  @JsonManagedReference
  @JsonIgnore
  public List<ChildTask> childTasks;

  public HtmlTask(TaskKey theKey) {
    super(theKey);
  }

  @Override
  public EvaluationResult evaluate(SearchContext searchContext) {
    // FIXME: refactor complete method!
    List<WebElement> foundElements = searchContext.findElements(By.xpath(xpathQuery));

    Matcher m = pattern.matcher(xpathQuery);
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
      m = pattern.matcher(xpathQuery);
    }

    if(foundElements.isEmpty())
      return new ElementResult(this, Success.NONE, "Element konnte nicht gefunden werden!");

    if(foundElements.size() > 1)
      return new ElementResult(this, Success.NONE,
          "Element konnte nicht eindeutig identifiziert werden. Existiert das Element eventuell mehrfach?");

    // Nur noch ein passendes Element
    WebElement foundElement = foundElements.get(0);

    List<ChildResult> evaluatedChildResults = evaluateAllChildResults(foundElement);
    List<AttributeResult> evaluatedAttributeResults = evaluateAllAttributeResults(foundElement);

    Success success = Success.PARTIALLY;
    if(allResultsSuccessful(evaluatedAttributeResults) && allResultsSuccessful(evaluatedChildResults)
        && parentsMissing.isEmpty())
      success = Success.COMPLETE;

    ElementResult result = new ElementResult(this, success);

    result.withAttributeResults(evaluatedAttributeResults);
    result.withChildResults(evaluatedChildResults);

    if(!parentsMissing.isEmpty())
      result.withParentsMissing(parentsMissing);

    return result;
  }

  @Override
  public String toString() {
    // TODO: implement
    return "(" + key.exerciseId + ", " + key.taskId + "): " + xpathQuery + " :: " + text + " :: "
        + (attributes.isEmpty() ? " -- " : attributes);
  }

  @Override
  protected List<ChildResult> getChildResults() {
    return childTasks.stream().map(childTask -> childTask.getChildResult()).collect(Collectors.toList());
  }

}
