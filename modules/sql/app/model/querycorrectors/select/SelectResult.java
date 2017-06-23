package model.querycorrectors.select;

import model.matching.Match;
import model.matching.MatchingResult;
import model.querycorrectors.SqlResult;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import play.twirl.api.Html;

public class SelectResult extends SqlResult<Select, SelectItem> {
  
  private MatchingResult<Expression, Match<Expression>> groupByComparison;
  
  private MatchingResult<OrderByElement, OrderByMatch> orderByComparison;
  
  public SelectResult(String theLearnerSolution) {
    super(theLearnerSolution);
  }
  
  public MatchingResult<Expression, Match<Expression>> getGroupByComparison() {
    return groupByComparison;
  }
  
  public MatchingResult<OrderByElement, OrderByMatch> getOrderByComparison() {
    return orderByComparison;
  }
  
  @Override
  public SqlResult<Select, SelectItem> makeOtherComparisons(Select userQ, Select sampleQ) {
    groupByComparison = SelectCorrector.compareGroupByElements(userQ, sampleQ);
    orderByComparison = SelectCorrector.compareOrderByElements(userQ, sampleQ);
    return this;
  }
  
  @Override
  public Html render() {
    return views.html.resultTemplates.selectResult.render(this);
  }

}
