package uniwue.html.parser.document;

public class RootHtmlTag extends HtmlTag {
  
  public RootHtmlTag(String tag) {
    super(tag);
    if(!tag.toLowerCase().equals("html"))
      // TODO: andere Exception bzw. andere Moeglichkeit, Fehler anzuzeigen!
      throw new IllegalArgumentException("RootTag <html> fehlt!");
  }
  
}
