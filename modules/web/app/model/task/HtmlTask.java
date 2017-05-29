package model.task;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Entity;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import com.google.common.base.Splitter;

import model.Attribute;
import model.result.AttributeResult;
import model.result.ElementResult;
import model.result.ElementResultBuilder;
import model.result.TextContentResult;

@Entity
public class HtmlTask extends Task<ElementResult> {
  
  private static final Splitter ATTR_SPLITTER = Splitter.on(";").omitEmptyStrings();
  
  public static final Finder<TaskKey, HtmlTask> finder = new Finder<>(HtmlTask.class);
  
  public String attributes;
  
  public String textContent;
  
  public HtmlTask(TaskKey theKey) {
    super(theKey);
  }
  
  @Override
  public ElementResult evaluate(SearchContext searchContext) {
    List<WebElement> foundElements = searchContext.findElements(By.xpath(xpathQuery));

    if(foundElements.isEmpty())
      return new ElementResultBuilder(this).withMessage("Element konnte nicht gefunden werden!").build();

    if(foundElements.size() > 1)
      return new ElementResultBuilder(this)
          .withMessage("Element konnte nicht eindeutig identifiziert werden. Existiert das Element eventuell mehrfach?")
          .build();

    WebElement foundElement = foundElements.get(0);

    // @formatter:off
    return new ElementResultBuilder(this)
        .withFoundElement(foundElement)
        .withAttributeResults(evaluateAllAttributeResults(foundElement))
        .withTextContentResult(textContent == null ? null : new TextContentResult(foundElement.getText(), textContent))
        .build();
    // @formatter:on
  }
  
  public List<Attribute> getAttributes() {
    return ATTR_SPLITTER.splitToList(attributes).stream().map(Attribute::fromString).collect(Collectors.toList());
  }
  
  protected List<AttributeResult> evaluateAllAttributeResults(WebElement foundElement) {
    return getAttributes().stream().map(attr -> attr.evaluate(foundElement)).collect(Collectors.toList());
  }
  
}
