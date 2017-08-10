package model.querycorrectors.change;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Singleton;

import model.querycorrectors.ColumnWrapper;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.insert.Insert;

@Singleton
public class InsertCorrector extends ChangeCorrector<Insert> {
  
  public InsertCorrector() {
    super("INSERT");
  }
  
  @Override
  protected List<ColumnWrapper> getColumnWrappers(Insert query) {
    return Collections.emptyList();
  }
  
  @Override
  protected List<String> getTableNames(Insert userQuery) {
    return Arrays.asList(userQuery.getTable().getName());
  }
  
  @Override
  protected List<Table> getTables(Insert query) {
    return Arrays.asList(query.getTable());
  }
  
  @Override
  protected Expression getWhere(Insert query) {
    return null;
  }
  
}
