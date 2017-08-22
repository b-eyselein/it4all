package model.querycorrectors.select;

import java.util.function.BiPredicate;

import model.matching.Match;
import model.matching.Matcher;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;

public class GroupByMatcher extends Matcher<Expression, Match<Expression>> {
  
  private static final BiPredicate<Expression, Expression> GROUP_BY_TEST = (g1, g2) -> {
    Column c1 = (Column) g1;
    Column c2 = (Column) g2;
    return c1.getColumnName().equals(c2.getColumnName());
  };
  
  public GroupByMatcher() {
    super("Group By-Elemente", GROUP_BY_TEST, GroupByMatch::new);
  }
  
}
