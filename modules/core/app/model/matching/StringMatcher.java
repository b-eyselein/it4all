package model.matching;

import java.util.List;

public class StringMatcher extends Matcher<String, StringMatch, StringMatchingResult> {

  public StringMatcher() {
    super((str1, str2) -> str1.equals(str2), StringMatch::new);
  }

  @Override
  protected StringMatchingResult instantiateMatch(List<StringMatch> matches, List<String> firstCollection,
      List<String> secondCollection) {
    return new StringMatchingResult(matches, firstCollection, secondCollection);
  }

}
