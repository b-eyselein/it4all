package model.queryCorrectors.update;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import model.SqlQueryResult;
import model.correctionResult.SqlExecutionResult;
import model.exercise.EvaluationFailed;
import model.exercise.EvaluationResult;
import model.exercise.FeedbackLevel;
import model.exercise.SqlExercise;
import model.queryCorrectors.QueryCorrector;
import play.Logger;
import play.db.Database;

public abstract class ChangeCorrector<QueryType extends net.sf.jsqlparser.statement.Statement, ComparedType>
    extends QueryCorrector<QueryType, ComparedType> {
  
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
  protected EvaluationResult executeQuery(Database database, QueryType userStatement, QueryType sampleStatement,
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
      return new EvaluationFailed(
          "Es gab einen Fehler beim Ausführen eines Statements:<p><pre>" + e.getMessage() + "</pre></p>");
    }
  }
  
}
