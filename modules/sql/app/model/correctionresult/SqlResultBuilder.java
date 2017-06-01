package model.correctionresult;

import model.matching.MatchingResult;
import net.sf.jsqlparser.expression.BinaryExpression;

public class SqlResultBuilder {

  private String learnerSolution;

  private MatchingResult<String> columnComparison;

  private MatchingResult<String> tableComparison;

  private MatchingResult<String> orderByComparison;

  private MatchingResult<String> groupByComparison;

  private MatchingResult<BinaryExpression> whereComparison;

  private SqlExecutionResult executionResult;

  public SqlResult build() {
    // @formatter:off
    return new SqlResult(
        learnerSolution,
        columnComparison,
        tableComparison,
        orderByComparison,
        groupByComparison,
        whereComparison,
        executionResult);
    // @formatter:on
  }

  public SqlResultBuilder setColumnComparison(MatchingResult<String> theColumnComparison) {
    columnComparison = theColumnComparison;
    return this;
  }

  public SqlResultBuilder setExecutionResult(SqlExecutionResult theExecutionResult) {
    executionResult = theExecutionResult;
    return this;
  }

  public SqlResultBuilder setGroupByComparison(MatchingResult<String> theGroupByComparison) {
    groupByComparison = theGroupByComparison;
    return this;
  }

  public SqlResultBuilder setLearnerSolution(String theLearnerSolution) {
    learnerSolution = theLearnerSolution;
    return this;
  }

  public SqlResultBuilder setOrderByComparison(MatchingResult<String> theOrderByComparison) {
    orderByComparison = theOrderByComparison;
    return this;
  }

  public SqlResultBuilder setTableComparison(MatchingResult<String> theTableComparison) {
    tableComparison = theTableComparison;
    return this;
  }

  public SqlResultBuilder setWhereComparison(MatchingResult<BinaryExpression> theWhereComparison) {
    whereComparison = theWhereComparison;
    return this;
  }
}
