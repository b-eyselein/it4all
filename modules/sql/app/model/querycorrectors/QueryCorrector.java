package model.querycorrectors;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import model.SqlCorrectionException;
import model.StringConsts;
import model.conditioncorrector.BinaryExpressionMatch;
import model.conditioncorrector.BinaryExpressionMatcher;
import model.conditioncorrector.ExpressionExtractor;
import model.correction.CorrectionException;
import model.exercise.SqlExercise;
import model.exercise.SqlSample;
import model.matching.Match;
import model.matching.Matcher;
import model.matching.MatchingResult;
import model.querycorrectors.columnmatch.ColumnMatch;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import play.db.Database;

public abstract class QueryCorrector<Q extends Statement, C> {
  
  private String queryType;
  
  public QueryCorrector(String theQueryType) {
    queryType = theQueryType;
  }
  
  protected static <T> List<String> listAsStrings(List<T> list) {
    if(list == null)
      return Collections.emptyList();
    return list.stream().map(T::toString).collect(Collectors.toList());
  }
  
  public SqlResult<Q, C> correct(Database database, String learnerSolution, SqlSample sampleStatement,
      SqlExercise exercise) throws CorrectionException {
    Q userQ = parseStatement(learnerSolution);
    Q sampleQ = parseStatement(sampleStatement.sample);
    
    Map<String, String> userTableAliases = resolveAliases(userQ);
    Map<String, String> sampleTableAliases = resolveAliases(sampleQ);
    
    return instantiateResult(learnerSolution)
        
        .setTableComparison(compareTables(userQ, sampleQ))
        
        .setColumnComparison(compareColumns(userQ, sampleQ))
        
        .setWhereComparison(compareWhereClauses(userQ, userTableAliases, sampleQ, sampleTableAliases))
        
        .setExecutionResult(executeQuery(database, userQ, sampleQ, exercise))
        
        .makeOtherComparisons(userQ, sampleQ);
  }
  
  private MatchingResult<BinaryExpression, BinaryExpressionMatch> compareWhereClauses(Q userQ,
      Map<String, String> userTableAliases, Q sampleQ, Map<String, String> sampleQueryAliases) {
    BinaryExpressionMatcher binExMatcher = new BinaryExpressionMatcher(userTableAliases, sampleQueryAliases);
    return binExMatcher.match(StringConsts.CONDITIONS_NAME, getExpressions(userQ), getExpressions(sampleQ));
  }
  
  private List<BinaryExpression> getExpressions(Q statement) {
    return new ExpressionExtractor(getWhere(statement)).extract();
  }
  
  protected abstract MatchingResult<C, ColumnMatch<C>> compareColumns(Q userQuery, Q sampleQuery);
  
  protected MatchingResult<String, Match<String>> compareTables(Q userQ, Q sampleQ) {
    return Matcher.STRING_EQ_MATCHER.match(StringConsts.TABLES_NAME, getTableNames(userQ), getTableNames(sampleQ));
  }
  
  protected abstract SqlExecutionResult executeQuery(Database database, Q userStatement, Q sampleStatement,
      SqlExercise exercise) throws CorrectionException;
  
  protected abstract List<String> getTableNames(Q query);
  
  protected abstract List<Table> getTables(Q query);
  
  protected abstract Expression getWhere(Q query);
  
  protected abstract SqlResult<Q, C> instantiateResult(String learnerSolution);
  
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
  
  protected Map<String, String> resolveAliases(Q query) {
    return getTables(query).stream().filter(table -> table != null && table.getAlias() != null)
        .collect(Collectors.toMap(table -> table.getAlias().getName(), Table::getName));
  }
}