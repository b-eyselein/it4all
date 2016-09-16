package model.queryCorrectors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import model.ScriptRunner;
import model.SqlCorrectionException;
import model.correctionResult.ColumnComparison;
import model.correctionResult.SqlExecutionResult;
import model.correctionResult.TableComparison;
import model.exercise.EvaluationFailed;
import model.exercise.EvaluationResult;
import model.exercise.FeedbackLevel;
import model.exercise.SqlExercise;
import model.exercise.Success;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.update.Update;
import play.Logger;
import play.db.Database;

public class UpdateCorrector extends QueryCorrector<Update, Update> {
  
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
      SqlExercise exercise, FeedbackLevel feedbackLevel) {
    Connection connection = database.getConnection();
    Path script = Paths.get("conf", "resources", exercise.scenario.scriptFile);
    
    try {
      List<String> lines = Files.readAllLines(script);
      
      connection.createStatement().executeUpdate("CREATE DATABASE IF NOT EXISTS " + exercise.scenario.shortName);
      connection.setCatalog(exercise.scenario.shortName);
      
      ScriptRunner.runScript(connection, lines, false, false);
      
      Logger.debug("Executing user statement: >>" + userStatement.toString() + "<< on database " + database.getUrl());
      
      Statement statement = connection.createStatement();
      statement.execute(userStatement.toString());
      
      ResultSet resultSet = statement.getResultSet();
      ResultSetMetaData rsmd = resultSet.getMetaData();
      
      int columnsNumber = rsmd.getColumnCount();
      while(resultSet.next()) {
        for(int i = 1; i <= columnsNumber; i++) {
          if(i > 1)
            System.out.print(",  ");
          String columnValue = resultSet.getString(i);
          System.out.print(columnValue + " " + rsmd.getColumnName(i));
        }
        System.out.println("");
      }
      
      Logger.debug(statement.getUpdateCount() + "");
      
      return new SqlExecutionResult(Success.NONE, "Ausführungsvergleich der Statements muss noch implementiert werden!",
          feedbackLevel, null, null);
      
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return new EvaluationFailed("There was an error while reading the script file " + script);
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return null;
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
