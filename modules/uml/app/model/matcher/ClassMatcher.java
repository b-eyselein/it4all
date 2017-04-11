package model.matcher;

import java.util.List;

import model.UmlClass;
import model.matching.Matcher;

public class ClassMatcher extends Matcher<UmlClass, UmlClassMatch, ClassMatchingResult> {
  
  public ClassMatcher() {
    super(
        // Test:
        (class1, class2) -> class1.getName().equals(class2.getName()),
        // Match action:
        UmlClassMatch::new);
  }
  
  @Override
  protected ClassMatchingResult instantiateMatch(List<UmlClassMatch> matches, List<UmlClass> firstCollection,
      List<UmlClass> secondCollection) {
    return new ClassMatchingResult(matches, firstCollection, secondCollection);
  }
  
}
