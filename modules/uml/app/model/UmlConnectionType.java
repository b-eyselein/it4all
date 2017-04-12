package model;

public enum UmlConnectionType {
  
  // @formatter:off
  ASSOCIATION     ("Assoziation",     5),
  AGGREGATION     ("Aggregation",     2),
  COMPOSITION     ("Komposition",     1),

  IMPLEMENTATION  ("Implementierung", 3);
  //@formatter:on
  
  private String name;
  private int value; // TODO: to remove...
  
  private UmlConnectionType(String theName, int theValue) {
    name = theName;
    value = theValue;
  }
  
  public String getName() {
    return name;
  }
  
  public int getValue() {
    return value;
  }
  
}
