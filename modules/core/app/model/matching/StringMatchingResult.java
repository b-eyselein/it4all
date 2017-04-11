package model.matching;

import java.util.List;

public class StringMatchingResult extends MatchingResult<String, StringMatch> {
  
  public StringMatchingResult(List<StringMatch> theMatches, List<String> theWrong, List<String> theMissing) {
    super(theMatches, theWrong, theMissing);
  }
  
}
