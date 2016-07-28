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

import model.Levenshtein;
import model.ScriptRunner;
import model.correctionResult.SqlCorrectionResult;
import model.exercise.SqlExercise;
import model.exercise.SqlExercise.SqlExType;
import model.exercise.SqlSampleSolution;
import model.exercise.Success;
import model.user.User;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import play.Logger;

public abstract class QueryCorrector<QueryType extends Statement> {

  private static final String DB_BASENAME = "sql_";
  private static final String CREATE_DB_DUMMY = "CREATE DATABASE IF NOT EXISTS ";
  private static final String SHOW_ALL_DBS = "SHOW DATABASES";

  private static final Logger.ALogger theLogger = Logger.of("sql");

  protected static List<String> listDifference(List<String> a, List<String> b) {
    List<String> ret = new LinkedList<>();
    ret.addAll(a);
    ret.removeAll(b);
    return ret;
  }

  protected SqlExType exType;

  public QueryCorrector(SqlExType theExType) {
    exType = theExType;
  }

  /**
   * Steps taken:
   *
   * <ol>
   * <li>preprocess statement --> syntax</li>
   * <li>compare statement to sample statement</li>
   * <li>execute statement, compare results</li>
   * </ol>
   *
   * @param userStatement
   * @param exercise
   * @param connection
   * @return
   */
  public SqlCorrectionResult correct(User user, String userStatement, SqlExercise exercise, Connection connection) {
    // Check if the right operator (SELECT/DELETE/UPDATE/...) was used
    if(exercise.exType != exType)
      return new SqlCorrectionResult(Success.NONE, "Es wurde das falsche Keyword verwendet!");

    // Parse user statement to right QueryType
    QueryType parsedUserStatement = parseStatement(userStatement);
    if(parsedUserStatement == null)
      return new SqlCorrectionResult(Success.NONE, "Es gab einen Fehler beim Parsen der Musterlösung!");

    // Get and parse best fitting sample solution
    SqlSampleSolution bestFitting = findBestFittingSample(parsedUserStatement, exercise.samples);
    QueryType parsedSampleStatement = parseStatement(bestFitting.sample);
    if(parsedSampleStatement == null)
      return new SqlCorrectionResult(Success.NONE, "Es gab einen Fehler beim Parsen der Musterlösung!");

    // Compare queries statically
    SqlCorrectionResult staticComp = compareStatically(parsedUserStatement, parsedSampleStatement);

    // Execute both queries, check if results match
    return executeQuery(parsedUserStatement, parsedSampleStatement, connection, DB_BASENAME + user.name,
        exercise.scenario.shortName).withTableComparisonResult(staticComp.getUsedTablesComparison())
            .withColumnsComparisonResult(staticComp.getUsedColumnsComparison());
  }

  private boolean databaseAlreadyExists(Connection connection, String slaveDB) throws SQLException {
    ResultSet existingDBs = connection.createStatement().executeQuery(SHOW_ALL_DBS);
    while(existingDBs.next())
      if(slaveDB.equals(existingDBs.getString(1)))
        return true;
    return false;
  }

  private SqlSampleSolution findBestFittingSample(QueryType userStatement, List<SqlSampleSolution> samples) {
    SqlSampleSolution bestFitting = null;
    int bestDistance = Integer.MAX_VALUE;

    for(SqlSampleSolution sample: samples) {
      int newDistance = Levenshtein.levenshteinDistance(sample.sample, userStatement.toString());
      if(newDistance < bestDistance) {
        bestFitting = sample;
        bestDistance = newDistance;
      }
    }
    return bestFitting;
  }

  @SuppressWarnings("unchecked")
  private QueryType parseStatement(String statement) {
    try {
      return (QueryType) CCJSqlParserUtil.parse(statement);
    } catch (JSQLParserException | ClassCastException e) {
      return null;
    }
  }

  protected abstract SqlCorrectionResult compareStatically(QueryType parsedUserStatement,
      QueryType parsedSampleStatement);

  protected void deleteDB(Connection connection, String slaveDB) throws SQLException {
    // TODO Auto-generated method stub
    long startTime = System.currentTimeMillis();
    connection.createStatement().executeUpdate("DROP DATABASE IF EXISTS " + slaveDB);
    long timeTaken = System.currentTimeMillis() - startTime;
    theLogger.info("Successfully dropped database " + slaveDB + " in " + timeTaken + "ms");
  }

  protected abstract SqlCorrectionResult executeQuery(QueryType userStatement, QueryType sampleStatement,
      Connection conn, String slaveDB, String scenarioName);

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
}