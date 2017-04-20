package model.matcher;

import java.util.List;

import model.matching.Match;
import model.matching.Matcher;
import model.uml.UmlClass;

public class ClassMatcher extends Matcher<UmlClass> {

  public ClassMatcher() {
    super((class1, class2) -> class1.name.equals(class2.name));
  }

  @Override
  protected void instantiateMatch(List<Match<UmlClass>> matches, UmlClass arg1, UmlClass arg2) {
    matches.add(new UmlClassMatch(arg1, arg2));
  }
}
