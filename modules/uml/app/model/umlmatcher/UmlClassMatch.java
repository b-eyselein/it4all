package model.umlmatcher;

import model.matching.Match;
import model.matching.MatchType;
import model.matching.MatchingResult;
import model.matching.StringEqualsMatcher;
import model.uml.UmlClass;

public class UmlClassMatch extends Match<UmlClass> {
  
  private static final StringEqualsMatcher ATTRS_MATCHER = new StringEqualsMatcher("Attribute");
  private static final StringEqualsMatcher METHODS_MATCHER = new StringEqualsMatcher("Methoden");
  
  private MatchingResult<String, Match<String>> attributesResult;
  private MatchingResult<String, Match<String>> methodsResult;
  private boolean compareAttrAndMethods;
  
  public UmlClassMatch(UmlClass class1, UmlClass class2, boolean theCompareAttrAndMethods) {
    super(class1, class2);
    compareAttrAndMethods = theCompareAttrAndMethods;
  }
  
  public MatchingResult<String, Match<String>> getAttributesResult() {
    return attributesResult;
  }
  
  public String getClassName() {
    return userArg.getName();
  }
  
  public MatchingResult<String, Match<String>> getMethodsResult() {
    return methodsResult;
  }
  
  @Override
  protected MatchType analyze(UmlClass theArg1, UmlClass theArg2) {
    attributesResult = ATTRS_MATCHER.match(theArg1.getAttributes(), theArg2.getAttributes());
    methodsResult = METHODS_MATCHER.match(theArg1.getMethods(), theArg2.getMethods());
    
    if(!compareAttrAndMethods || (attributesResult.isSuccessful() && methodsResult.isSuccessful()))
      return MatchType.SUCCESSFUL_MATCH;
    else
      return MatchType.UNSUCCESSFUL_MATCH;
  }
  
}
