package model.queryCorrectors.where;

import java.util.Comparator;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;

public class BinaryExpressionComparator implements Comparator<BinaryExpression> {
  
  public static boolean compareLeftColumn(Expression leftExp, Expression rightExp) {
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
  private static Expression getColumnToCompare(BinaryExpression expression) {
    Expression leftExp = expression.getLeftExpression(), rightExp = expression.getRightExpression();
    if(compareLeftColumn(leftExp, rightExp))
      return leftExp;
    else
      return rightExp;
  }
  
  @Override
  public int compare(BinaryExpression arg0, BinaryExpression arg1) {
    String exp0 = getColumnToCompare(arg1).toString();
    String exp1 = getColumnToCompare(arg1).toString();
    // FIXME: test!
    return exp0.compareTo(exp1);
  }
  
}
