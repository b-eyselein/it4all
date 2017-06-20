package model.querycorrectors.create;

import model.querycorrectors.ColumnMatch;
import model.querycorrectors.ColumnMatcher;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

public class CreateColumnMatcher extends ColumnMatcher<ColumnDefinition> {

  public CreateColumnMatcher() {
    super((arg0, arg1) -> arg0.getColumnName().equals(arg1.getColumnName()));
  }

  @Override
  protected ColumnMatch<ColumnDefinition> instantiateMatch(ColumnDefinition arg1, ColumnDefinition arg2) {
    return new CreateColumnMatch(arg1, arg2);
  }
}
