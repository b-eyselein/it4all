package model.html.task;

import java.util.Collections;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.openqa.selenium.WebElement;

import model.html.result.ElementResult;
import model.html.result.Success;

@Entity
@DiscriminatorValue("title")
public class TitleTask extends Task {
  
  public String title;
  
  @Override
  public ElementResult evaluateMore(List<WebElement> foundElements) {
    if(foundElements.size() == 0 || foundElements.size() > 1)
      return new ElementResult(this, Success.NONE, Collections.emptyList(), Collections.emptyList());
    
    // TODO: check Title!
    // String foundTitle = ((WebDriver) searchContext).getTitle();
    // if(foundTitle.equals(title))
    return new ElementResult(this, Success.COMPLETE, Collections.emptyList(), Collections.emptyList());
    // else
    // return new TitleResult(this, Success.PARTIALLY);
    
  }
}
