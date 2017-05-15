package model.querycorrectors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import model.ScriptRunner;
import model.SqlCorrectionException;
import model.correctionresult.SqlExecutionResult;
import model.correctionresult.SqlResult;
import model.correctionresult.SqlResultBuilder;
import model.exercise.FeedbackLevel;
import model.exercise.SqlExercise;
import model.exercise.SqlSample;
import model.matching.Matcher.StringEqualsMatcher;
import model.matching.MatchingResult;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.Statement;
import play.Logger;
import play.db.Database;

public abstract class QueryCorrector<Q extends Statement, C> {

  protected static final StringEqualsMatcher STRING_EQ_MATCHER = new StringEqualsMatcher();

  private static final String CREATE_DB = "CREATE DATABASE IF NOT EXISTS ?";

  protected static <T> List<String> listAsStrings(List<T> list) {
    if(list == null)
      return Collections.emptyList();
    return list.stream().map(T::toString).collect(Collectors.toList());
  }

  public SqlResult correct(Database database, String userStatement, SqlSample sampleStatement, SqlExercise exercise,
      FeedbackLevel fbLevel) {
    Q parsedUserStatement = null;
    Q parsedSampleStatement = null;
    try {
      parsedUserStatement = parseStatement(userStatement);
      parsedSampleStatement = parseStatement(sampleStatement.sample);
    } catch (SqlCorrectionException e) { // NOSONAR
      // ret.add(new EvaluationFailed(e.getMessage(), e.getCauseMessage()));
    }

    C plainUserQuery = getPlainStatement(parsedUserStatement);
    C plainSampleQuery = getPlainStatement(parsedSampleStatement);

    // FIXME: use flags?
    MatchingResult<String> columnComp = compareColumns(plainUserQuery, plainSampleQuery);

    MatchingResult<String> tableComp = compareTables(plainUserQuery, plainSampleQuery);

    MatchingResult<String> orderByComparison = compareOrderByElements(plainUserQuery, plainSampleQuery);

    MatchingResult<String> groupByComparison = compareGroupByElements(plainUserQuery, plainSampleQuery);

    MatchingResult<BinaryExpression> whereComp = compareWheres(plainUserQuery, plainSampleQuery);

    SqlExecutionResult executionResult = executeQuery(database, parsedUserStatement, parsedSampleStatement, exercise,
        fbLevel);

    // @formatter:off
    return new SqlResultBuilder()
        .setColumnComparison(columnComp)
        .setTableComparison(tableComp)
        .setGroupByComparison(groupByComparison)
        .setOrderByComparison(orderByComparison)
        .setWhereComparison(whereComp)
        .setExecutionResult(executionResult)
        .build();
    // @formatter:on
  }

  private void createDatabase(Connection connection, String databaseName) throws SQLException {
    PreparedStatement createStatement = connection.prepareStatement(CREATE_DB);
    createStatement.setString(1, databaseName);
    createStatement.executeQuery();
    connection.setCatalog(databaseName);
    createStatement.close();
  }

  protected abstract MatchingResult<String> compareColumns(C userQuery, C sampleQuery);

  protected abstract MatchingResult<String> compareGroupByElements(C userQuery, C sampleQuery);

  protected abstract MatchingResult<String> compareOrderByElements(C userQuery, C sampleQuery);

  protected MatchingResult<String> compareTables(C userQuery, C sampleQuery) {
    return STRING_EQ_MATCHER.match("Tabellen", getTables(userQuery), getTables(sampleQuery));
  }

  protected MatchingResult<BinaryExpression> compareWheres(C userQuery, C sampleQuery) {
    Expression userWhere = getWhere(userQuery);
    Expression sampleWhere = getWhere(sampleQuery);

    if(userWhere == null && sampleWhere == null)
      return null;
    else if(userWhere == null)
      // return new EvaluationFailed("In der Musterlösung sind Bedingungen!");
      return null;
    else if(sampleWhere == null)
      // return new EvaluationFailed("In der Musterlösung waren keine
      // Bedingungen angegeben!");
      return null;

    WhereCorrector whereCorrector = new WhereCorrector();
    return whereCorrector.correct(userWhere, sampleWhere);
  }

  protected void createDatabaseIfNotExists(Connection connection, String databaseName, Path scriptFile) {
    try(ResultSet catalogs = connection.getMetaData().getCatalogs()) {
      while(catalogs.next())
        if(catalogs.getString(1).equals(databaseName))
          // Database already exists
          return;

      List<String> lines = Files.readAllLines(scriptFile);
      createDatabase(connection, databaseName);
      connection.setCatalog(databaseName);
      ScriptRunner.runScript(connection, lines, false, true);
    } catch (SQLException | IOException e) {
      Logger.error("Error while initialising database " + databaseName, e);
    }
  }

  protected abstract SqlExecutionResult executeQuery(Database database, Q userStatement, Q sampleStatement,
      SqlExercise exercise, FeedbackLevel feedbackLevel);

  protected abstract C getPlainStatement(Q query);

  protected abstract List<String> getTables(C userQuery);

  protected abstract Expression getWhere(C query);

  protected abstract Q parseStatement(String statement) throws SqlCorrectionException;
}