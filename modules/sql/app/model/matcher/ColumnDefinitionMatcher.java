package model.matcher;

import java.util.List;

import model.correctionresult.CreateResult;
import model.matching.Matcher;
import model.matching.MatchingResult;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

public class ColumnDefinitionMatcher extends Matcher<ColumnDefinition, ColumnDefinitionMatch> {

  public ColumnDefinitionMatcher() {
    super(
        // Equals tester
        (arg0, arg1) -> arg0.getColumnName().equals(arg1.getColumnName()),
        // Matching Action
        ColumnDefinitionMatch::new);
  }

  @Override
  protected MatchingResult<ColumnDefinition, ColumnDefinitionMatch> instantiateMatch(
      List<ColumnDefinitionMatch> matches, List<ColumnDefinition> notMatchedInFirst,
      List<ColumnDefinition> notMatchedInSecond) {
    return new CreateResult(matches, notMatchedInFirst, notMatchedInSecond);
  }
}
