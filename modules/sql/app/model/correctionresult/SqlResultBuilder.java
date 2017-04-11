package model.correctionresult;

import model.matcher.BinaryExpressionMatch;
import model.matching.MatchingResult;
import net.sf.jsqlparser.expression.BinaryExpression;

public class SqlResultBuilder {

  private ComparisonTwoListsOfStrings columnComparison;

  private ComparisonTwoListsOfStrings tableComparison;

  private MatchingResult<BinaryExpression, BinaryExpressionMatch> whereComparison;

  private ComparisonTwoListsOfStrings orderByComparison;

  private ComparisonTwoListsOfStrings groupByComparison;

  private SqlExecutionResult executionResult;

  public SqlResult build() {
    // @formatter:off
    return new SqlResult(
        columnComparison,
        tableComparison,
        whereComparison,
        orderByComparison,
        groupByComparison,
        executionResult);
    // @formatter:on
  }

  public SqlResultBuilder setColumnComparison(ComparisonTwoListsOfStrings theColumnComparison) {
    columnComparison = theColumnComparison;
    return this;
  }

  public SqlResultBuilder setExecutionResult(SqlExecutionResult theExecutionResult) {
    executionResult = theExecutionResult;
    return this;
  }

  public SqlResultBuilder setGroupByComparison(ComparisonTwoListsOfStrings theGroupByComparison) {
    groupByComparison = theGroupByComparison;
    return this;
  }

  public SqlResultBuilder setOrderByComparison(ComparisonTwoListsOfStrings theOrderByComparison) {
    orderByComparison = theOrderByComparison;
    return this;
  }

  public SqlResultBuilder setTableComparison(ComparisonTwoListsOfStrings theTableComparison) {
    tableComparison = theTableComparison;
    return this;
  }

  public SqlResultBuilder setWhereComparison(
      MatchingResult<BinaryExpression, BinaryExpressionMatch> theWhereComparison) {
    whereComparison = theWhereComparison;
    return this;
  }
}
