package model.querycorrectors.update;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import model.SqlCorrectionException;
import model.SqlQueryResult;
import model.correctionresult.SqlExecutionResult;
import model.exercise.FeedbackLevel;
import model.exercise.SqlExercise;
import model.matching.MatchingResult;
import model.querycorrectors.QueryCorrector;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import play.Logger;
import play.db.Database;

public abstract class ChangeCorrector<QueryType extends net.sf.jsqlparser.statement.Statement>
    extends QueryCorrector<QueryType, QueryType> {
  
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
    return null;
  }
  
  @Override
  protected MatchingResult<String> compareOrderByElements(QueryType userQuery, QueryType sampleQuery) {
    return null;
  }
  
  @Override
  protected SqlExecutionResult executeQuery(Database database, QueryType userStatement, QueryType sampleStatement,
      SqlExercise exercise, FeedbackLevel feedbackLevel) {
    try(Connection connection = database.getConnection()) {
      
      connection.setCatalog(exercise.scenario.shortName);
      connection.setAutoCommit(false);
      
      createDatabaseIfNotExists(connection, exercise.scenario.shortName,
          Paths.get("conf", "resources", exercise.scenario.scriptFile));
      
      String validation = exercise.validation;
      
      runUpdate(connection, userStatement.toString());
      SqlQueryResult userResult = runQuery(connection, validation);
      connection.rollback();
      
      runUpdate(connection, sampleStatement.toString());
      SqlQueryResult sampleResult = runQuery(connection, validation);
      connection.rollback();
      
      return new SqlExecutionResult(feedbackLevel, userResult, sampleResult);
      
    } catch (SQLException e) {
      Logger.error("There was an error while executing a sql statement: ", e);
      // return new EvaluationFailed(
      // "Es gab einen Fehler beim Ausführen eines Statements:<p><pre>" +
      // e.getMessage() + "</pre></p>");
      return null;
    }
  }
  
  @Override
  protected QueryType getPlainStatement(QueryType query) {
    return query;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  protected QueryType parseStatement(String statement) throws SqlCorrectionException {
    try {
      return (QueryType) CCJSqlParserUtil.parse(statement);
    } catch (JSQLParserException e) { // NOSONAR
      throw new SqlCorrectionException("Es gab einen Fehler beim Parsen des folgenden Statements: " + statement);
    } catch (ClassCastException e) { // NOSONAR
      throw new SqlCorrectionException("Das Statement war vom falschen Typ! Erwartet wurde INSERT!");
    }
  }
  
}
