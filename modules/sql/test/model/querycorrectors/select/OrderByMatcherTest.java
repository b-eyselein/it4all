package model.querycorrectors.select;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import model.matching.Match;
import model.matching.MatchingResult;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

public class OrderByMatcherTest {
  
  private static final OrderByMatcher MATCHER = new OrderByMatcher();
  
  private static final String QUERY = "SELECT * FROM table ORDER BY username ASC";
  
  @Test
  public void test() throws JSQLParserException {
    
    Select select = (Select) CCJSqlParserUtil.parse(QUERY);
    
    List<OrderByElement> orderByElements = ((PlainSelect) select.getSelectBody()).getOrderByElements();
    MatchingResult<OrderByElement, Match<OrderByElement>> match = MATCHER.match(orderByElements, orderByElements);
    
    assertNotNull(match);
  }
  
}
