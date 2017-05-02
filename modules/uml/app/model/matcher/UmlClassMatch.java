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
    
    attributesResult = MATCHER.match(class1.getAttributes(), class2.getAttributes());
    methodsResult = MATCHER.match(class1.getMethods(), class2.getMethods());
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
  
}
