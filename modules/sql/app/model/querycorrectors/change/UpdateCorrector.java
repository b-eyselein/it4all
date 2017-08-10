package model.querycorrectors.change;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import model.querycorrectors.ColumnWrapper;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.update.Update;

@Singleton
public class UpdateCorrector extends ChangeCorrector<Update> {
  
  public UpdateCorrector() {
    super("UPDATE");
  }

  @Override
  protected List<ColumnWrapper> getColumnWrappers(Update query) {
    return query.getColumns().parallelStream().map(ColumnWrapper::wrap).collect(Collectors.toList());
  }
  
  @Override
  protected List<String> getTableNames(Update statement) {
    return statement.getTables().stream().map(Table::getName).collect(Collectors.toList());
  }
  
  @Override
  protected List<Table> getTables(Update query) {
    return query.getTables();
  }
  
  @Override
  protected Expression getWhere(Update query) {
    return query.getWhere();
  }

}
