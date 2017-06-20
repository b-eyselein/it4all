package model.querycorrectors.select;

import model.matching.Match;
import net.sf.jsqlparser.statement.select.OrderByElement;

public class OrderByMatch extends Match<OrderByElement> {

  public OrderByMatch(OrderByElement theArg1, OrderByElement theArg2) {
    super(theArg1, theArg2);
  }
  
  @Override
  protected boolean analyze(OrderByElement theArg1, OrderByElement theArg2) {
    // TODO Auto-generated method stub
    return false;
  }
  
}
