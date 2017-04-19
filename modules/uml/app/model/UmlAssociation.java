package model;

public class UmlAssociation extends UmlConnection {
  
  public UmlAssociationType type;
  
  public Multiplicity mulstart;
  
  public Multiplicity multarget;
  
  public Multiplicity getMulstart() {
    return mulstart;
  }
  
  public Multiplicity getMultarget() {
    return multarget;
  }
  
  public UmlAssociationType getType() {
    return type;
  }
  
}
