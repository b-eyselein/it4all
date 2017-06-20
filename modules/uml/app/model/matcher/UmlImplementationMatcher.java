package model.matcher;

import model.matching.Matcher;
import model.uml.UmlImplementation;

public class UmlImplementationMatcher extends Matcher<UmlImplementation, UmlImplementationMatch> {

  public UmlImplementationMatcher() {
    super(UmlImplementation::equals);
  }

  @Override
  protected UmlImplementationMatch instantiateMatch(UmlImplementation arg1, UmlImplementation arg2) {
    return new UmlImplementationMatch(arg1, arg2);
  }

}
