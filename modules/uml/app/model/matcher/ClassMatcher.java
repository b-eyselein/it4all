package model.matcher;

import model.matching.Match;
import model.matching.Matcher;
import model.uml.UmlClass;

public class ClassMatcher extends Matcher<UmlClass> {

  public ClassMatcher() {
    super((class1, class2) -> class1.getName().equals(class2.getName()));
  }

  @Override
  protected Match<UmlClass> instantiateMatch(UmlClass arg1, UmlClass arg2) {
    return new UmlClassMatch(arg1, arg2);
  }
}
