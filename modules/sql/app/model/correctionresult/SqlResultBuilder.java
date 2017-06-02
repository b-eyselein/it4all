package model.correctionresult;

import model.matching.MatchingResult;
import net.sf.jsqlparser.expression.BinaryExpression;

public class SqlResultBuilder<C> {
  
  private String learnerSolution;
  
  private MatchingResult<C> columnComparison;
  
  private MatchingResult<String> tableComparison;
  
  private MatchingResult<String> orderByComparison;
  
  private MatchingResult<String> groupByComparison;
  
  private MatchingResult<BinaryExpression> whereComparison;
  
  private SqlExecutionResult executionResult;
  
  public SqlResult<C> build() {
    // @formatter:off
    return new SqlResult<>(
        learnerSolution,
        columnComparison,
        tableComparison,
        orderByComparison,
        groupByComparison,
        whereComparison,
        executionResult);
    // @formatter:on
  }
  
  public SqlResultBuilder<C> setColumnComparison(MatchingResult<C> theColumnComparison) {
    columnComparison = theColumnComparison;
    return this;
  }
  
  public SqlResultBuilder<C> setExecutionResult(SqlExecutionResult theExecutionResult) {
    executionResult = theExecutionResult;
    return this;
  }
  
  public SqlResultBuilder<C> setGroupByComparison(MatchingResult<String> theGroupByComparison) {
    groupByComparison = theGroupByComparison;
    return this;
  }
  
  public SqlResultBuilder<C> setLearnerSolution(String theLearnerSolution) {
    learnerSolution = theLearnerSolution;
    return this;
  }
  
  public SqlResultBuilder<C> setOrderByComparison(MatchingResult<String> theOrderByComparison) {
    orderByComparison = theOrderByComparison;
    return this;
  }
  
  public SqlResultBuilder<C> setTableComparison(MatchingResult<String> theTableComparison) {
    tableComparison = theTableComparison;
    return this;
  }
  
  public SqlResultBuilder<C> setWhereComparison(MatchingResult<BinaryExpression> theWhereComparison) {
    whereComparison = theWhereComparison;
    return this;
  }
}
