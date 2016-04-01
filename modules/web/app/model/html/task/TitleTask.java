package model.html.task;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import model.html.result.ElementResult;
import model.html.result.Success;
import model.html.result.TitleResult;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;

@Entity
@DiscriminatorValue("title")
public class TitleTask extends Task {
  
  public String title;
  
  @Override
  public ElementResult<? extends Task> evaluate(SearchContext searchContext) {
    // TODO: evtl. ueber driver.findElement(By.tag("title")), um genauere
    // Meldung hinzubekommen?
    // TODO: Cast entfernen ?!?
    String foundTitle = ((WebDriver) searchContext).getTitle();
    if(foundTitle == null)
      return new TitleResult(this, Success.NONE);
    else if(foundTitle.equals(title))
      return new TitleResult(this, Success.COMPLETE);
    else
      return new TitleResult(this, Success.PARTIALLY);
  }
}
