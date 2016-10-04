package model.queryCorrectors;

import java.util.List;

import model.correctionResult.create.ColumnDefinitionMatch;
import model.correctionResult.create.CreateResult;
import model.matching.Match;
import model.matching.Matcher;
import model.matching.MatchingResult;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

public class ColumnDefinitionMatcher extends Matcher<ColumnDefinition> {
  
  public ColumnDefinitionMatcher() {
    super(
        // Equals tester
        (arg0, arg1) -> arg0.getColumnName().equals(arg1.getColumnName()),
        // Matching Action
        (arg1, arg2) -> new ColumnDefinitionMatch(arg1, arg2));
  }
  
  @Override
  protected MatchingResult<ColumnDefinition> instantiateMatch(List<Match<ColumnDefinition>> matches,
      List<ColumnDefinition> notMatchedInFirst, List<ColumnDefinition> notMatchedInSecond) {
    return new CreateResult(matches, notMatchedInFirst, notMatchedInSecond);
  }
}
