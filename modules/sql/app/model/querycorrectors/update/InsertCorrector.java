package model.querycorrectors.update;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import model.SqlCorrectionException;
import model.correctionresult.ColumnComparison;
import model.correctionresult.TableComparison;
import model.exercise.FeedbackLevel;
import model.exercise.Success;
import model.result.EvaluationResult;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.insert.Insert;

public class InsertCorrector extends ChangeCorrector<Insert, Insert> {

  private List<String> getColumns(Insert statement) {
    List<Column> columns = statement.getColumns();
    if(columns == null)
      return Collections.emptyList();
    return columns.stream().map(column -> column.getColumnName()).collect(Collectors.toList());
  }

  @Override
  protected ColumnComparison compareColumns(Insert userQuery, Insert sampleQuery) {
    // TODO Auto-generated method stub
    // FIXME: keine Beachtung der Groß-/Kleinschreibung bei Vergleich! -->
    // Verwendung core --> model.result.Matcher?
    List<String> userColumns = getColumns(userQuery).stream().map(q -> q.toUpperCase()).collect(Collectors.toList());
    List<String> sampleColumns = getColumns(sampleQuery).stream().map(q -> q.toUpperCase())
        .collect(Collectors.toList());
    
    List<String> wrongColumns = listDifference(userColumns, sampleColumns);
    List<String> missingColumns = listDifference(sampleColumns, userColumns);
    
    Success success = Success.NONE;
    if(wrongColumns.isEmpty() && missingColumns.isEmpty())
      success = Success.COMPLETE;
    
    return new ColumnComparison(success, missingColumns, wrongColumns);
  }
  
  @Override
  protected List<EvaluationResult> compareStatically(Insert userQuery, Insert sampleQuery,
      FeedbackLevel feedbackLevel) {

    TableComparison tableComparison = compareTables(userQuery, sampleQuery);

    ColumnComparison columnComparison = compareColumns(userQuery, sampleQuery);

    return Arrays.asList(tableComparison, columnComparison);
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
