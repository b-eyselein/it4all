package model.conditioncorrector;

import model.matching.Match;
import model.matching.MatchType;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import play.twirl.api.Html;

public class BinaryExpressionMatch extends Match<BinaryExpression> {
  
  public BinaryExpressionMatch(BinaryExpression theArg1, BinaryExpression theArg2) {
    super(theArg1, theArg2);
  }
  
  private static boolean columnsEqual(String col1, String col2) {
    return col1.equals(col2);
  }
  
  private static boolean compareLeftColumn(Expression leftExp, Expression rightExp) {
    return !(rightExp instanceof Column)
        || (leftExp instanceof Column && leftExp.toString().compareTo(rightExp.toString()) < 0);
  }
  
  @Override
  public Html describe() {
    return views.html.matchResult.render(this);
  }
  
  @Override
  protected MatchType analyze(BinaryExpression theArg1, BinaryExpression theArg2) {
    // FIXME: test...
    Expression arg1Left = theArg1.getLeftExpression();
    Expression arg1Right = theArg1.getRightExpression();
    
    Expression arg2Left = theArg2.getLeftExpression();
    Expression arg2Right = theArg2.getRightExpression();
    
    Expression toMatch1 = compareLeftColumn(arg1Left, arg1Right) ? arg1Left : arg1Right;
    Expression toMatch2 = compareLeftColumn(arg2Left, arg2Right) ? arg2Left : arg2Right;
    
    String firstType = theArg1.getStringExpression();
    String secondType = theArg2.getStringExpression();
    
    if(columnsEqual(toMatch1.toString(), toMatch2.toString()) && firstType.equals(secondType))
      return MatchType.SUCCESSFUL_MATCH;
    else
      return MatchType.UNSUCCESSFUL_MATCH;
  }
  
}
