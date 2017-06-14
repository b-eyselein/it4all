package model.querycorrectors;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import model.SqlCorrectionException;
import model.StringConsts;
import model.conditioncorrector.BinaryExpressionMatch;
import model.conditioncorrector.BinaryExpressionMatcher;
import model.conditioncorrector.ExpressionExtractor;
import model.correction.CorrectionException;
import model.correctionresult.SqlExecutionResult;
import model.correctionresult.SqlResult;
import model.correctionresult.SqlResultBuilder;
import model.exercise.SqlExercise;
import model.exercise.SqlSample;
import model.matching.Match;
import model.matching.MatchingResult;
import model.querycorrectors.columnMatch.ColumnMatch;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import play.db.Database;

public abstract class QueryCorrector<Q extends Statement, C> {
  
  private static final BinaryExpressionMatcher BIN_EX_MATCHER = new BinaryExpressionMatcher();
  
  private String queryType;
  
  public QueryCorrector(String theQueryType) {
    queryType = theQueryType;
  }
  
  protected static <T> List<String> listAsStrings(List<T> list) {
    if(list == null)
      return Collections.emptyList();
    return list.stream().map(T::toString).collect(Collectors.toList());
  }
  
  public SqlResult<C> correct(Database database, String learnerSolution, SqlSample sampleStatement,
      SqlExercise exercise) throws CorrectionException {
    Q userQ = parseStatement(learnerSolution);
    Q sampleQ = parseStatement(sampleStatement.sample);
    
    return new SqlResultBuilder<C>(learnerSolution)
        
        .setColumnComparison(compareColumns(userQ, sampleQ))
        
        .setStringComparisons(makeStringComparisons(userQ, sampleQ))
        
        .setWhereComparison(compareWhereClauses(userQ, sampleQ))
        
        .setExecutionResult(executeQuery(database, userQ, sampleQ, exercise)).build();
  }
  
  private MatchingResult<BinaryExpression, BinaryExpressionMatch> compareWhereClauses(Q userQ, Q sampleQ) {
    return BIN_EX_MATCHER.match(StringConsts.CONDITIONS_NAME, getExpressions(userQ), getExpressions(sampleQ));
  }
  
  private List<BinaryExpression> getExpressions(Q statement) {
    return new ExpressionExtractor(getWhere(statement)).extract();
  }
  
  protected abstract MatchingResult<C, ColumnMatch<C>> compareColumns(Q userQuery, Q sampleQuery);
  
  protected abstract SqlExecutionResult executeQuery(Database database, Q userStatement, Q sampleStatement,
      SqlExercise exercise) throws CorrectionException;
  
  protected abstract List<String> getTables(Q userQuery);
  
  protected abstract Expression getWhere(Q query);
  
  protected abstract List<MatchingResult<String, Match<String>>> makeStringComparisons(Q userQ, Q sampleQ);
  
  @SuppressWarnings("unchecked")
  protected Q parseStatement(String statement) throws SqlCorrectionException {
    try {
      return (Q) CCJSqlParserUtil.parse(statement);
    } catch (JSQLParserException e) {
      throw new SqlCorrectionException(statement, "Es gab einen Fehler beim Parsen des Statements: " + statement, e);
    } catch (ClassCastException e) {
      throw new SqlCorrectionException(statement,
          "Das Statement war vom falschen Typ! Erwartet wurde " + queryType + "!", e);
    }
  }
}