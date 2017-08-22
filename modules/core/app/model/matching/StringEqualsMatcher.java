package model.matching;

public class StringEqualsMatcher extends Matcher<String, Match<String>> {
  
  public StringEqualsMatcher(String matchName) {
    super(matchName, String::equals, GenericMatch::new);
  }
  
}