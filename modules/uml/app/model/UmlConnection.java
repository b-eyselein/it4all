package model;

public class UmlConnection {

  public enum UmlConnectionType {
    
    AGGREGATION, ASSOCIATION, COMPOSITION, GENERALIZATION, IMPLEMENTATION, STANDARD;
  
  }
  
  private UmlConnectionType type;
  private String start;
  private String target;
  private String mulstart;

  private String multarget;
  
  public UmlConnection(UmlConnectionType connType, String connStart, String connTarget, String connMulstart,
      String connMultarget) {
    type = connType;
    
    start = connStart;
    target = connTarget;
    mulstart = connMulstart;
    multarget = connMultarget;
  }

  public String getMulstart() {
    return mulstart;
  }

  public String getMultarget() {
    return multarget;
  }

  public String getStart() {
    return start;
  }

  public String getTarget() {
    return target;
  }

  public UmlConnectionType getType() {
    return type;
  }

}
