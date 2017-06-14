package model.correctionresult;

import java.util.List;

import model.conditioncorrector.BinaryExpressionMatch;
import model.matching.Match;
import model.matching.MatchingResult;
import model.querycorrectors.columnMatch.ColumnMatch;
import net.sf.jsqlparser.expression.BinaryExpression;

public class SqlResultBuilder<C> {

  private String learnerSolution;

  private MatchingResult<C, ColumnMatch<C>> columnComparison;

  private List<MatchingResult<String, Match<String>>> stringComparisons;

  private MatchingResult<BinaryExpression, BinaryExpressionMatch> whereComparison;

  private SqlExecutionResult executionResult;

  public SqlResultBuilder(String theLearnerSolution) {
    learnerSolution = theLearnerSolution;
  }

  public SqlResult<C> build() {
    // @formatter:off
    return new SqlResult<>(
        learnerSolution,
        columnComparison,
        stringComparisons,
        whereComparison,
        executionResult);
    // @formatter:on
  }

  public SqlResultBuilder<C> setColumnComparison(MatchingResult<C, ColumnMatch<C>> theColumnComparison) {
    columnComparison = theColumnComparison;
    return this;
  }

  public SqlResultBuilder<C> setExecutionResult(SqlExecutionResult theExecutionResult) {
    executionResult = theExecutionResult;
    return this;
  }

  public SqlResultBuilder<C> setStringComparisons(List<MatchingResult<String, Match<String>>> theStringComparisons) {
    stringComparisons = theStringComparisons;
    return this;
  }

  public SqlResultBuilder<C> setWhereComparison(
      MatchingResult<BinaryExpression, BinaryExpressionMatch> theWhereComparison) {
    whereComparison = theWhereComparison;
    return this;
  }
}
