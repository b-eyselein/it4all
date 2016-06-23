package model.queryCorrectors;

import java.sql.Connection;

import model.SqlCorrectionResult;
import model.SqlExercise;
import model.SqlExercise.SqlExType;
import model.exercise.Success;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.TablesNamesFinder;

public abstract class QueryCorrector<QueryType extends Statement> {

  protected static TablesNamesFinder tableNameFinder = new TablesNamesFinder();
  protected SqlExType exType;

  public QueryCorrector(SqlExType theExType) {
    exType = theExType;
  }

  /**
   * Steps taken:
   *
   * <ol>
   * <li>preprocess statement --> syntax</li>
   * <li>compare statement to sample statement</li>
   * <li>execute statement, compare results</li>
   * </ol>
   *
   * @param userStatement
   * @param exercise
   * @param connection
   * @return
   */
  @SuppressWarnings("unchecked")
  public SqlCorrectionResult correct(Statement userStatement, SqlExercise exercise, Connection connection) {
    if(exercise.exType != exType)
      return new SqlCorrectionResult(Success.NONE, "Es wurde das falsche Keyword verwendet!");

    QueryType parsedSampleStatement = null;
    QueryType parsedUserStatement = null;
    try {
      parsedSampleStatement = (QueryType) CCJSqlParserUtil.parse(exercise.sample);
      parsedUserStatement = (QueryType) userStatement;
    } catch (JSQLParserException | ClassCastException e) {
      return new SqlCorrectionResult(Success.NONE, "Es gab einen Fehler bei der Korrektur!");
    }

    SqlCorrectionResult tablesUsed = compareUsedTables(parsedUserStatement, parsedSampleStatement);
    if(tablesUsed != null)
      return tablesUsed;

    return correctSpecialForQuery(parsedUserStatement, exercise, connection);
  }

  protected abstract SqlCorrectionResult compareUsedTables(QueryType parsedStatement, QueryType parsedSampleStatement);

  protected abstract SqlCorrectionResult correctSpecialForQuery(QueryType parsedStatement, SqlExercise exercise,
      Connection connection);
}