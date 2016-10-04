package model.queryCorrectors.where;

import model.exercise.Success;
import model.result.Match;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;

public class BinaryExpressionMatch extends Match<BinaryExpression> {

  public BinaryExpressionMatch(BinaryExpression theArg1, BinaryExpression theArg2) {
    super(theArg1, theArg2);
  }

  @Override
  public void analyze() {
    Expression arg1Left = arg1.getLeftExpression(), arg1Right = arg1.getRightExpression();
    Expression arg2Left = arg2.getLeftExpression(), arg2Right = arg2.getRightExpression();

    Expression toMatch1 = arg1Left;
    Expression toMatch2 = arg2Left;

    String firstType = arg1.getStringExpression();
    String secondType = arg2.getStringExpression();

    if(BinaryExpressionComparator.compareLeftColumn(arg1Left, arg1Right))
      toMatch1 = arg1Right;

    if(BinaryExpressionComparator.compareLeftColumn(arg2Left, arg2Right))
      toMatch2 = arg2Right;

    if(toMatch1.toString().equals(toMatch2.toString())) {
      success = Success.PARTIALLY;
      if(firstType.equals(secondType))
        success = Success.COMPLETE;
    }
  }

  @Override
  public String getAsHtml() {
    // FIXME: implement result!
    String ret = "<div class=\"alert alert-" + getBSClass() + "\">";
    ret += "<p>Ihre Bedingung: <code>" + arg1 + "</code></p>";
    ret += "<p>Musterlösung: <code>" + arg2 + "</code></p>";
    ret += "</div>";
    return ret;
  }

}
