package model.task;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import com.google.common.base.Splitter;

import io.ebean.Finder;
import model.Attribute;
import model.result.AttributeResult;
import model.result.ElementResultBuilder;
import model.result.TextContentResult;
import model.result.WebResult;

@Entity
public class HtmlTask extends WebTask {
  
  private static final Splitter ATTR_SPLITTER = Splitter.on(";").omitEmptyStrings();
  
  public static final Finder<WebTaskKey, HtmlTask> finder = new Finder<>(HtmlTask.class);
  
  @Column
  public String attributes;
  
  @Column
  public String textContent;
  
  public HtmlTask(WebTaskKey theKey) {
    super(theKey);
  }
  
  @Override
  public WebResult evaluate(SearchContext searchContext) {
    final List<WebElement> foundElements = searchContext.findElements(By.xpath(xpathQuery));
    
    if(foundElements.isEmpty())
      return new ElementResultBuilder(this).withMessage("Element konnte nicht gefunden werden!").build();
    
    if(foundElements.size() > 1)
      return new ElementResultBuilder(this)
          .withMessage("Element konnte nicht eindeutig identifiziert werden. Existiert das Element eventuell mehrfach?")
          .build();
    
    final WebElement foundElement = foundElements.get(0);
    
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
