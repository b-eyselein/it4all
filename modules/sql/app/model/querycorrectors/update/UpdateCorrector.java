package model.querycorrectors.update;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import model.ColumnWrapper;
import model.matching.MatchingResult;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.update.Update;

@Singleton
public class UpdateCorrector extends ChangeCorrector<Update> {
  
  public UpdateCorrector() {
    super("UPDATE", true, true);
  }
  
  // @Override
  // protected List<String> getColumns(Update statement) {
  // List<Column> columns = statement.getColumns();
  //
  // if(columns == null)
  // return Collections.emptyList();
  //
  // return
  // columns.stream().map(Column::getColumnName).collect(Collectors.toList());
  // }
  
  @Override
  protected MatchingResult<ColumnWrapper<?>> compareColumns(Update userQuery, Update sampleQuery) {
    // TODO Auto-generated method stub
    return null;
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
