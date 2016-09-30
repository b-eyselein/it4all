package model.queryCorrectors.update;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import model.SqlCorrectionException;
import model.correctionResult.ColumnComparison;
import model.correctionResult.TableComparison;
import model.exercise.EvaluationResult;
import model.exercise.FeedbackLevel;
import model.exercise.SqlExercise;
import model.exercise.update.UpdateExercise;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.update.Update;

public class UpdateCorrector extends ChangeCorrector<Update, Update> {

  @Override
  protected List<EvaluationResult> compareStatically(Update userQuery, Update sampleQuery,
      FeedbackLevel feedbackLevel) {

    TableComparison tableComparison = compareTables(userQuery, sampleQuery);

    ColumnComparison columnComparison = compareColumns(userQuery, sampleQuery);

    EvaluationResult whereComparison = compareWheres(userQuery, sampleQuery);

    return Arrays.asList(tableComparison, columnComparison, whereComparison);
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
  protected String getValidation(SqlExercise exercise) {
    return ((UpdateExercise) exercise).validation;
  }

  @Override
  protected Expression getWhere(Update query) {
    return query.getWhere();
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
