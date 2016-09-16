package model.queryCorrectors;

import java.util.LinkedList;
import java.util.List;

import model.SqlCorrectionException;
import model.correctionResult.ColumnComparison;
import model.correctionResult.SqlCorrectionResult;
import model.correctionResult.TableComparison;
import model.exercise.FeedbackLevel;
import model.exercise.SqlExercise;
import model.exercise.Success;
import net.sf.jsqlparser.statement.Statement;
import play.db.Database;

public abstract class QueryCorrector<QueryType extends Statement, ComparedType> {

  protected static List<String> listDifference(List<String> a, List<String> b) {
    List<String> ret = new LinkedList<>();
    ret.addAll(a);
    ret.removeAll(b);
    return ret;
  }

  public SqlCorrectionResult correct(Database database, String userStatement, String sampleStatement,
      SqlExercise exercise, FeedbackLevel feedbackLevel) {
    QueryType parsedUserStatement, parsedSampleStatement;
    try {
      parsedUserStatement = parseStatement(userStatement);
      parsedSampleStatement = parseStatement(sampleStatement);
    } catch (SqlCorrectionException e) {
      return new SqlCorrectionResult(Success.NONE, e.getMessage(), feedbackLevel);
    }

    // Compare queries statically
    SqlCorrectionResult staticComp = compareStatically(parsedUserStatement, parsedSampleStatement, feedbackLevel);

    // Execute both queries, check if results match
    SqlCorrectionResult executionResult = executeQuery(database, parsedUserStatement, parsedSampleStatement,
        exercise.scenario.shortName, feedbackLevel);

    if(staticComp != null)
      executionResult.withTableComparisonResult(staticComp.getTableComparison())
          .withColumnsComparisonResult(staticComp.getColumnComparison());

    return executionResult;
  }

  protected final ColumnComparison compareColumns(ComparedType userQuery, ComparedType sampleQuery) {
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
      QueryType parsedSampleStatement, FeedbackLevel feedbackLevel);

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

  protected abstract SqlCorrectionResult executeQuery(Database database, QueryType userStatement,
      QueryType sampleStatement, String scenarioName, FeedbackLevel feedbackLevel);

  protected abstract List<String> getColumns(ComparedType statement);

  protected abstract List<String> getTables(ComparedType userQuery);

  protected abstract QueryType parseStatement(String statement) throws SqlCorrectionException;
}