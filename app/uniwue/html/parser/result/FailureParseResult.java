package uniwue.html.parser.result;

import java.util.LinkedList;
import java.util.List;

public class FailureParseResult implements ParseResult {
  
  //FIXME: return successful options?
  
  
  private List<HtmlParsingError> errors = new LinkedList<HtmlParsingError>();
  
  public void addParsingError(HtmlParsingError error) {
    errors.add(error);
  }
  
  public List<HtmlParsingError> getErrors() {
    return errors;
  }
  
  public boolean parseWasSuccessful() {
    return false;
  }
  
  public void addAllErrors(List<HtmlParsingError> allErrors) {
    errors.addAll(allErrors);
  }
  
}
