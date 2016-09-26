package model.queryCorrectors.where;

import java.util.List;

import model.result.Matcher;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.schema.Column;

class BinaryExpressionMatcher extends Matcher<BinaryExpression> {

  public BinaryExpressionMatcher(List<BinaryExpression> theFirstCollection,
      List<BinaryExpression> theSecondCollection) {
    super(theFirstCollection, theSecondCollection);

    setComparator(new BinaryExpressionComparator());

    setMatchedAction((arg1, arg2) -> new BinaryExpressionMatch(arg1, arg2));

    setFilter(expression -> {
      return expression.getLeftExpression() instanceof Column || expression.getRightExpression() instanceof Column;
    });
  }

}