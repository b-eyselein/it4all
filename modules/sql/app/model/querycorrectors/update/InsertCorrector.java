package model.querycorrectors.update;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import model.SqlCorrectionException;
import model.correctionresult.ComparisonTwoListsOfStrings;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.insert.Insert;

@Singleton
public class InsertCorrector extends ChangeCorrector<Insert, Insert> {
  
  private List<String> getColumns(Insert statement) {
    List<Column> columns = statement.getColumns();
    return (columns == null) ? Collections.emptyList()
        : columns.stream().map(Column::getColumnName).collect(Collectors.toList());
  }
  
  @Override
  protected ComparisonTwoListsOfStrings compareColumns(Insert userQuery, Insert sampleQuery) {
    // TODO Auto-generated method stub
    // FIXME: keine Beachtung der Groß-/Kleinschreibung bei Vergleich! -->
    // Verwendung core --> model.result.Matcher?
    List<String> userColumns = getColumns(userQuery).stream().map(String::toUpperCase).collect(Collectors.toList());
    List<String> sampleColumns = getColumns(sampleQuery).stream().map(String::toUpperCase).collect(Collectors.toList());
    
    List<String> wrongColumns = listDifference(userColumns, sampleColumns);
    List<String> missingColumns = listDifference(sampleColumns, userColumns);
    
    return new ComparisonTwoListsOfStrings("Spalten", missingColumns, wrongColumns);
  }
  
  @Override
  protected ComparisonTwoListsOfStrings compareGroupByElements(Insert userQuery, Insert sampleQuery) {
    return null;
  }
  
  @Override
  protected ComparisonTwoListsOfStrings compareOrderByElements(Insert userQuery, Insert sampleQuery) {
    return null;
  }
  
  @Override
  protected Insert getPlainStatement(Insert query) {
    return query;
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
    } catch (JSQLParserException e) { // NOSONAR
      throw new SqlCorrectionException("Es gab einen Fehler beim Parsen des folgenden Statements: " + statement);
    } catch (ClassCastException e) { // NOSONAR
      throw new SqlCorrectionException("Das Statement war vom falschen Typ! Erwartet wurde INSERT!");
    }
  }
  
}
