package model.querycorrectors.update;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import model.StringConsts;
import model.correctionresult.SqlExecutionResult;
import model.exercise.SqlExercise;
import model.matching.MatchingResult;
import model.querycorrectors.QueryCorrector;
import model.sql.SqlQueryResult;
import play.Logger;
import play.db.Database;

public abstract class ChangeCorrector<QueryType extends net.sf.jsqlparser.statement.Statement>
    extends QueryCorrector<QueryType> {
  
  public ChangeCorrector(String theQueryType, boolean theCompareColumns, boolean theCompareWhere) {
    super(theQueryType, theCompareColumns, false, false, theCompareWhere, true);
  }
  
  public SqlQueryResult runQuery(Connection connection, String query) {
    try(Statement statement = connection.createStatement()) {
      return new SqlQueryResult(statement.executeQuery(query));
    } catch (SQLException e) {
      Logger.error("There has been an error running an sql query: \"" + query + "\"", e);
      return null;
    }
  }
  
  private void runUpdate(Connection connection, String query) {
    try(Statement statement = connection.createStatement()) {
      statement.executeUpdate(query);
    } catch (SQLException e) {
      Logger.error("There has been an error running an sql query: \"" + query + "\"", e);
    }
  }
  
  @Override
  protected MatchingResult<String> compareGroupByElements(QueryType userQuery, QueryType sampleQuery) {
    throw new UnsupportedOperationException("UPDATE / INSERT / DELETE statements do not have group by elements!");
  }
  
  @Override
  protected MatchingResult<String> compareOrderByElements(QueryType userQuery, QueryType sampleQuery) {
    throw new UnsupportedOperationException("UPDATE / INSERT / DELETE statements do not have order by elements!");
  }
  
  @Override
  protected SqlExecutionResult executeQuery(Database database, QueryType userStatement, QueryType sampleStatement,
      SqlExercise exercise) {
    try(Connection connection = database.getConnection()) {
      
      connection.setCatalog(exercise.scenario.shortName);
      connection.setAutoCommit(false);
      
      String validation = StringConsts.SELECT_ALL_DUMMY + getTables(sampleStatement).get(0);
      
      runUpdate(connection, userStatement.toString());
      SqlQueryResult userResult = runQuery(connection, validation);
      connection.rollback();
      
      runUpdate(connection, sampleStatement.toString());
      SqlQueryResult sampleResult = runQuery(connection, validation);
      connection.rollback();
      
      return new SqlExecutionResult(userResult, sampleResult);
      
    } catch (SQLException e) {
      Logger.error("There was an error while executing a sql statement: ", e);
      // return new EvaluationFailed(
      // "Es gab einen Fehler beim Ausführen eines Statements:<p><pre>" +
      // e.getMessage() + "</pre></p>");
      return null;
    }
  }
  
  // @Override
  // protected abstract List<String> getColumns(QueryType userQuery);
  
}
