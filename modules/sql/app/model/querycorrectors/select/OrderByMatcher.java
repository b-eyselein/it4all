package model.querycorrectors.select;

import java.util.List;

import model.matching.Matcher;
import model.matching.MatchingResult;
import net.sf.jsqlparser.statement.select.OrderByElement;

public class OrderByMatcher extends Matcher<OrderByElement, OrderByMatch> {

  public OrderByMatcher() {
    super((ob1, ob2) -> ob1.getExpression().toString().equals(ob2.getExpression().toString()));
  }

  public MatchingResult<OrderByElement, OrderByMatch> match(List<OrderByElement> orderByElements1,
      List<OrderByElement> orderByElements2) {
    return match("Order By-Elemente", orderByElements1, orderByElements2);
  }

  @Override
  protected OrderByMatch instantiateMatch(OrderByElement arg1, OrderByElement arg2) {
    return new OrderByMatch(arg1, arg2);
  }

}
