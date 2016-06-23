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

  protected static final TablesNamesFinder TABLE_NAME_FINDER = new TablesNamesFinder();
  protected SqlExType exType;
  
  public QueryCorrector(SqlExType theExType) {
    exType = theExType;
  }

  @SuppressWarnings("unchecked")
  public SqlCorrectionResult correct(QueryType parsedUserStatement, SqlExercise exercise, Connection connection) {
    if(exercise.exType != exType)
      return new SqlCorrectionResult(Success.NONE, "Es wurde das falsche Keyword verwendet!");

    QueryType parsedSampleStatement = null;
    try {
      parsedSampleStatement = (QueryType) CCJSqlParserUtil.parse(exercise.sample);
    } catch (JSQLParserException | ClassCastException e) {
      return new SqlCorrectionResult(Success.NONE, "Es gab einen Fehler bei der Korrektur!");
    }
    
    compareUsedTables(parsedUserStatement, parsedSampleStatement);
    
    return correctSpecialForQuery(parsedUserStatement, exercise, connection);
  }
  
  protected abstract SqlCorrectionResult compareUsedTables(QueryType parsedStatement, QueryType parsedSampleStatement);
  
  protected abstract SqlCorrectionResult correctSpecialForQuery(QueryType parsedStatement, SqlExercise exercise,
      Connection connection);
}