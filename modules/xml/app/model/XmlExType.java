package model;

import model.exercise.Tag;

public enum XmlExType implements Tag {
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
  
  @Override
  public String getButtonContent() {
    return toString();
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
  
  @Override
  public String getTitle() {
    // TODO Auto-generated method stub
    return toString();
  }
  
}