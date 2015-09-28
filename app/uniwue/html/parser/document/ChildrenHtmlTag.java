package uniwue.html.parser.document;

public class ChildrenHtmlTag extends HtmlTag {
  
  private HtmlTag par;
  
  public ChildrenHtmlTag(String tag, HtmlTag parent) {
    super(tag);
    par = parent;
  }
  
  public HtmlTag getParent() {
    return par;
  }
  
}
