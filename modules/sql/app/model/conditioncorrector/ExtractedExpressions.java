package model.conditioncorrector;

import java.util.List;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;

public class ExtractedExpressions {
  
  private List<BinaryExpression> binaryExpressions;

  private List<Expression> otherExpressions;

  public ExtractedExpressions(List<BinaryExpression> theBinaryExpressions, List<Expression> theOtherExpressions) {
    binaryExpressions = theBinaryExpressions;
    otherExpressions = theOtherExpressions;
  }

  public List<BinaryExpression> getBinaryExpressions() {
    return binaryExpressions;
  }

  public List<Expression> getOtherExpressions() {
    return otherExpressions;
  }

  public boolean isEmpty() {
    return binaryExpressions.isEmpty() && otherExpressions.isEmpty();
  }
  
}
