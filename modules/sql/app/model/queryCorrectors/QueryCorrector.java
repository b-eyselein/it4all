package model.queryCorrectors;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import model.SqlCorrectionException;
import model.correctionResult.ColumnComparison;
import model.correctionResult.TableComparison;
import model.exercise.EvaluationFailed;
import model.exercise.EvaluationResult;
import model.exercise.FeedbackLevel;
import model.exercise.SqlExercise;
import model.exercise.Success;
import net.sf.jsqlparser.statement.Statement;
import play.db.Database;

public abstract class QueryCorrector<QueryType extends Statement, ComparedType, ExerciseType extends SqlExercise> {

  protected static <T> List<T> listDifference(List<T> a, List<T> b) {
    List<T> ret = new LinkedList<>(a);
    ret.removeAll(b);
    return ret;
  }

  @SafeVarargs
  protected static <T extends Comparable<T>> T minimum(T... toCompare) {
    if(toCompare.length == 0)
      return null;
    T min = toCompare[0];
    for(T t: toCompare)
      if(t.compareTo(min) < 0)
        min = t;
    return min;
  }

  @SuppressWarnings("unchecked")
  public List<EvaluationResult> correct(Database database, String userStatement, String sampleStatement,
      SqlExercise exercise, FeedbackLevel fbLevel) {
    QueryType parsedUserStatement, parsedSampleStatement;
    try {
      parsedUserStatement = parseStatement(userStatement);
      parsedSampleStatement = parseStatement(sampleStatement);
    } catch (SqlCorrectionException e) {
      return Arrays.asList(new EvaluationFailed(e.getMessage()));
    }
    
    List<EvaluationResult> ret = new LinkedList<>();

    if(fbLevel.compareTo(FeedbackLevel.FULL_FEEDBACK) >= 0)
      // Compare queries statically
      ret.addAll(compareStatically(parsedUserStatement, parsedSampleStatement, fbLevel));

    // Execute both queries, check if results match
    ret.add(executeQuery(database, parsedUserStatement, parsedSampleStatement, (ExerciseType) exercise, fbLevel));

    return ret;
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

  protected abstract List<EvaluationResult> compareStatically(QueryType parsedUserStatement,
      QueryType parsedSampleStatement, FeedbackLevel feedbackLevel);

  protected TableComparison compareTables(ComparedType userQuery, ComparedType sampleQuery) {
    List<String> userTableNames = getTables(userQuery);
    List<String> sampleTableNames = getTables(sampleQuery);

    List<String> wrongTables = listDifference(userTableNames, sampleTableNames);
    List<String> missingTables = listDifference(sampleTableNames, userTableNames);

    Success success = Success.NONE;
    if(missingTables.isEmpty() && wrongTables.isEmpty())
      success = Success.COMPLETE;

    return new TableComparison(success, missingTables, wrongTables);
  }

  protected abstract EvaluationResult executeQuery(Database database, QueryType userStatement,
      QueryType sampleStatement, ExerciseType exercise, FeedbackLevel feedbackLevel);

  protected abstract List<String> getColumns(ComparedType statement);

  protected abstract List<String> getTables(ComparedType userQuery);

  protected abstract QueryType parseStatement(String statement) throws SqlCorrectionException;
}