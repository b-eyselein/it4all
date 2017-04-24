package model.uml;

import java.util.List;

public class UmlAssociation {

  private UmlAssociationType assocType;

  private List<UmlAssociationEnd> ends;

  public UmlAssociation() {
    // Dummy constructor for Json.fromJson(...)
  }

  public UmlAssociation(UmlAssociationType theAssocType, List<UmlAssociationEnd> theEnds) {
    assocType = theAssocType;
    ends = theEnds;
  }

  private static String multAsString(Multiplicity mul1, Multiplicity mul2) {
    return mul1.getRepresentant() + " : " + mul2.getRepresentant();
  }

  public UmlAssociationType getAssocType() {
    return assocType;
  }

  public List<UmlAssociationEnd> getEnds() {
    return ends;
  }

  public String multsAsString() {
    // FIXME: refactor...
    return multAsString(ends.get(0).getMultiplicity(), ends.get(1).getMultiplicity());
  }

  public String multsAsString(boolean switchOrder) {
    if(switchOrder)
      return multAsString(ends.get(1).getMultiplicity(), ends.get(0).getMultiplicity());
    else
      return multAsString(ends.get(0).getMultiplicity(), ends.get(1).getMultiplicity());
  }

  public void setAssocType(UmlAssociationType theAssocType) {
    assocType = theAssocType;
  }

  public void setEnds(List<UmlAssociationEnd> theEnds) {
    if(theEnds.size() != 2)
      throw new IllegalArgumentException("There are not 2 ends! Reality: " + theEnds.size());

    ends = theEnds;
  }
}
