package model.matcher;

import model.matching.Match;
import model.matching.MatchType;
import model.matching.Matcher;
import model.matching.MatchingResult;
import model.uml.UmlClass;
import play.twirl.api.Html;

public class UmlClassMatch extends Match<UmlClass> {
  
  private MatchingResult<String, Match<String>> attributesResult;
  private MatchingResult<String, Match<String>> methodsResult;
  private boolean compareAttrAndMethods;

  public UmlClassMatch(UmlClass class1, UmlClass class2, boolean theCompareAttrAndMethods) {
    super(class1, class2);
    compareAttrAndMethods = theCompareAttrAndMethods;
  }

  @Override
  public Html describe() {
    return views.html.matchResult.render(this);
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
    attributesResult = Matcher.STRING_EQ_MATCHER.match("Attribute", theArg1.getAttributes(), theArg2.getAttributes());
    methodsResult = Matcher.STRING_EQ_MATCHER.match("Methoden", theArg1.getMethods(), theArg2.getMethods());
    
    if(!compareAttrAndMethods || (attributesResult.isSuccessful() && methodsResult.isSuccessful()))
      return MatchType.SUCCESSFUL_MATCH;
    else
      return MatchType.UNSUCCESSFUL_MATCH;
  }

}
