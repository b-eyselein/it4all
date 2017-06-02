package model.matcher;

import model.matching.Match;
import model.matching.Matcher;
import model.matching.MatchingResult;
import model.uml.UmlClass;

public class UmlClassMatch extends Match<UmlClass> {

  private static final Matcher.StringEqualsMatcher MATCHER = new Matcher.StringEqualsMatcher();

  private MatchingResult<String> attributesResult;
  private MatchingResult<String> methodsResult;

  public UmlClassMatch(UmlClass class1, UmlClass class2) {
    super(class1, class2);

  }

  public MatchingResult<String> getAttributesResult() {
    return attributesResult;
  }

  public String getClassName() {
    return arg1.getName();
  }

  public MatchingResult<String> getMethodsResult() {
    return methodsResult;
  }

  @Override
  protected boolean analyze(UmlClass theArg1, UmlClass theArg2) {
    attributesResult = MATCHER.match("Attribute", theArg1.getAttributes(), theArg2.getAttributes());
    methodsResult = MATCHER.match("Methoden", theArg1.getMethods(), theArg2.getMethods());
    return attributesResult.isSuccessful() && methodsResult.isSuccessful();
  }

}
