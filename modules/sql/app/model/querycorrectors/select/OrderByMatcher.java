package model.querycorrectors.select;

import java.util.List;

import model.matching.Matcher;
import model.matching.MatchingResult;
import net.sf.jsqlparser.statement.select.OrderByElement;

public class OrderByMatcher extends Matcher<OrderByElement, OrderByMatch> {

  public OrderByMatcher() {
    super((ob1, ob2) -> {
      String column1 = ob1.getExpression().toString();
      String column2 = ob2.getExpression().toString();
      return column1.equals(column2) && (ob1.isAsc() == ob2.isAsc());
    });
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
