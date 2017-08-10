package model.querycorrectors.change;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Singleton;

import model.querycorrectors.ColumnWrapper;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;

@Singleton
public class DeleteCorrector extends ChangeCorrector<Delete> {
  
  public DeleteCorrector() {
    super("DELETE");
  }
  
  @Override
  protected List<ColumnWrapper> getColumnWrappers(Delete query) {
    return Collections.emptyList();
  }
  
  @Override
  protected List<String> getTableNames(Delete userQuery) {
    return Arrays.asList(userQuery.getTable().getName());
  }
  
  @Override
  protected List<Table> getTables(Delete query) {
    return query.getTables();
  }
  
  @Override
  protected Expression getWhere(Delete query) {
    return query.getWhere();
  }
  
}
