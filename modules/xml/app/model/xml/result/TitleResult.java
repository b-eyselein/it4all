package model.xml.result;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import model.html.task.TitleTask;
import play.Logger;

public class TitleResult extends ElementResult<TitleTask> {
  
  public TitleResult(TitleTask task, Success success) {
    super(task, success, Collections.emptyList());
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
      Logger.error("Fehler bei Titlesuche!");
      return Arrays.asList("{\"suc\": \"-\", \"mes\": \"Fehler bei Titelsuche!\"}");
    }
  }
}
