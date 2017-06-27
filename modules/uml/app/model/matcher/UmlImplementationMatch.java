package model.matcher;

import model.matching.Match;
import model.matching.MatchType;
import model.uml.UmlImplementation;

public class UmlImplementationMatch extends Match<UmlImplementation> {
  
  public UmlImplementationMatch(UmlImplementation theArg1, UmlImplementation theArg2) {
    super(theArg1, theArg2);
  }
  
  @Override
  protected MatchType analyze(UmlImplementation theArg1, UmlImplementation theArg2) {
    // TODO Auto-generated method stub
    return MatchType.UNSUCCESSFUL_MATCH;
  }
  
}
