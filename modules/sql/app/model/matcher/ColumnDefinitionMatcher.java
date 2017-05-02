package model.matcher;

import model.matching.Matcher;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

public class ColumnDefinitionMatcher extends Matcher<ColumnDefinition> {
  
  public ColumnDefinitionMatcher() {
    super((arg0, arg1) -> arg0.getColumnName().equals(arg1.getColumnName()));
  }
  
}
