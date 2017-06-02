package model.querycorrectors;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import model.SqlCorrectionException;
import model.StringConsts;
import model.correction.CorrectionException;
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

public abstract class QueryCorrector<Q extends Statement, ColumnType> {

  protected static final StringEqualsMatcher STRING_EQ_MATCHER = new StringEqualsMatcher();
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

  public SqlResult<ColumnType> correct(Database database, String learnerSolution, SqlSample sampleStatement,
      SqlExercise exercise) throws CorrectionException {
    Q userQ = parseStatement(learnerSolution);
    Q sampleQ = parseStatement(sampleStatement.sample);

    MatchingResult<ColumnType> columnComp = compareColumns(userQ, sampleQ);

    MatchingResult<String> tableComp = STRING_EQ_MATCHER.match(StringConsts.TABLES_NAME, getTables(userQ),
        getTables(sampleQ));

    MatchingResult<String> orderByComparison = compareOrderByElements(userQ, sampleQ);

    MatchingResult<String> groupByComparison = compareGroupByElements(userQ, sampleQ);

    MatchingResult<BinaryExpression> whereComp = BIN_EX_MATCHER.match(StringConsts.CONDITIONS_NAME,
        getExpressions(userQ), getExpressions(sampleQ));

    SqlExecutionResult executionResult = null;
    try {
      executionResult = executeQuery(database, userQ, sampleQ, exercise);
    } catch (CorrectionException e) {
      throw e;
    }

    // @formatter:off
    return new SqlResultBuilder<ColumnType>()
        .setLearnerSolution(learnerSolution)
        .setColumnComparison(columnComp)
        .setTableComparison(tableComp)
        .setGroupByComparison(groupByComparison)
        .setOrderByComparison(orderByComparison)
        .setWhereComparison(whereComp)
        .setExecutionResult(executionResult)
        .build();
    // @formatter:on
  }

  private List<BinaryExpression> getExpressions(Q statement) {
    return new ExpressionExtractor(getWhere(statement)).extract();
  }

  protected abstract MatchingResult<ColumnType> compareColumns(Q userQuery, Q sampleQuery);

  protected abstract MatchingResult<String> compareGroupByElements(Q userQuery, Q sampleQuery);

  protected abstract MatchingResult<String> compareOrderByElements(Q userQuery, Q sampleQuery);

  protected abstract SqlExecutionResult executeQuery(Database database, Q userStatement, Q sampleStatement,
      SqlExercise exercise) throws CorrectionException;

  protected abstract List<String> getTables(Q userQuery);

  protected abstract Expression getWhere(Q query);

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