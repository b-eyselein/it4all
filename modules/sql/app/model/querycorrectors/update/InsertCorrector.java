package model.querycorrectors.update;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import model.SqlCorrectionException;
import model.matching.MatchingResult;
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
  protected MatchingResult<String> compareColumns(Insert userQuery, Insert sampleQuery) {
    return STRING_EQ_MATCHER.match("Spalten", getColumns(userQuery), getColumns(sampleQuery));
  }
  
  @Override
  protected MatchingResult<String> compareGroupByElements(Insert userQuery, Insert sampleQuery) {
    return null;
  }
  
  @Override
  protected MatchingResult<String> compareOrderByElements(Insert userQuery, Insert sampleQuery) {
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
