package model.conditioncorrector;

import java.util.Map;

import model.StringConsts;
import model.matching.Match;
import model.matching.Matcher;
import model.matching.MatchingResult;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;

public class ExpressionMatcher extends Matcher<Expression, Match<Expression>> {
  
  public ExpressionMatcher(Map<String, String> theUserTableAliases, Map<String, String> theSampleTableAliases) {
    super(new ExpressionBiPredicate(theUserTableAliases, theSampleTableAliases),
        // FILTER:
        expression -> {
          if(expression instanceof BinaryExpression)
            return ((BinaryExpression) expression).getLeftExpression() instanceof Column
                || ((BinaryExpression) expression).getRightExpression() instanceof Column;
          else
            return true;
        });
  }
  
  public MatchingResult<Expression, Match<Expression>> match(ExtractedExpressions userExps,
      ExtractedExpressions sampleExps) {
    MatchingResult<Expression, Match<Expression>> binMatch = match(StringConsts.CONDITIONS_NAME,
        userExps.getBinaryExpressions(), sampleExps.getBinaryExpressions());

    MatchingResult<Expression, Match<Expression>> singleMatch = match(StringConsts.CONDITIONS_NAME,
        userExps.getOtherExpressions(), sampleExps.getOtherExpressions());

    return MatchingResult.merge(binMatch, singleMatch);
  }
  
  @Override
  protected ExpressionMatch instantiateMatch(Expression arg1, Expression arg2) {
    return new ExpressionMatch(arg1, arg2);
  }
  
}