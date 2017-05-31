package model.querycorrectors;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import model.SqlCorrectionException;
import model.correctionresult.SqlExecutionResult;
import model.correctionresult.SqlResult;
import model.correctionresult.SqlResultBuilder;
import model.exercise.SqlExercise;
import model.exercise.SqlSample;
import model.matcher.BinaryExpressionMatcher;
import model.matching.Matcher.StringEqualsMatcher;
import model.matching.MatchingResult;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import play.db.Database;

public abstract class QueryCorrector<Q extends Statement, C> {
  
  protected static final StringEqualsMatcher STRING_EQ_MATCHER = new StringEqualsMatcher();
  private static final BinaryExpressionMatcher BIN_EX_MATCHER = new BinaryExpressionMatcher();
  
  private String queryType;
  
  private boolean compareColumns;
  private boolean compareOrderBy;
  private boolean compareGroupBy;
  private boolean compareWhere;
  private boolean execute;
  
  public QueryCorrector(String theQueryType, boolean theCompareColumns, boolean theCompareOrderBy,
      boolean theCompareGroupBy, boolean theCompareWhere, boolean theExecute) {
    queryType = theQueryType;
    
    compareColumns = theCompareColumns;
    compareOrderBy = theCompareOrderBy;
    compareGroupBy = theCompareGroupBy;
    compareWhere = theCompareWhere;
    execute = theExecute;
  }
  
  protected static <T> List<String> listAsStrings(List<T> list) {
    if(list == null)
      return Collections.emptyList();
    return list.stream().map(T::toString).collect(Collectors.toList());
  }
  
  public SqlResult correct(Database database, String userStatement, SqlSample sampleStatement, SqlExercise exercise)
      throws SqlCorrectionException {
    Q parsedUserStatement = parseStatement(userStatement);
    Q parsedSampleStatement = parseStatement(sampleStatement.sample);
    
    C userQ = getPlainStatement(parsedUserStatement);
    C sampleQ = getPlainStatement(parsedSampleStatement);
    
    MatchingResult<String> columnComp = compareColumns ? compareColumns(userQ, sampleQ) : null;
    
    MatchingResult<String> tableComp = STRING_EQ_MATCHER.match("Tabellen", getTables(userQ), getTables(sampleQ));
    
    MatchingResult<String> orderByComparison = compareOrderBy ? compareOrderByElements(userQ, sampleQ) : null;
    
    MatchingResult<String> groupByComparison = compareGroupBy ? compareGroupByElements(userQ, sampleQ) : null;
    
    MatchingResult<BinaryExpression> whereComp = compareWhere
        ? BIN_EX_MATCHER.match("Bedingungen", getExpressions(userQ), getExpressions(sampleQ)) : null;
    
    SqlExecutionResult executionResult = execute
        ? executeQuery(database, parsedUserStatement, parsedSampleStatement, exercise) : null;
    
    // @formatter:off
    return new SqlResultBuilder()
        .setColumnComparison(columnComp)
        .setTableComparison(tableComp)
        .setGroupByComparison(groupByComparison)
        .setOrderByComparison(orderByComparison)
        .setWhereComparison(whereComp)
        .setExecutionResult(executionResult)
        .build();
    // @formatter:on
  }
  
  private List<BinaryExpression> getExpressions(C statement) {
    return new ExpressionExtractor(getWhere(statement)).extract();
  }
  
  protected abstract MatchingResult<String> compareColumns(C userQuery, C sampleQuery);
  
  protected abstract MatchingResult<String> compareGroupByElements(C userQuery, C sampleQuery);
  
  protected abstract MatchingResult<String> compareOrderByElements(C userQuery, C sampleQuery);
  
  protected abstract SqlExecutionResult executeQuery(Database database, Q userStatement, Q sampleStatement,
      SqlExercise exercise);
  
  protected abstract C getPlainStatement(Q query);
  
  protected abstract List<String> getTables(C userQuery);
  
  protected abstract Expression getWhere(C query);
  
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