package model.conditioncorrector;

import java.util.Map;

import model.matching.Matcher;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.schema.Column;

public class BinaryExpressionMatcher extends Matcher<BinaryExpression, BinaryExpressionMatch> {
  
  public BinaryExpressionMatcher(Map<String, String> theUserTableAliases, Map<String, String> theSampleTableAliases) {
    super(new BinaryExpressionBiPredicate(theUserTableAliases, theSampleTableAliases),
        expression -> expression.getLeftExpression() instanceof Column
            || expression.getRightExpression() instanceof Column);
  }
  
  @Override
  protected BinaryExpressionMatch instantiateMatch(BinaryExpression arg1, BinaryExpression arg2) {
    return new BinaryExpressionMatch(arg1, arg2);
  }
  
}