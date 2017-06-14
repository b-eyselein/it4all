package model.querycorrectors.create;

import model.querycorrectors.columnMatch.ColumnMatch;
import model.querycorrectors.columnMatch.ColumnMatcher;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import play.Logger;

public class CreateColumnMatcher extends ColumnMatcher<ColumnDefinition> {
  
  public CreateColumnMatcher() {
    super((arg0, arg1) -> {
      Logger.debug("Column Names: >>" + arg0.getColumnName() + "<< :: >>" + arg1.getColumnName() + "<<");
      return arg0.getColumnName().equals(arg1.getColumnName());
    });
  }
  
  @Override
  protected ColumnMatch<ColumnDefinition> instantiateMatch(ColumnDefinition arg1, ColumnDefinition arg2) {
    return new CreateColumnMatch(arg1, arg2);
  }
}
