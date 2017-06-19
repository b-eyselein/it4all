package model.querycorrectors.change;

import java.util.Arrays;
import java.util.List;

import javax.inject.Singleton;

import model.matching.MatchingResult;
import model.querycorrectors.columnmatch.ColumnMatch;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.delete.Delete;

@Singleton
public class DeleteCorrector extends ChangeCorrector<Delete> {
  
  public DeleteCorrector() {
    super("DELETE");
  }
  
  @Override
  protected MatchingResult<Column, ColumnMatch<Column>> compareColumns(Delete userQuery, Delete sampleQuery) {
    return null;
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
