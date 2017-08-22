package model.conditioncorrector;

import java.util.Map;

import model.StringConsts;
import model.matching.Matcher;
import model.matching.MatchingResult;
import net.sf.jsqlparser.expression.Expression;

public class ExpressionMatcher extends Matcher<Expression, ExpressionMatch> {
  
  public ExpressionMatcher(Map<String, String> theUserTableAliases, Map<String, String> theSampleTableAliases) {
    super(StringConsts.CONDITIONS_NAME, new ExpressionBiPredicate(theUserTableAliases, theSampleTableAliases),
        ExpressionMatch::new);
  }
  
  public MatchingResult<Expression, ExpressionMatch> matchExpressions(ExtractedExpressions userExps,
      ExtractedExpressions sampleExps) {
    MatchingResult<Expression, ExpressionMatch> binMatch = match(userExps.getBinaryExpressions(),
        sampleExps.getBinaryExpressions());
    
    MatchingResult<Expression, ExpressionMatch> singleMatch = match(userExps.getOtherExpressions(),
        sampleExps.getOtherExpressions());
    
    return MatchingResult.merge(binMatch, singleMatch);
  }
  
}