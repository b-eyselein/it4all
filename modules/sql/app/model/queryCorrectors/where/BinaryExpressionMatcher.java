package model.queryCorrectors.where;

import java.util.Comparator;
import java.util.List;

import model.correctionResult.WhereComparison;
import model.result.Match;
import model.result.Matcher;
import model.result.MatchingResult;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.schema.Column;

class BinaryExpressionMatcher extends Matcher<BinaryExpression> {
  
  private static Comparator<BinaryExpression> comparator = new BinaryExpressionComparator();
  
  public BinaryExpressionMatcher() {
    super(
        // Equals tester
        (arg0, arg1) -> comparator.compare(arg0, arg1) == 0,
        // Matching action
        (arg1, arg2) -> new BinaryExpressionMatch(arg1, arg2));
    
    setFilter(expression -> {
      return expression.getLeftExpression() instanceof Column || expression.getRightExpression() instanceof Column;
    });
  }
  
  @Override
  protected MatchingResult<BinaryExpression> instantiateMatch(List<Match<BinaryExpression>> matches,
      List<BinaryExpression> notMatchedInFirst, List<BinaryExpression> notMatchedInSecond) {
    // TODO Auto-generated method stub
    return new WhereComparison(matches, notMatchedInFirst, notMatchedInSecond);
  }
  
}