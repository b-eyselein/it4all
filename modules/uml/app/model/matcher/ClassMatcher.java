package model.matcher;

import model.matching.Matcher;
import model.uml.UmlClass;

public class ClassMatcher extends Matcher<UmlClass, UmlClassMatch> {

  public ClassMatcher() {
    super((class1, class2) -> class1.getName().equals(class2.getName()));
  }

  @Override
  protected UmlClassMatch instantiateMatch(UmlClass arg1, UmlClass arg2) {
    return new UmlClassMatch(arg1, arg2);
  }
}
