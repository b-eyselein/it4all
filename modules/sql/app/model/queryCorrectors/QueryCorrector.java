package model.queryCorrectors;

import java.sql.Connection;

import model.SqlExercise;
import model.SqlExercise.SqlExType;
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
  public String correct(QueryType parsedUserStatement, SqlExercise exercise, Connection connection) {
    if(exercise.exType != exType)
      return "Es wurde das falsche Keyword verwendet!";
    
    QueryType parsedSampleStatement = null;
    try {
      parsedSampleStatement = (QueryType) CCJSqlParserUtil.parse(exercise.sample);
    } catch (JSQLParserException e) {
      e.printStackTrace();
    }

    compareUsedTables(parsedUserStatement, parsedSampleStatement);

    return correctSpecialForQuery(parsedUserStatement, exercise, connection);
  }

  protected abstract void compareUsedTables(QueryType parsedStatement, QueryType parsedSampleStatement);

  protected abstract String correctSpecialForQuery(QueryType parsedStatement, SqlExercise exercise,
      Connection connection);
}