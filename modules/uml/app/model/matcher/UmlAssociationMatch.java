package model.matcher;

import model.matching.Match;
import model.uml.UmlAssociation;

public class UmlAssociationMatch extends Match<UmlAssociation> {
  
  private boolean assocTypeEqual;
  
  private boolean multiplicitiesEqual;
  
  public UmlAssociationMatch(UmlAssociation assoc1, UmlAssociation assoc2) {
    super(assoc1, assoc2);
    
    assocTypeEqual = arg1.assocType == arg2.assocType;
    
    if(AssociationMatcher.endsParallelEqual(arg1, arg2))
      multiplicitiesEqual = arg1.start.multiplicity == arg2.start.multiplicity
          && arg1.target.multiplicity == arg2.target.multiplicity;
    else
      multiplicitiesEqual = arg1.start.multiplicity == arg2.target.multiplicity
          && arg1.target.multiplicity == arg2.start.multiplicity;
  }
  
  public boolean areMultiplicitiesEqual() {
    return multiplicitiesEqual;
  }
  
  public String getCorrectMultiplicity() {
    if(multiplicitiesEqual)
      return arg1.multsAsString();
    else
      return arg2.multsAsString(AssociationMatcher.endsCrossedEqual(arg1, arg2));
  }
  
  public boolean isAssocTypeEqual() {
    return assocTypeEqual;
  }
  
  public boolean isCorrect() {
    return multiplicitiesEqual && assocTypeEqual;
  }
  
}
