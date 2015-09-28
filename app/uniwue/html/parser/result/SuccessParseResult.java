package uniwue.html.parser.result;

import uniwue.html.parser.document.RootHtmlTag;
import uniwue.html.parser.result.ParseResult;

public class SuccessParseResult implements ParseResult {
  
  private String doctype;
  private RootHtmlTag root;
  
  public SuccessParseResult(String doctypeString, RootHtmlTag rootTag) {
    doctype = doctypeString;
    root = rootTag;
  }
  
  public String getDoctypeString() {
    return doctype;
  }
  
  public RootHtmlTag getRootTag() {
    return root;
  }

  public boolean parseWasSuccessful() {
    return true;
  }
  
}
