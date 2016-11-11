package model.querycorrectors.update;

import java.util.Arrays;
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
import net.sf.jsqlparser.statement.update.Update;
import play.Logger;

public class UpdateCorrector extends ChangeCorrector<Update, Update> {
  
  private List<String> getColumns(Update statement) {
    return statement.getColumns().stream().map(col -> col.getColumnName()).collect(Collectors.toList());
  }
  
  @Override
  protected ColumnComparison compareColumns(Update userQuery, Update sampleQuery) {
    // FIXME: keine Beachtung der Groß-/Kleinschreibung bei Vergleich! -->
    // Verwendung core --> model.result.Matcher?
    List<String> userColumns = getColumns(userQuery).stream().map(q -> q.toUpperCase()).collect(Collectors.toList());
    List<String> sampleColumns = getColumns(sampleQuery).stream().map(q -> q.toUpperCase())
        .collect(Collectors.toList());
    
    // FIXME: correct set ... part of query!
    for(Expression e: userQuery.getExpressions())
      Logger.debug(e + "");
    
    List<String> wrongColumns = listDifference(userColumns, sampleColumns);
    List<String> missingColumns = listDifference(sampleColumns, userColumns);
    
    Success success = Success.NONE;
    if(wrongColumns.isEmpty() && missingColumns.isEmpty())
      success = Success.COMPLETE;
    
    return new ColumnComparison(success, missingColumns, wrongColumns);
  }
  
  @Override
  protected List<EvaluationResult> compareStatically(Update userQuery, Update sampleQuery,
      FeedbackLevel feedbackLevel) {
    
    TableComparison tableComparison = compareTables(userQuery, sampleQuery);
    
    ColumnComparison columnComparison = compareColumns(userQuery, sampleQuery);
    
    EvaluationResult whereComparison = compareWheres(userQuery, sampleQuery);
    
    return Arrays.asList(tableComparison, columnComparison, whereComparison);
  }
  
  @Override
  protected List<String> getTables(Update statement) {
    return statement.getTables().stream().map(table -> table.getName()).collect(Collectors.toList());
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
