package model.conditioncorrector;

import java.util.Comparator;

import model.matching.Matcher;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import play.Logger;

public class BinaryExpressionMatcher extends Matcher<BinaryExpression, BinaryExpressionMatch> {

  private static final Comparator<BinaryExpression> COMPARATOR = new Comparator<BinaryExpression>() {

    @Override
    public int compare(BinaryExpression binEx1, BinaryExpression binEx2) {

      Logger.debug(binEx1.toString() + " :: " + binEx2.toString());

      Expression exp1 = getColumnToCompare(binEx1);
      Expression exp2 = getColumnToCompare(binEx2);

      // FIXME: no difference between single and double quote ( ' and " )
      String where1 = exp1.toString().replaceAll("\"", "'");
      String where2 = exp2.toString().replaceAll("\"", "'");

      Logger.debug(where1 + " :: " + where2);

      System.out.println();

      return where1.compareTo(where2);
    }

    private boolean compareLeftExpression(BinaryExpression expression) {
      Logger.debug(expression.toString());

      Expression leftExp = expression.getLeftExpression();
      Expression rightExp = expression.getRightExpression();

      Logger.debug(leftExp + " (" + leftExp.getClass() + ") <--> " + rightExp + " (" + rightExp.getClass() + ")");

      return !(rightExp instanceof Column)
          || (leftExp instanceof Column && leftExp.toString().compareTo(rightExp.toString()) < 0);
    }

    private Expression getColumnToCompare(BinaryExpression expression) {
      return compareLeftExpression(expression) ? expression.getLeftExpression() : expression.getRightExpression();
    }
  };

  public BinaryExpressionMatcher() {
    super((arg0, arg1) -> COMPARATOR.compare(arg0, arg1) == 0);

    setFilter(expression -> expression.getLeftExpression() instanceof Column
        || expression.getRightExpression() instanceof Column);
  }

  @Override
  protected BinaryExpressionMatch instantiateMatch(BinaryExpression arg1, BinaryExpression arg2) {
    return new BinaryExpressionMatch(arg1, arg2);
  }

}