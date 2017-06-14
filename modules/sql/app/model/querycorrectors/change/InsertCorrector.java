package model.querycorrectors.change;

import java.util.Arrays;
import java.util.List;

import javax.inject.Singleton;

import model.matching.MatchingResult;
import model.querycorrectors.columnMatch.ColumnMatch;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.insert.Insert;

@Singleton
public class InsertCorrector extends ChangeCorrector<Insert> {

  public InsertCorrector() {
    super("INSERT");
  }

  @Override
  protected MatchingResult<Column, ColumnMatch<Column>> compareColumns(Insert userQuery, Insert sampleQuery) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected List<String> getTables(Insert userQuery) {
    return Arrays.asList(userQuery.getTable().getName());
  }

  @Override
  protected Expression getWhere(Insert query) {
    throw new UnsupportedOperationException("A INSERT statement has no WHERE clauses!");
  }

}
