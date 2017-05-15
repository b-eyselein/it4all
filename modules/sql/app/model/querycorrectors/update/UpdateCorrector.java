package model.querycorrectors.update;

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
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.update.Update;

@Singleton
public class UpdateCorrector extends ChangeCorrector<Update, Update> {

  private List<String> getColumns(Update statement) {
    List<Column> columns = statement.getColumns();
    return (columns == null) ? Collections.emptyList()
        : columns.stream().map(Column::getColumnName).collect(Collectors.toList());
  }

  @Override
  protected MatchingResult<String> compareColumns(Update userQuery, Update sampleQuery) {
    return STRING_EQ_MATCHER.match("Spalten", getColumns(userQuery), getColumns(sampleQuery));
  }

  @Override
  protected MatchingResult<String> compareGroupByElements(Update userQuery, Update sampleQuery) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected MatchingResult<String> compareOrderByElements(Update userQuery, Update sampleQuery) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected Update getPlainStatement(Update query) {
    return query;
  }

  @Override
  protected List<String> getTables(Update statement) {
    return statement.getTables().stream().map(Table::getName).collect(Collectors.toList());
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
