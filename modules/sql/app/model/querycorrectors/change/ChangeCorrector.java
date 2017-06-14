package model.querycorrectors.change;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import model.StringConsts;
import model.correctionresult.SqlExecutionResult;
import model.exercise.SqlExercise;
import model.matching.Match;
import model.matching.Matcher;
import model.matching.MatchingResult;
import model.querycorrectors.QueryCorrector;
import model.sql.SqlQueryResult;
import net.sf.jsqlparser.schema.Column;
import play.Logger;
import play.db.Database;

public abstract class ChangeCorrector<QueryType extends net.sf.jsqlparser.statement.Statement>
    extends QueryCorrector<QueryType, Column> {
  
  public ChangeCorrector(String theQueryType) {
    super(theQueryType);
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
  
  @Override
  protected List<MatchingResult<String, Match<String>>> makeStringComparisons(QueryType userQ, QueryType sampleQ) {
    return Arrays
        .asList(Matcher.STRING_EQ_MATCHER.match(StringConsts.TABLES_NAME, getTables(userQ), getTables(sampleQ)));
  }
  
}
