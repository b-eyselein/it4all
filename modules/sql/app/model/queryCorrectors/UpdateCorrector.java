package model.queryCorrectors;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import model.SqlCorrectionException;
import model.correctionResult.ColumnComparison;
import model.correctionResult.SqlExecutionResult;
import model.correctionResult.TableComparison;
import model.exercise.EvaluationResult;
import model.exercise.FeedbackLevel;
import model.exercise.SqlExercise;
import model.exercise.Success;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.update.Update;
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
  protected SqlExecutionResult executeQuery(Database database, Update userStatement, Update sampleStatement,
      SqlExercise exercise, FeedbackLevel feedbackLevel) {
    // TODO Auto-generated method stub
    return new SqlExecutionResult(Success.NONE, "Ausführungsvergleich der Statements muss noch implementiert werden!",
        feedbackLevel, null, null);
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
