package model.matching;

import play.twirl.api.Html;

public class GenericMatch<T> extends Match<T> {

  public GenericMatch(T theArg1, T theArg2) {
    super(theArg1, theArg2);
  }

  @Override
  public Html describe() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected MatchType analyze(T theArg1, T theArg2) {
    if(theArg1.equals(theArg2))
      return MatchType.SUCCESSFUL_MATCH;
    else
      return MatchType.UNSUCCESSFUL_MATCH;
  }

}
