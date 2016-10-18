package model.matcher;

import model.exercise.FeedbackLevel;
import model.exercise.Success;
import model.matching.Match;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;

public class BinaryExpressionMatch extends Match<BinaryExpression> {
  
  private static boolean compareLeftColumn(Expression leftExp, Expression rightExp) {
    return !(rightExp instanceof Column)
        || (leftExp instanceof Column && leftExp.toString().compareTo(rightExp.toString()) < 0);
  }
  
  public BinaryExpressionMatch(BinaryExpression theArg1, BinaryExpression theArg2) {
    super(FeedbackLevel.MEDIUM_FEEDBACK, theArg1, theArg2);
  }
  
  @Override
  public void analyze() {
    Expression arg1Left = arg1.getLeftExpression();
    Expression arg1Right = arg1.getRightExpression();

    Expression arg2Left = arg2.getLeftExpression();
    Expression arg2Right = arg2.getRightExpression();
    
    Expression toMatch1 = arg1Left;
    Expression toMatch2 = arg2Left;
    
    String firstType = arg1.getStringExpression();
    String secondType = arg2.getStringExpression();
    
    if(compareLeftColumn(arg1Left, arg1Right))
      toMatch1 = arg1Right;
    
    if(compareLeftColumn(arg2Left, arg2Right))
      toMatch2 = arg2Right;
    
    if(toMatch1.toString().equals(toMatch2.toString())) {
      success = Success.PARTIALLY;
      if(firstType.equals(secondType))
        success = Success.COMPLETE;
    }
  }
  
  @Override
  public String getAsHtml() {
    // TODO: genauere Beschreibung Resultat?!?
    String ret = "<div class=\"col-md-6\">";
    ret += "<div class=\"alert alert-" + getBSClass() + "\">";
    ret += "<p>Ihre Bedingung: <code>" + arg1 + "</code></p>";
    ret += "<p>Musterlösung: <code>" + arg2 + "</code></p>";
    ret += "</div></div>";
    return ret;
  }
  
}
