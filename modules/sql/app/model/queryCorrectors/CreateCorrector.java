package model.queryCorrectors;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import model.SqlCorrectionException;
import model.correctionResult.ColumnComparison;
import model.exercise.CreateExercise;
import model.exercise.EvaluationFailed;
import model.exercise.EvaluationResult;
import model.exercise.FeedbackLevel;
import model.exercise.Success;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import play.Logger;
import play.db.Database;

public class CreateCorrector extends QueryCorrector<CreateTable, CreateTable, CreateExercise> {

  @Override
  protected List<EvaluationResult> compareStatically(CreateTable userStatement, CreateTable sampleStatement,
      FeedbackLevel feedbackLevel) {

    // List<ColumnDefinition> userDefs = userStatement.getColumnDefinitions();
    // List<ColumnDefinition> sampleDefs =
    // sampleStatement.getColumnDefinitions();

    List<String> userDefColumns = getColumns(userStatement);
    List<String> sampleDefColumns = getColumns(sampleStatement);

    Logger.debug("\nUser has defined following columns:\n\t" + userDefColumns
        + "\nSample has defined following columns:\n\t" + sampleDefColumns);

    List<String> missingColumns = listDifference(sampleDefColumns, userDefColumns);
    List<String> unneccessaryColumns = listDifference(userDefColumns, sampleDefColumns);

    Logger.debug("Missing columns: " + missingColumns);
    Logger.debug("Unneccessary columns: " + unneccessaryColumns);

    ColumnComparison columnComparison = new ColumnComparison(Success.NONE, missingColumns, unneccessaryColumns);

    // TODO Auto-generated method stub
    return Arrays.asList(columnComparison, new EvaluationFailed("NOT YET IMPLEMENTED!"));
  }

  @Override
  protected EvaluationResult executeQuery(Database database, CreateTable parsedStatement,
      CreateTable parsedSampleStatement, CreateExercise exercise, FeedbackLevel feedbackLevel) {
    try(Connection connection = database.getConnection()) {
      connection.setCatalog(exercise.scenario.shortName);

      // ResultSet resultSet = connection.createStatement().executeQuery("SHOW
      // FIELDS FROM " + "Employee"); // exercise.tablename);
      // ResultSetMetaData rsmd = resultSet.getMetaData();
      // while(resultSet.next()) {
      // for(int i = 1; i <= rsmd.getColumnCount(); i++) {
      // System.out.print(resultSet.getString(i) + "\t\t");
      // }
      // System.out.println();
      // }

    } catch (SQLException e) {
      Logger.error("Failure:", e);
    }
    return new EvaluationFailed("NOT YET IMPLEMENTED!");
  }

  @Override
  protected List<String> getColumns(CreateTable statement) {
    return statement.getColumnDefinitions().stream().map(userDef -> userDef.getColumnName())
        .collect(Collectors.toList());
  }

  @Override
  protected List<String> getTables(CreateTable userQuery) {
    return Arrays.asList(userQuery.getTable().toString());
  }

  @Override
  protected CreateTable parseStatement(String statement) throws SqlCorrectionException {
    try {
      return (CreateTable) CCJSqlParserUtil.parse(statement);
    } catch (JSQLParserException e) {
      throw new SqlCorrectionException("Es gab einen Fehler beim Parsen des folgenden Statements:</p><p>" + statement,
          e);
    } catch (ClassCastException e) {
      throw new SqlCorrectionException("Das Statement war vom falschen Typ! Erwartet wurde CREATE TABLE!");
    }
  }
}
