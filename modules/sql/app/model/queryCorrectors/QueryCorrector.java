package model.queryCorrectors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import model.ScriptRunner;
import model.SqlCorrectionException;
import model.correctionResult.SqlCorrectionResult;
import model.correctionResult.ColumnComparison;
import model.correctionResult.TableComparison;
import model.exercise.SqlExercise;
import model.exercise.Success;
import net.sf.jsqlparser.statement.Statement;
import play.Logger;
import play.db.Database;

public abstract class QueryCorrector<QueryType extends Statement, ComparedType> {
  
  private static final String CREATE_DB_DUMMY = "CREATE DATABASE IF NOT EXISTS ";
  private static final String SHOW_ALL_DBS = "SHOW DATABASES";
  
  private static final Logger.ALogger theLogger = Logger.of("sql");
  
  protected static List<String> listDifference(List<String> a, List<String> b) {
    List<String> ret = new LinkedList<>();
    ret.addAll(a);
    ret.removeAll(b);
    return ret;
  }
  
  public SqlCorrectionResult correct(Database database, String userStatement, String sampleStatement,
      SqlExercise exercise) {
    QueryType parsedUserStatement, parsedSampleStatement;
    try {
      parsedUserStatement = parseStatement(userStatement);
      parsedSampleStatement = parseStatement(sampleStatement);
    } catch (SqlCorrectionException e) {
      return new SqlCorrectionResult(Success.NONE, "Es gab einen Fehler beim Parsen der Musterlösung!");
    }
    
    // Compare queries statically
    SqlCorrectionResult staticComp = compareStatically(parsedUserStatement, parsedSampleStatement);
    
    // Execute both queries, check if results match
    SqlCorrectionResult executionResult = executeQuery(database, parsedUserStatement, parsedSampleStatement,
        exercise.scenario.shortName);

    if(staticComp != null)
      executionResult.withTableComparisonResult(staticComp.getTableComparison())
          .withColumnsComparisonResult(staticComp.getColumnComparison());

    return executionResult;
  }
  
  private boolean databaseAlreadyExists(Connection connection, String slaveDB) throws SQLException {
    ResultSet existingDBs = connection.createStatement().executeQuery(SHOW_ALL_DBS);
    while(existingDBs.next())
      if(slaveDB.equals(existingDBs.getString(1)))
        return true;
    return false;
  }
  
  protected final ColumnComparison compareQueriedColumns(ComparedType userQuery, ComparedType sampleQuery) {
    List<String> userColumns = getColumns(userQuery);
    List<String> sampleColumns = getColumns(sampleQuery);
    
    List<String> wrongColumns = listDifference(userColumns, sampleColumns);
    List<String> missingColumns = listDifference(sampleColumns, userColumns);
    
    Success success = Success.NONE;
    if(wrongColumns.isEmpty() && missingColumns.isEmpty())
      success = Success.COMPLETE;
    
    return new ColumnComparison(success, missingColumns, wrongColumns);
  }

  protected abstract SqlCorrectionResult compareStatically(QueryType parsedUserStatement,
      QueryType parsedSampleStatement);
  
  protected TableComparison compareTables(ComparedType userQuery, ComparedType sampleQuery) {
    List<String> userTableNames = getTables(userQuery);
    List<String> sampleTableNames = getTables(sampleQuery);
    
    List<String> wrongTables = listDifference(userTableNames, sampleTableNames);
    List<String> missingTables = listDifference(sampleTableNames, userTableNames);
    
    Success success = Success.NONE;
    if(missingTables.isEmpty())
      success = Success.PARTIALLY;
    if(wrongTables.isEmpty())
      success = Success.COMPLETE;
    
    return new TableComparison(success, missingTables, wrongTables);
  }
  
  protected void deleteDB(Connection connection, String slaveDB) throws SQLException {
    // TODO Auto-generated method stub
    long startTime = System.currentTimeMillis();
    connection.createStatement().executeUpdate("DROP DATABASE IF EXISTS " + slaveDB);
    long timeTaken = System.currentTimeMillis() - startTime;
    theLogger.info("Successfully dropped database " + slaveDB + " in " + timeTaken + "ms");
  }
  
  protected abstract SqlCorrectionResult executeQuery(Database database, QueryType userStatement,
      QueryType sampleStatement, String scenarioName);

  protected abstract List<String> getColumns(ComparedType statement);
  
  protected abstract List<String> getTables(ComparedType userQuery);
  
  protected void initializeDB(Connection connection, String slaveDB, String scenarioName) throws SQLException {
    long startTime = System.currentTimeMillis();
    // Check if slave DB exists, if not, create
    if(!databaseAlreadyExists(connection, slaveDB))
      connection.createStatement().executeUpdate(CREATE_DB_DUMMY + slaveDB);
    
    // Change db to users own db
    connection.setCatalog(slaveDB);
    
    // Initialize DB with values
    Path sqlScriptFile = Paths.get("modules/sql/conf/resources/" + scenarioName + ".sql");
    if(Files.exists(sqlScriptFile))
      try {
        ScriptRunner.runScript(connection, Files.readAllLines(sqlScriptFile), false, false);
      } catch (IOException e) {
        theLogger.error("Error reading Sql-Script " + sqlScriptFile, e);
      }
    else
      theLogger.error("Trying to initialize database with script " + sqlScriptFile + ", but there is no such file!");
    
    long timeTaken = System.currentTimeMillis() - startTime;
    theLogger.info("Successfully initialized the database " + slaveDB + " in " + timeTaken + "ms");
  }
  
  protected abstract QueryType parseStatement(String statement) throws SqlCorrectionException;
}