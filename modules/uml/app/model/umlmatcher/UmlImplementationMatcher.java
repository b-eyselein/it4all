package model.umlmatcher;

import model.matching.Matcher;
import model.uml.UmlImplementation;

public class UmlImplementationMatcher extends Matcher<UmlImplementation, UmlImplementationMatch> {
  
  public UmlImplementationMatcher() {
    super("Vererbungsbeziehungen", UmlImplementation::equals, UmlImplementationMatch::new);
  }
  
}
