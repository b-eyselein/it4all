package model.matching;

import java.util.List;

import model.exercise.EvaluationResult;
import model.exercise.FeedbackLevel;
import model.exercise.Success;

public abstract class MatchingResult<T, U extends Match<T>> extends EvaluationResult {
  
  // FIXME: implement feedbackLevel!
  protected List<U> matches;
  protected List<T> notMatchedInFirst;
  protected List<T> notMatchedInSecond;
  
  public MatchingResult(FeedbackLevel theMinimalFL, List<U> theMatches, List<T> theNotMatchedInFirst,
      List<T> theNotMatchedInSecond) {
    super(theMinimalFL, Success.NONE);
    matches = theMatches;
    notMatchedInFirst = theNotMatchedInFirst;
    notMatchedInSecond = theNotMatchedInSecond;
    
    analyze();
  }
  
  public List<U> getMatches() {
    return matches;
  }
  
  public List<T> getNotMatchedInFirst() {
    return notMatchedInFirst;
  }
  
  public List<T> getNotMatchedInSecond() {
    return notMatchedInSecond;
  }
  
  protected void analyze() {
    boolean allMatched = notMatchedInFirst.isEmpty() && notMatchedInSecond.isEmpty();
    boolean matchesOk = true;
    for(Match<T> match: matches)
      if(match.getSuccess() != Success.COMPLETE)
        matchesOk = false;
      
    if(allMatched && matchesOk)
      success = Success.COMPLETE;
    else if(allMatched || matchesOk)
      success = Success.PARTIALLY;
    else
      success = Success.NONE;
  }
}
