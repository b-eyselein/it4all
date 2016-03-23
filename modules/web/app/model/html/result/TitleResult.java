package model.html.result;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import model.html.task.TitleTask;

import org.openqa.selenium.WebDriver;

public class TitleResult extends ElementResult<TitleTask> {

  private String title;

  public TitleResult(TitleTask task, String theTitle) {
    super(task);
    title = theTitle;
  }

  @Override
  public Success evaluate(WebDriver driver) {
    // TODO: evtl. ueber driver.findElement(By.tag("title")), um genauere
    // Meldung hinzubekommen?
    if(driver.getTitle() == null || driver.getTitle().isEmpty())
      return Success.NONE;
    else if(driver.getTitle().equals(title))
      return Success.COMPLETE;
    else
      return Success.PARTIALLY;
  }

  @Override
  protected List<String> getAttributesAsJson() {
    return Collections.emptyList();
  }

  @Override
  protected List<String> getMessagesAsJson() {
    switch(success) {
    case COMPLETE:
      return Arrays.asList("{\"suc\": \"+\", \"mes\": \"Titel wurde gefunden und hat richtigen Wert.\"}");
    case PARTIALLY:
      return Arrays.asList("{\"suc\": \"o\", \"mes\": \"Titel wurde nicht gefunden oder hat den falschen Wert!\"}");
    case NONE:
      return Arrays.asList("{\"suc\": \"-\", \"mes\": \"Titel wurde nicht gefunden!\"}");
    default:
      // TODO: LOG Failure!
      return Arrays.asList("{\"suc\": \"-\", \"mes\": \"Fehler bei Titelsuche!\"}");
    }
  }
}
