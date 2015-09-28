package uniwue.html.parser.document;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class HtmlTag {
  
  private String tagName;
  private List<ChildrenHtmlTag> children = new LinkedList<ChildrenHtmlTag>();
  private Map<String, String> attributes = new HashMap<String, String>();
  
  public HtmlTag(String tag) {
    tagName = tag.toLowerCase();
  }
  
  public String getTag() {
    return tagName;
  }
  
  public void addChildren(ChildrenHtmlTag tag) {
    children.add(tag);
  }
  
  public List<ChildrenHtmlTag> getChildren() {
    return children;
  }
  
  public void addAttribute(String name, String value) {
    attributes.put(name, value);
  }
  
  public String getAttributeValue(String name) {
    return attributes.get(name);
  }
  
  @Override
  public String toString() {
    return tagName + children.toString();
  }
  
}
