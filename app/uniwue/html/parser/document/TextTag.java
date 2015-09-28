package uniwue.html.parser.document;

import java.util.List;

public class TextTag extends ChildrenHtmlTag {

  private String con;
  
  public TextTag(String content, HtmlTag parent) {
    super("", parent);
    con = content;
  }
  
  @Override
  public String getTag() {
    return null;
  }
  
  @Override
  public List<ChildrenHtmlTag> getChildren() {
    return null;
  }
  
  public String getContent() {
    return con;
  }
  
}
