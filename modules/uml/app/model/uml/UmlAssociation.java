package model.uml;

public class UmlAssociation {

  public static class UmlAssociationEnd {

    public String endName;
    public Multiplicity multiplicity;

  }

  public UmlAssociationType assocType;

  public UmlAssociationEnd start;
  public UmlAssociationEnd target;

  public String multsAsString() {
    return start.multiplicity.getRepresentant() + " : " + target.multiplicity.getRepresentant();
  }

  public String multsAsString(boolean switchOrder) {
    if(switchOrder)
      return target.multiplicity.getRepresentant() + " : " + start.multiplicity.getRepresentant();
    else
      return start.multiplicity.getRepresentant() + " : " + target.multiplicity.getRepresentant();
  }
}
