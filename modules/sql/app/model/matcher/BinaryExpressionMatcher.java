package model.matcher;

import java.util.Comparator;
import java.util.List;

import model.correctionresult.WhereComparison;
import model.matching.Matcher;
import model.matching.MatchingResult;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;

public class BinaryExpressionMatcher extends Matcher<BinaryExpression, BinaryExpressionMatch> {
  
  private static Comparator<BinaryExpression> comparator = new Comparator<BinaryExpression>() {
    
    @Override
    public int compare(BinaryExpression arg0, BinaryExpression arg1) {
      // FIXME: ignore aliases of tables --> use only columnname?!?
      String exp0 = getColumnToCompare(arg0).toString();
      String exp1 = getColumnToCompare(arg1).toString();
      return exp0.compareTo(exp1);
    }
    
    private boolean compareLeftColumn(Expression leftExp, Expression rightExp) {
      return !(rightExp instanceof Column)
          || (leftExp instanceof Column && leftExp.toString().compareTo(rightExp.toString()) < 0);
    }
    
    /**
     * take expression with column as comparison argument, if there is any. if
     * both are columns, take alphabetically lesser
     *
     * @param expression
     *
     * @return
     */
    private Expression getColumnToCompare(BinaryExpression expression) {
      Expression leftExp = expression.getLeftExpression();
      Expression rightExp = expression.getRightExpression();
      if(compareLeftColumn(leftExp, rightExp))
        return leftExp;
      else
        return rightExp;
    }
    
  };
  
  public BinaryExpressionMatcher() {
    super(
        // Equals tester
        (arg0, arg1) -> comparator.compare(arg0, arg1) == 0,
        // Matching action
        BinaryExpressionMatch::new);
    
    setFilter(expression -> expression.getLeftExpression() instanceof Column
        || expression.getRightExpression() instanceof Column);
  }
  
  @Override
  protected MatchingResult<BinaryExpression, BinaryExpressionMatch> instantiateMatch(
      List<BinaryExpressionMatch> matches, List<BinaryExpression> notMatchedInFirst,
      List<BinaryExpression> notMatchedInSecond) {
    return new WhereComparison(matches, notMatchedInFirst, notMatchedInSecond);
  }
  
}