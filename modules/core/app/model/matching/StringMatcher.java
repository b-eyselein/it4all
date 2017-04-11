package model.matching;

import java.util.List;

public class StringMatcher extends Matcher<String, StringMatch> {
  
  public StringMatcher() {
    super((str1, str2) -> str1.equals(str2), StringMatch::new);
  }
  
  @Override
  protected MatchingResult<String, StringMatch> instantiateMatch(List<StringMatch> correct, List<String> wrong,
      List<String> missing) {
    return new MatchingResult<>(correct, wrong, missing);
  }
  
}
