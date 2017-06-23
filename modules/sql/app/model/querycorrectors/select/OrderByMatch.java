package model.querycorrectors.select;

import model.matching.Match;
import model.matching.MatchType;
import net.sf.jsqlparser.statement.select.OrderByElement;

public class OrderByMatch extends Match<OrderByElement> {

  public OrderByMatch(OrderByElement theArg1, OrderByElement theArg2) {
    super(theArg1, theArg2);
  }
  
  @Override
  protected MatchType analyze(OrderByElement theArg1, OrderByElement theArg2) {
    if(theArg1.isAsc() == theArg2.isAsc())
      return MatchType.SUCCESSFUL_MATCH;
    else
      return MatchType.UNSUCCESSFUL_MATCH;
  }

}