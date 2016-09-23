package model.queryCorrectors.update;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import model.SqlCorrectionException;
import model.SqlQueryResult;
import model.correctionResult.ColumnComparison;
import model.correctionResult.SqlExecutionResult;
import model.correctionResult.TableComparison;
import model.exercise.EvaluationFailed;
import model.exercise.EvaluationResult;
import model.exercise.FeedbackLevel;
import model.exercise.Success;
import model.exercise.update.UpdateExercise;
import model.queryCorrectors.QueryCorrector;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.update.Update;
import play.Logger;
import play.db.Database;

public class UpdateCorrector extends QueryCorrector<Update, Update, UpdateExercise> {

  @Override
  protected List<EvaluationResult> compareStatically(Update userQuery, Update sampleQuery,
      FeedbackLevel feedbackLevel) {
    Success success = Success.COMPLETE;

    TableComparison tableComparison = compareTables(userQuery, sampleQuery);

    ColumnComparison columnComparison = compareColumns(userQuery, sampleQuery);

    // comparison has "lower" success than assumed at the moment
    if(success.compareTo(tableComparison.getSuccess()) > 0)
      success = tableComparison.getSuccess();
    if(success.compareTo(columnComparison.getSuccess()) > 0)
      success = columnComparison.getSuccess();

    return Arrays.asList(tableComparison, columnComparison);
  }

  @Override
  protected EvaluationResult executeQuery(Database database, Update userStatement, Update sampleStatement,
      UpdateExercise exercise, FeedbackLevel feedbackLevel) {
    try {
      Connection connection = database.getConnection();
      connection.setCatalog(exercise.scenario.shortName);
      connection.setAutoCommit(false);

      createDatabaseIfNotExists(connection, exercise.scenario.shortName,
          Paths.get("conf", "resources", exercise.scenario.scriptFile));

      connection.createStatement().executeUpdate(userStatement.toString());
      SqlQueryResult userResult = new SqlQueryResult(connection.createStatement().executeQuery(exercise.validation));
      connection.rollback();

      connection.createStatement().executeUpdate(sampleStatement.toString());
      SqlQueryResult sampleResult = new SqlQueryResult(connection.createStatement().executeQuery(exercise.validation));
      connection.rollback();

      connection.close();

      return new SqlExecutionResult(feedbackLevel, userResult, sampleResult);

      // } catch (IOException e) {
      // return new EvaluationFailed("There was an error while reading the
      // script file " + script);
    } catch (SQLException e) {
      Logger.error("There was an error while executing a sql statement: ", e);
      return new EvaluationFailed("There was an error while executing a sql statement!");
    }
  }

  @Override
  protected List<String> getColumns(Update statement) {
    return statement.getColumns().stream().map(col -> col.getColumnName()).collect(Collectors.toList());
  }

  @Override
  protected List<String> getTables(Update statement) {
    return statement.getTables().stream().map(table -> table.getName()).collect(Collectors.toList());
  }

  @Override
  protected Update parseStatement(String statement) throws SqlCorrectionException {
    try {
      return (Update) CCJSqlParserUtil.parse(statement);
    } catch (JSQLParserException e) {
      throw new SqlCorrectionException("Es gab einen Fehler beim Parsen des folgenden Statements: " + statement);
    } catch (ClassCastException e) {
      throw new SqlCorrectionException("Das Statement war vom falschen Typ! Erwartet wurde UPDATE!");
    }
  }

}
