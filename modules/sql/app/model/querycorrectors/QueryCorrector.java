package model.querycorrectors;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import model.CorrectionException;
import model.StringConsts;
import model.conditioncorrector.ExpressionExtractor;
import model.conditioncorrector.ExpressionMatch;
import model.conditioncorrector.ExpressionMatcher;
import model.conditioncorrector.ExtractedExpressions;
import model.exercise.SqlExercise;
import model.exercise.SqlSample;
import model.matching.Match;
import model.matching.MatchingResult;
import model.matching.StringEqualsMatcher;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import play.db.Database;;

public abstract class QueryCorrector<Q extends Statement> {
  
  private static final StringEqualsMatcher TABLE_NAME_MATCHER = new StringEqualsMatcher(StringConsts.TABLES_NAME);
  
  private String queryType;
  
  public QueryCorrector(String theQueryType) {
    queryType = theQueryType;
  }
  
  protected static <T> List<String> listAsStrings(List<T> list) {
    if(list == null)
      return Collections.emptyList();
    return list.stream().map(T::toString).collect(Collectors.toList());
  }
  
  public SqlResult correct(Database database, String learnerSolution, SqlSample sampleStatement, SqlExercise exercise)
      throws CorrectionException {
    Q userQ = parseStatement(learnerSolution);
    Q sampleQ = parseStatement(sampleStatement.sample);
    
    Map<String, String> userTableAliases = resolveAliases(userQ);
    Map<String, String> sampleTableAliases = resolveAliases(sampleQ);
    
    return new SqlResult(learnerSolution)
        
        .setTableComparison(compareTables(userQ, sampleQ))
        
        .setColumnComparison(compareColumns(userQ, userTableAliases, sampleQ, sampleTableAliases))
        
        .setWhereComparison(compareWhereClauses(userQ, userTableAliases, sampleQ, sampleTableAliases))
        
        .setExecutionResult(executeQuery(database, userQ, sampleQ, exercise))
        
        .setOtherComparisons(makeOtherComparisons(userQ, sampleQ));
  }
  
  private MatchingResult<ColumnWrapper, ColumnMatch> compareColumns(Q userQuery, Map<String, String> userTableAliases,
      Q sampleQuery, Map<String, String> sampleTableAliases) {
    return new ColumnMatcher().match(getColumnWrappers(userQuery), getColumnWrappers(sampleQuery));
    
  }
  
  private MatchingResult<Expression, ExpressionMatch> compareWhereClauses(Q userQ, Map<String, String> userTableAliases,
      Q sampleQ, Map<String, String> sampleQueryAliases) {
    ExtractedExpressions userExps = getExpressions(userQ);
    ExtractedExpressions sampleExps = getExpressions(sampleQ);
    
    if(userExps.isEmpty() && sampleExps.isEmpty())
      return null;
    
    ExpressionMatcher binExMatcher = new ExpressionMatcher(userTableAliases, sampleQueryAliases);
    return binExMatcher.matchExpressions(userExps, sampleExps);
  }
  
  private ExtractedExpressions getExpressions(Q statement) {
    return new ExpressionExtractor(getWhere(statement)).extract();
  }
  
  protected MatchingResult<String, Match<String>> compareTables(Q userQ, Q sampleQ) {
    return TABLE_NAME_MATCHER.match(getTableNames(userQ), getTableNames(sampleQ));
  }
  
  protected abstract SqlExecutionResult executeQuery(Database database, Q userStatement, Q sampleStatement,
      SqlExercise exercise) throws CorrectionException;
  
  protected abstract List<ColumnWrapper> getColumnWrappers(Q query);
  
  protected abstract List<String> getTableNames(Q query);
  
  protected abstract List<Table> getTables(Q query);
  
  protected abstract Expression getWhere(Q query);
  
  protected abstract List<MatchingResult<? extends Object, ? extends Match<? extends Object>>> makeOtherComparisons(
      Q userQ, Q sampleQ);
  
  @SuppressWarnings("unchecked")
  protected Q parseStatement(String statement) throws CorrectionException {
    try {
      return (Q) CCJSqlParserUtil.parse(statement);
    } catch (JSQLParserException e) {
      throw new CorrectionException(statement, "Es gab einen Fehler beim Parsen des Statements: " + statement, e);
    } catch (ClassCastException e) {
      throw new CorrectionException(statement, "Das Statement war vom falschen Typ! Erwartet wurde " + queryType + "!",
          e);
    }
  }
  
  protected Map<String, String> resolveAliases(Q query) {
    return getTables(query).stream().filter(table -> table != null && table.getAlias() != null)
        .collect(Collectors.toMap(table -> table.getAlias().getName(), Table::getName));
  }
}