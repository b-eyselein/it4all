package model.result;

import model.UmlAssociation;
import model.UmlClass;
import model.UmlExercise;
import model.UmlImplementation;
import model.matcher.ConnectionMatcher;
import model.matching.Matcher;

public abstract class UmlResult {
  
  protected static final Matcher.StringEqualsMatcher STRING_EQUALS_MATCHER = new Matcher.StringEqualsMatcher();
  
  protected static final Matcher<UmlClass> CLASS_MATCHER = new Matcher<>(
      (class1, class2) -> class1.getName().equals(class2.getName()));
  
  protected static final ConnectionMatcher<UmlAssociation> ASSOCIATION_MATCHER = new ConnectionMatcher<>();
  protected static final ConnectionMatcher<UmlImplementation> IMPLEMENTATION_MATCHER = new ConnectionMatcher<>();
  
  private UmlExercise exercise;
  
  public UmlResult(UmlExercise theExercise) {
    exercise = theExercise;
  }
  
  public UmlExercise getExercise() {
    return exercise;
  }
  
}
