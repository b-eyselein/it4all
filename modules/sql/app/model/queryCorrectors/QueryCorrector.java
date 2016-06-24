package model.queryCorrectors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import model.Levenshtein;
import model.ScriptRunner;
import model.SqlCorrectionResult;
import model.TableComparisonResult;
import model.exercise.SqlExercise;
import model.exercise.SqlExercise.SqlExType;
import model.exercise.SqlSampleSolution;
import model.exercise.Success;
import model.user.User;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.util.TablesNamesFinder;
import play.Logger;

public abstract class QueryCorrector<QueryType extends Statement> {

  protected static final String DB_BASENAME = "sql_";
  protected static TablesNamesFinder tableNameFinder = new TablesNamesFinder();
  private static final String CREATE_DB_DUMMY = "CREATE DATABASE IF NOT EXISTS ";

  private static final String SHOW_ALL_DBS = "SHOW DATABASES";
  private static final Logger.ALogger theLogger = Logger.of("sql");
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
  @SuppressWarnings("unchecked")
  public SqlCorrectionResult correct(User user, Statement userStatement, SqlExercise exercise, Connection connection) {
    // 1. Check if the right operator (SELECT/DELETE/UPDATE/...) was used
    if(exercise.exType != exType)
      return new SqlCorrectionResult(Success.NONE, "Es wurde das falsche Keyword verwendet!");

    // 2. FIXME Get best fitting sample solution
    SqlSampleSolution bestFitting = findBestFittingSample(userStatement, exercise.samples);

    // 3. Cast learner solution to right class, get sample solution
    QueryType parsedSampleStatement = null;
    QueryType parsedUserStatement = null;
    try {
      parsedSampleStatement = (QueryType) CCJSqlParserUtil.parse(bestFitting.sample);
      parsedUserStatement = (QueryType) userStatement;
    } catch (JSQLParserException | ClassCastException e) {
      return new SqlCorrectionResult(Success.NONE, "Es gab einen Fehler bei der Korrektur!");
    }

    // if parsed statements are equal, no need to execute queries!
    if(parsedUserStatement.toString().equals(parsedSampleStatement.toString()))
      return new SqlCorrectionResult(Success.COMPLETE, "Stimmt mit Musterlösung überein!");

    // 4. Compare queries statically
    TableComparisonResult tableComparison = compareUsedTables(parsedUserStatement, parsedSampleStatement);

    // 5. Execute both queries, check if results match
    String slaveDB = DB_BASENAME + user.name;
    SqlCorrectionResult afterExecution = executeQuery(parsedUserStatement, parsedSampleStatement, connection, slaveDB);
    return afterExecution.withTableComparisonResult(tableComparison);
  }

  private boolean databaseAlreadyExists(Connection connection, String slaveDB) throws SQLException {
    ResultSet existingDBs = connection.createStatement().executeQuery(SHOW_ALL_DBS);
    while(existingDBs.next())
      if(slaveDB.equals(existingDBs.getString(1)))
        return true;
    return false;
  }

  private SqlSampleSolution findBestFittingSample(Statement userStatement, List<SqlSampleSolution> samples) {
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

  protected abstract TableComparisonResult compareUsedTables(QueryType parsedStatement,
      QueryType parsedSampleStatement);

  protected void deleteDB(Connection connection, String slaveDB) {
    // TODO Auto-generated method stub
    long startTime = System.currentTimeMillis();
    try {
      connection.createStatement().executeUpdate("DROP DATABASE IF EXISTS " + slaveDB);
    } catch (SQLException e) {
      theLogger.error("Problems while dropping database " + slaveDB, e);
    }
    long timeTaken = System.currentTimeMillis() - startTime;
    theLogger.info("Successfully dropped database " + slaveDB + " in " + timeTaken + "ms");
  }

  protected abstract SqlCorrectionResult executeQuery(QueryType userStatement, QueryType sampleStatement,
      Connection conn, String slaveDB);

  protected void initializeDB(Connection connection, String slaveDB) {
    long startTime = System.currentTimeMillis();
    try {
      // Check if slave DB exists, if not, create
      if(!databaseAlreadyExists(connection, slaveDB))
        connection.createStatement().executeUpdate(CREATE_DB_DUMMY + slaveDB);

      // Change db to users own db
      connection.setCatalog(slaveDB);

      // Initialize DB with values
      Path sqlScriptFile = Paths.get("modules/sql/conf/resources/phone.sql");
      if(Files.exists(sqlScriptFile))
        ScriptRunner.runScript(connection, Files.readAllLines(sqlScriptFile), false, false);
      else
        theLogger.error("Trying to initialize database with script " + sqlScriptFile + ", but there is no such file!");

    } catch (SQLException | IOException e) {
      theLogger.error("Error while initializing database " + slaveDB, e);
    }
    long timeTaken = System.currentTimeMillis() - startTime;
    theLogger.info("Successfully initialized the database " + slaveDB + " in " + timeTaken + "ms");
  }
}