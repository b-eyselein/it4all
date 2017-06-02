package model.matcher;

import model.matching.Match;
import model.uml.UmlAssociation;

public class UmlAssociationMatch extends Match<UmlAssociation> {

  private boolean assocTypeEqual;

  private boolean multiplicitiesEqual;

  public UmlAssociationMatch(UmlAssociation assoc1, UmlAssociation assoc2) {
    super(assoc1, assoc2);
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

  @Override
  protected boolean analyze(UmlAssociation assoc1, UmlAssociation assoc2) {
    assocTypeEqual = arg1.getAssocType() == arg2.getAssocType();

    if(AssociationMatcher.endsParallelEqual(arg1, arg2))
      multiplicitiesEqual = arg1.getEnds().get(0).getMultiplicity() == arg2.getEnds().get(0).getMultiplicity()
          && arg1.getEnds().get(1).getMultiplicity() == arg2.getEnds().get(1).getMultiplicity();
    else
      multiplicitiesEqual = arg1.getEnds().get(0).getMultiplicity() == arg2.getEnds().get(1).getMultiplicity()
          && arg1.getEnds().get(1).getMultiplicity() == arg2.getEnds().get(0).getMultiplicity();

    return assocTypeEqual && multiplicitiesEqual;
  }

}
