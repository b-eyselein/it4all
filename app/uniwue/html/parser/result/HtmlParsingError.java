package uniwue.html.parser.result;

import uniwue.html.parser.Parser.ParsingState;

public class HtmlParsingError {
  
  private ParsingState st;
  private String msg;
  private int li;
  
  public HtmlParsingError(ParsingState state, String message, int line) {
    st = state;
    msg = message;
    li = line;
  }
  
  public ParsingState getState() {
    return st;
  }
  
  public String getMessage() {
    return msg;
  }
  
  public int getLine() {
    return li;
  }
  
  @Override
  public String toString() {
    return "Zeile " + li + " (" + st + "): " + msg;
  }
  
}
