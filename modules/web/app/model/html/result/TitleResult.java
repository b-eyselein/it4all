package model.html.result;

import model.html.task.TitleTask;

import org.openqa.selenium.WebDriver;

public class TitleResult extends ElementResult<TitleTask> {
  
  private String theTitle;
  
  public TitleResult(TitleTask task, String title, String attributes) {
    super(task, "title", attributes);
    theTitle = title;
  }
  
  @Override
  public void evaluate(WebDriver driver) {
    // TODO Auto-generated method stub
    if(driver.getTitle() == null)
      setResult(Success.NONE, "Es wurde kein Seitentitel gefunden!");
    else if(driver.getTitle().equals(this.theTitle))
      setResult(Success.COMPLETE, "Es wurde der richtige Titel gefunden.");
    else
      setResult(Success.PARTIALLY, "Es wurde ein falscher Title gefunden!");
  }
  
}
