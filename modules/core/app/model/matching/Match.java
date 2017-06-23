package model.matching;

import play.twirl.api.Html;

public abstract class Match<T> {
  
  protected T userArg;
  protected T sampleArg;
  
  protected MatchType matchType = MatchType.FAILURE;
  
  public Match(T theUserArg, T theSampleArg) {
    userArg = theUserArg;
    sampleArg = theSampleArg;
    
    if(theUserArg == null && theSampleArg != null)
      matchType = MatchType.ONLY_SAMPLE;
    else if(theUserArg != null && theSampleArg == null)
      matchType = MatchType.ONLY_USER;
    else
      matchType = analyze(theUserArg, theSampleArg);
  }
  
  public Html describe() {
    return views.html.matchResult.render(this);
  }
  
  public String getBSClass() {
    return matchType == MatchType.SUCCESSFUL_MATCH ? "success" : "warning";
  }
  
  public MatchType getMatchType() {
    return matchType;
  }
  
  public T getSampleArg() {
    return sampleArg;
  }
  
  public T getUserArg() {
    return userArg;
  }
  
  public boolean isSuccessful() {
    return matchType == MatchType.SUCCESSFUL_MATCH;
  }
  
  protected abstract MatchType analyze(T theArg1, T theArg2);
  
}
