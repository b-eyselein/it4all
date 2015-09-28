package uniwue.html.parser.handler;

import uniwue.html.parser.Parser;
import uniwue.html.parser.Parser.ParsingState;
import uniwue.html.parser.document.HtmlTag;
import uniwue.html.parser.document.TextTag;

public class DefaultStateCharHandler implements CharHandler<DefaultStateCharHandler> {
  
  public static String handle(Parser parser, char c, String currentRead, HtmlTag currentOpenTag) {
    if(c == '<') {
      if(currentRead != "") {
        currentOpenTag.addChildren(new TextTag(currentRead, currentOpenTag));
        currentRead = "";
      }
      parser.changeStateTo(ParsingState.OPEN_TAG_SEARCH_NAME);
    } else if(c == 10) {
      // Ignore Whitespaces
    } else  
      // innerHTML --> Text!
      currentRead += c;
    return currentRead;
  }
  
}
