package model.conditioncorrector;

import java.util.Comparator;

import model.matching.Matcher;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;

public class BinaryExpressionMatcher extends Matcher<BinaryExpression, BinaryExpressionMatch> {

  private static Comparator<BinaryExpression> comparator = new Comparator<BinaryExpression>() {

    @Override
    public int compare(BinaryExpression arg0, BinaryExpression arg1) {
      // FIXME: ignore aliases of tables --> use only columnname?!?
      Expression exp1 = getColumnToCompare(arg0);
      Expression exp2 = getColumnToCompare(arg1);
      return exp1.toString().compareTo(exp2.toString());
    }

    private boolean compareLeftColumn(Expression leftExp, Expression rightExp) {
      return !(rightExp instanceof Column)
          || (leftExp instanceof Column && leftExp.toString().compareTo(rightExp.toString()) < 0);
    }

    private Expression getColumnToCompare(BinaryExpression expression) {
      Expression leftExp = expression.getLeftExpression();
      Expression rightExp = expression.getRightExpression();
      return compareLeftColumn(leftExp, rightExp) ? leftExp : rightExp;
    }

  };

  public BinaryExpressionMatcher() {
    super((arg0, arg1) -> comparator.compare(arg0, arg1) == 0);
    setFilter(expression -> expression.getLeftExpression() instanceof Column
        || expression.getRightExpression() instanceof Column);
  }

  @Override
  protected BinaryExpressionMatch instantiateMatch(BinaryExpression arg1, BinaryExpression arg2) {
    return new BinaryExpressionMatch(arg1, arg2);
  }

}