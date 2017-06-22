package model.querycorrectors.change;

import java.util.List;
import java.util.Map;

import model.matching.MatchingResult;
import model.querycorrectors.ColumnMatch;
import model.querycorrectors.ColumnMatcher;
import net.sf.jsqlparser.schema.Column;

public class UpdateColumnMatcher extends ColumnMatcher<Column> {

  public UpdateColumnMatcher(Map<String, String> theUserTableAliases, Map<String, String> theSampleTableAliases) {
    super((col1, col2) -> col1.getColumnName().equals(col2.getColumnName()));
  }

  public MatchingResult<Column, ColumnMatch<Column>> match(List<Column> userColumns, List<Column> sampleColumns) {
    return match("Spalten", userColumns, sampleColumns);
  }

  @Override
  protected ColumnMatch<Column> instantiateMatch(Column arg1, Column arg2) {
    return new UpdateColumnMatch(arg1, arg2);
  }

}
