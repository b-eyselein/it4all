package model.matching;

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
  
  public String getBSClass() {
    return matchType == MatchType.SUCCESSFUL_MATCH ? "success" : "warning";
  }
  
  public String getExplanation() {
    switch(matchType) {
    case FAILURE:
      return "Es ist ein Fehler aufgetreten.";
    case ONLY_USER:
      return "Angegeben, aber nicht in der Musterlösung vorhanden!";
    case ONLY_SAMPLE:
      return "Nur in der Musterlösung, nicht in der Lernerlösung vorhanden!";
    case UNSUCCESSFUL_MATCH:
      return "Fehler beim Abgleich.";
    case SUCCESSFUL_MATCH:
      return "Korrekt.";
    default:
      throw new IllegalArgumentException();
    }
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
