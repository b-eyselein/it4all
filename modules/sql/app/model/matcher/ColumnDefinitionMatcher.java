package model.matcher;

import java.util.List;

import model.correctionresult.CreateResult;
import model.matching.Matcher;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

public class ColumnDefinitionMatcher extends Matcher<ColumnDefinition, ColumnDefinitionMatch, CreateResult> {
  
  public ColumnDefinitionMatcher() {
    super(
        // Equals tester
        (arg0, arg1) -> arg0.getColumnName().equals(arg1.getColumnName()),
        // Matching Action
        ColumnDefinitionMatch::new);
  }
  
  @Override
  protected CreateResult instantiateMatch(List<ColumnDefinitionMatch> matches, List<ColumnDefinition> notMatchedInFirst,
      List<ColumnDefinition> notMatchedInSecond) {
    return new CreateResult(matches, notMatchedInFirst, notMatchedInSecond);
  }
}
