package model.querycorrectors.create;

import model.matching.Match;
import model.matching.Matcher;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import play.Logger;

public class ColumnDefinitionMatcher extends Matcher<ColumnDefinition> {
  
  public ColumnDefinitionMatcher() {
    super((arg0, arg1) -> {
      Logger.debug("Column Names: >>" + arg0.getColumnName() + "<< :: >>" + arg1.getColumnName() + "<<");
      return arg0.getColumnName().equals(arg1.getColumnName());
    });
  }
  
  @Override
  protected Match<ColumnDefinition> instantiateMatch(ColumnDefinition arg1, ColumnDefinition arg2) {
    return new CreateColumnDefinitionMatch(arg1, arg2);
  }
}
