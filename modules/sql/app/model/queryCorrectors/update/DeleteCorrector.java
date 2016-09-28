package model.queryCorrectors.update;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import model.SqlCorrectionException;
import model.SqlQueryResult;
import model.correctionResult.ColumnComparison;
import model.correctionResult.SqlExecutionResult;
import model.correctionResult.TableComparison;
import model.exercise.EvaluationFailed;
import model.exercise.EvaluationResult;
import model.exercise.FeedbackLevel;
import model.exercise.update.DeleteExercise;
import model.queryCorrectors.QueryCorrector;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.delete.Delete;
import play.Logger;
import play.db.Database;

public class DeleteCorrector extends QueryCorrector<Delete, Delete, DeleteExercise> {

  @Override
  protected List<EvaluationResult> compareStatically(Delete userQuery, Delete sampleQuery,
      FeedbackLevel feedbackLevel) {
    
    TableComparison tableComparison = compareTables(userQuery, sampleQuery);
    
    ColumnComparison columnComparison = compareColumns(userQuery, sampleQuery);

    EvaluationResult whereComparison = compareWheres(userQuery, sampleQuery);
    
    return Arrays.asList(tableComparison, columnComparison, whereComparison);
  }
  
  @Override
  protected EvaluationResult executeQuery(Database database, Delete userStatement, Delete sampleStatement,
      DeleteExercise exercise, FeedbackLevel feedbackLevel) {
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
      return new EvaluationFailed(
          "Es gab einen Fehler beim Ausführen eines Statements:<p><pre>" + e.getMessage() + "</pre></p>");
    }
  }
  
  @Override
  protected List<String> getColumns(Delete statement) {
    return Collections.emptyList();
  }
  
  @Override
  protected List<String> getTables(Delete userQuery) {
    return Arrays.asList(userQuery.getTable().getName());
  }
  
  @Override
  protected Expression getWhere(Delete query) {
    return query.getWhere();
  }

  @Override
  protected Delete parseStatement(String statement) throws SqlCorrectionException {
    try {
      return (Delete) CCJSqlParserUtil.parse(statement);
    } catch (JSQLParserException e) {
      throw new SqlCorrectionException("Es gab einen Fehler beim Parsen des folgenden Statements: " + statement);
    } catch (ClassCastException e) {
      throw new SqlCorrectionException("Das Statement war vom falschen Typ! Erwartet wurde DELETE!");
    }
  }
}