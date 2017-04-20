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
    
    attributesResult = MATCHER.match(class1.attributes, class2.attributes);
    methodsResult = MATCHER.match(class1.methods, class2.methods);
  }
  
  public MatchingResult<String> getAttributesResult() {
    return attributesResult;
  }
  
  public String getClassName() {
    return arg1.name;
  }
  
  public MatchingResult<String> getMethodsResult() {
    return methodsResult;
  }
  
}
