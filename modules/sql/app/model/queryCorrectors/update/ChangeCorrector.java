package model.queryCorrectors.update;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;

import model.SqlQueryResult;
import model.correctionResult.SqlExecutionResult;
import model.exercise.EvaluationFailed;
import model.exercise.EvaluationResult;
import model.exercise.FeedbackLevel;
import model.exercise.SqlExercise;
import model.queryCorrectors.QueryCorrector;
import net.sf.jsqlparser.statement.Statement;
import play.Logger;
import play.db.Database;

public abstract class ChangeCorrector<QueryType extends Statement, ComparedType>
    extends QueryCorrector<QueryType, ComparedType> {

  @Override
  protected EvaluationResult executeQuery(Database database, QueryType userStatement, QueryType sampleStatement,
      SqlExercise exercise, FeedbackLevel feedbackLevel) {
    try(Connection connection = database.getConnection()) {

      connection.setCatalog(exercise.scenario.shortName);
      connection.setAutoCommit(false);

      createDatabaseIfNotExists(connection, exercise.scenario.shortName,
          Paths.get("conf", "resources", exercise.scenario.scriptFile));

      String validation = exercise.validation;

      connection.createStatement().executeUpdate(userStatement.toString());
      SqlQueryResult userResult = new SqlQueryResult(connection.createStatement().executeQuery(validation));
      connection.rollback();

      connection.createStatement().executeUpdate(sampleStatement.toString());
      SqlQueryResult sampleResult = new SqlQueryResult(connection.createStatement().executeQuery(validation));
      connection.rollback();

      return new SqlExecutionResult(feedbackLevel, userResult, sampleResult);

    } catch (SQLException e) {
      Logger.error("There was an error while executing a sql statement: ", e);
      return new EvaluationFailed(
          "Es gab einen Fehler beim Ausführen eines Statements:<p><pre>" + e.getMessage() + "</pre></p>");
    }
  }

}
