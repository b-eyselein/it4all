package model.querycorrectors.select;

import java.util.function.BiPredicate;

import model.matching.Matcher;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.OrderByElement;

public class OrderByMatcher extends Matcher<OrderByElement, OrderByMatch> {
  
  private static final BiPredicate<OrderByElement, OrderByElement> ORDER_BY_TEST = (ob1, ob2) -> {
    Expression exp1 = ob1.getExpression();
    Expression exp2 = ob2.getExpression();
    return exp1.toString().equals(exp2.toString());
  };
  
  public OrderByMatcher() {
    super("Order By-Elemente", ORDER_BY_TEST, OrderByMatch::new);
  }
  
}
