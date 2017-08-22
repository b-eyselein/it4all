package model.umlmatcher;

import java.util.function.BiPredicate;

import model.matching.Matcher;
import model.uml.UmlClass;

public class UmlClassMatcher extends Matcher<UmlClass, UmlClassMatch> {
  
  private static final BiPredicate<UmlClass, UmlClass> CLASS_TEST = (c1, c2) -> {
    String class1Name = c1.getName();
    String class2Name = c2.getName();
    return class1Name.equals(class2Name);
  };
  
  public UmlClassMatcher(boolean theCompareAttrAndMethods) {
    super("Klassen", CLASS_TEST, (c1, c2) -> new UmlClassMatch(c1, c2, theCompareAttrAndMethods));
  }
  
}
