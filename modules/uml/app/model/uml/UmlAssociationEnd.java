package model.uml;

public class UmlAssociationEnd {
  
  private String endName;
  
  private Multiplicity multiplicity;
  
  public UmlAssociationEnd() {
    // Dummy constructor for Json.fromJson(...)
  }

  public UmlAssociationEnd(String theEndName, Multiplicity theMultiplicity) {
    endName = theEndName;
    multiplicity = theMultiplicity;
  }
  
  public String getEndName() {
    return endName;
  }
  
  public Multiplicity getMultiplicity() {
    return multiplicity;
  }
  
  public void setEndName(String theEndName) {
    endName = theEndName;
  }
  
  public void setMultiplicity(Multiplicity theMultiplicity) {
    multiplicity = theMultiplicity;
  }
  
}
