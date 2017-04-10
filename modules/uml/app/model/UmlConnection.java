package model;

public class UmlConnection {
  
  private UmlConnectionType type;

  private String start;
  private String target;

  private Multiplicity mulstart;
  private Multiplicity multarget;

  public UmlConnection(UmlConnectionType theType, String theStart, String theTarget, Multiplicity theMulStart,
      Multiplicity theMulTarget) {
    type = theType;

    start = theStart;
    target = theTarget;

    mulstart = theMulStart;
    multarget = theMulTarget;
  }

  public Multiplicity getMulstart() {
    return mulstart;
  }

  public Multiplicity getMultarget() {
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
