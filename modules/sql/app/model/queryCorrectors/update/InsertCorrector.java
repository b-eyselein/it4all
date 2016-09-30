package model.queryCorrectors.update;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import model.SqlCorrectionException;
import model.correctionResult.ColumnComparison;
import model.correctionResult.TableComparison;
import model.exercise.EvaluationResult;
import model.exercise.FeedbackLevel;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.insert.Insert;

public class InsertCorrector extends ChangeCorrector<Insert, Insert> {
  
  @Override
  protected List<EvaluationResult> compareStatically(Insert userQuery, Insert sampleQuery,
      FeedbackLevel feedbackLevel) {
    
    TableComparison tableComparison = compareTables(userQuery, sampleQuery);
    
    ColumnComparison columnComparison = compareColumns(userQuery, sampleQuery);
    
    return Arrays.asList(tableComparison, columnComparison);
  }
  
  @Override
  protected List<String> getColumns(Insert statement) {
    List<Column> columns = statement.getColumns();
    if(columns == null)
      return Collections.emptyList();
    return columns.stream().map(column -> column.getColumnName()).collect(Collectors.toList());
  }
  
  @Override
  protected List<String> getTables(Insert userQuery) {
    return Arrays.asList(userQuery.getTable().getName());
  }
  
  @Override
  protected Expression getWhere(Insert query) {
    return null;
  }
  
  @Override
  protected Insert parseStatement(String statement) throws SqlCorrectionException {
    try {
      return (Insert) CCJSqlParserUtil.parse(statement);
    } catch (JSQLParserException e) {
      throw new SqlCorrectionException("Es gab einen Fehler beim Parsen des folgenden Statements: " + statement);
    } catch (ClassCastException e) {
      throw new SqlCorrectionException("Das Statement war vom falschen Typ! Erwartet wurde INSERT!");
    }
  }
}
