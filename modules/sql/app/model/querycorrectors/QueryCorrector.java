package model.querycorrectors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import model.ScriptRunner;
import model.SqlCorrectionException;
import model.correctionresult.ComparisonTwoListsOfStrings;
import model.correctionresult.SqlExecutionResult;
import model.correctionresult.SqlResult;
import model.correctionresult.SqlResultBuilder;
import model.exercise.FeedbackLevel;
import model.exercise.SqlExercise;
import model.matcher.BinaryExpressionMatch;
import model.matching.MatchingResult;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.Statement;
import play.Logger;
import play.db.Database;

public abstract class QueryCorrector<Q extends Statement, C> {

  private static final String CREATE_DB = "CREATE DATABASE IF NOT EXISTS ?";

  protected static <T> List<String> listAsStrings(List<T> list) {
    if(list == null)
      return Collections.emptyList();
    return list.stream().map(T::toString).collect(Collectors.toList());
  }

  protected static <T> List<T> listDifference(List<T> a, List<T> b) {
    List<T> ret = new LinkedList<>(a);
    ret.removeAll(b);
    return ret;
  }

  protected abstract ComparisonTwoListsOfStrings compareColumns(C userQuery, C sampleQuery);

  protected abstract ComparisonTwoListsOfStrings compareGroupByElements(C userQuery, C sampleQuery);

  protected abstract ComparisonTwoListsOfStrings compareOrderByElements(C userQuery, C sampleQuery);

  protected ComparisonTwoListsOfStrings compareTables(C userQuery, C sampleQuery) {
    List<String> userTableNames = getTables(userQuery);
    List<String> sampleTableNames = getTables(sampleQuery);

    List<String> wrongTables = listDifference(userTableNames, sampleTableNames);
    List<String> missingTables = listDifference(sampleTableNames, userTableNames);

    return new ComparisonTwoListsOfStrings("Tabellen", missingTables, wrongTables);
  }

  protected MatchingResult<BinaryExpression, BinaryExpressionMatch> compareWheres(C userQuery, C sampleQuery) {
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

  public SqlResult correct(Database database, String userStatement, String sampleStatement, SqlExercise exercise,
      FeedbackLevel fbLevel) {
    Q parsedUserStatement = null;
    Q parsedSampleStatement = null;
    try {
      parsedUserStatement = parseStatement(userStatement);
      parsedSampleStatement = parseStatement(sampleStatement);
    } catch (SqlCorrectionException e) { // NOSONAR
      // ret.add(new EvaluationFailed(e.getMessage(), e.getCauseMessage()));
    }

    C plainUserQuery = getPlainStatement(parsedUserStatement);
    C plainSampleQuery = getPlainStatement(parsedSampleStatement);

    // FIXME: use flags?
    ComparisonTwoListsOfStrings columnComp = compareColumns(plainUserQuery, plainSampleQuery);

    ComparisonTwoListsOfStrings tableComp = compareTables(plainUserQuery, plainSampleQuery);

    ComparisonTwoListsOfStrings orderByComparison = compareOrderByElements(plainUserQuery, plainSampleQuery);

    ComparisonTwoListsOfStrings groupByComparison = compareGroupByElements(plainUserQuery, plainSampleQuery);

    MatchingResult<BinaryExpression, BinaryExpressionMatch> whereComp = compareWheres(plainUserQuery, plainSampleQuery);

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