package model.umlmatcher;

import model.matching.Match;
import model.matching.MatchType;
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
      return userArg.multsAsString();
    else
      return sampleArg.multsAsString(UmlAssociationMatcher.endsCrossedEqual(userArg, sampleArg));
  }
  
  public boolean isAssocTypeEqual() {
    return assocTypeEqual;
  }
  
  public boolean isCorrect() {
    return multiplicitiesEqual && assocTypeEqual;
  }
  
  @Override
  protected MatchType analyze(UmlAssociation assoc1, UmlAssociation assoc2) {
    assocTypeEqual = userArg.getAssocType() == sampleArg.getAssocType();
    
    if(UmlAssociationMatcher.endsParallelEqual(userArg, sampleArg))
      multiplicitiesEqual = userArg.getEnds().get(0).getMultiplicity() == sampleArg.getEnds().get(0).getMultiplicity()
          && userArg.getEnds().get(1).getMultiplicity() == sampleArg.getEnds().get(1).getMultiplicity();
    else
      multiplicitiesEqual = userArg.getEnds().get(0).getMultiplicity() == sampleArg.getEnds().get(1).getMultiplicity()
          && userArg.getEnds().get(1).getMultiplicity() == sampleArg.getEnds().get(0).getMultiplicity();
    
    if(assocTypeEqual && multiplicitiesEqual)
      return MatchType.SUCCESSFUL_MATCH;
    else
      return MatchType.UNSUCCESSFUL_MATCH;
  }
  
}
