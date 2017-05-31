package model.querycorrectors.update;

import java.util.Arrays;
import java.util.List;

import javax.inject.Singleton;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.delete.Delete;

@Singleton
public class DeleteCorrector extends ChangeCorrector<Delete> {

  public DeleteCorrector() {
    super("DELETE", false, true);
  }

  @Override
  protected List<String> getColumns(Delete userQuery) {
    throw new UnsupportedOperationException("A DELETE statement has no columns!");
  }

  @Override
  protected List<String> getTables(Delete userQuery) {
    return Arrays.asList(userQuery.getTable().getName());
  }

  @Override
  protected Expression getWhere(Delete query) {
    return query.getWhere();
  }

}
