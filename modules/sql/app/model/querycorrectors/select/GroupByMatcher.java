package model.querycorrectors.select;

import java.util.List;

import model.matching.Match;
import model.matching.Matcher;
import model.matching.MatchingResult;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;

public class GroupByMatcher extends Matcher<Expression, Match<Expression>> {
  
  public GroupByMatcher() {
    super((g1, g2) -> ((Column) g1).getColumnName().equals(((Column) g2).getColumnName()));
    // TODO Auto-generated constructor stub
  }
  
  public MatchingResult<Expression, Match<Expression>> match(List<Expression> groupByColumnReferences1,
      List<Expression> groupByColumnReferences2) {
    return match("Group By-Elemente", groupByColumnReferences1, groupByColumnReferences2);
  }
  
  @Override
  protected Match<Expression> instantiateMatch(Expression arg1, Expression arg2) {
    return new GroupByMatch(arg1, arg2);
  }
  
}
