package model.querycorrectors.update;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import model.matching.MatchingResult;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.update.Update;

@Singleton
public class UpdateCorrector extends ChangeCorrector<Update> {

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
  protected List<String> getTables(Update statement) {
    return statement.getTables().stream().map(Table::getName).collect(Collectors.toList());
  }

  @Override
  protected Expression getWhere(Update query) {
    return query.getWhere();
  }

}
