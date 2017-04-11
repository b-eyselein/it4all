package model.matcher;

import java.util.List;

import model.UmlClass;
import model.matching.Matcher;
import model.matching.MatchingResult;

public class ClassMatcher extends Matcher<UmlClass, UmlClassMatch> {

  public ClassMatcher() {
    super(
        // Test:
        (class1, class2) -> class1.getName().equals(class2.getName()),
        // Match action:
        UmlClassMatch::new);
  }

  @Override
  protected MatchingResult<UmlClass, UmlClassMatch> instantiateMatch(List<UmlClassMatch> correct, List<UmlClass> wrong,
      List<UmlClass> missing) {
    return new MatchingResult<>(correct, wrong, missing);
  }

}
