package model.querycorrectors.update;

import java.util.Arrays;
import java.util.List;

import model.SqlCorrectionException;
import model.correctionresult.ColumnComparison;
import model.correctionresult.TableComparison;
import model.exercise.FeedbackLevel;
import model.result.EvaluationResult;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.delete.Delete;

public class DeleteCorrector extends ChangeCorrector<Delete, Delete> {

  @Override
  protected ColumnComparison compareColumns(Delete userQuery, Delete sampleQuery) {
    return null;
  }
  
  @Override
  protected List<EvaluationResult> compareStatically(Delete userQuery, Delete sampleQuery,
      FeedbackLevel feedbackLevel) {

    TableComparison tableComparison = compareTables(userQuery, sampleQuery);

    ColumnComparison columnComparison = compareColumns(userQuery, sampleQuery);

    EvaluationResult whereComparison = compareWheres(userQuery, sampleQuery);

    return Arrays.asList(tableComparison, columnComparison, whereComparison);
  }

  @Override
  protected List<String> getTables(Delete userQuery) {
    return Arrays.asList(userQuery.getTable().getName());
  }

  @Override
  protected Expression getWhere(Delete query) {
    return query.getWhere();
  }

  @Override
  protected Delete parseStatement(String statement) throws SqlCorrectionException {
    try {
      return (Delete) CCJSqlParserUtil.parse(statement);
    } catch (JSQLParserException e) {
      throw new SqlCorrectionException("Es gab einen Fehler beim Parsen des folgenden Statements: " + statement);
    } catch (ClassCastException e) {
      throw new SqlCorrectionException("Das Statement war vom falschen Typ! Erwartet wurde DELETE!");
    }
  }
}