package model;

public enum XmlExType {
  // @formatter:off
  XML_XSD("xml", "xsd", "xsd"),
  XML_DTD("xml", "dtd", "dtd"),
  XSD_XML("xsd", "xml", "xsd"),
  DTD_XML("dtd", "xml", "dtd");
  // @formatter:on

  private String studFileEnding;
  private String refFileEnding;
  private String gramFileEnding;

  private XmlExType(String studentEnding, String theRefFileEnding, String theGramFileEnding) {
    studFileEnding = studentEnding;
    refFileEnding = theRefFileEnding;
    gramFileEnding = theGramFileEnding;
  }

  public String getGramFileEnding() {
    return gramFileEnding;
  }

  public String getRefFileEnding() {
    return refFileEnding;
  }

  public String getStudFileEnding() {
    return studFileEnding;
  }

  public String getTag() {
    return "<span class=\"label label-default\">" + toString().replace("_", " gegen ") + "</span>";
  }

}