package uniwue.html.parser.handler;

import uniwue.html.parser.Parser;
import uniwue.html.parser.document.HtmlTag;

public interface CharHandler<T extends CharHandler<T>> {
  
  /**
   * Override this method!!
   * 
   * @param parser
   * @param c
   * @param currentRead
   * @param currentOpenTag
   * @return
   */
  public static String handle(Parser parser, char c, String currentRead, HtmlTag currentOpenTag) {
    return currentRead;
  }

}
