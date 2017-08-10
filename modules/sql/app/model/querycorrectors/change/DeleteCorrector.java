package model.querycorrectors.change;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;

import model.matching.MatchingResult;
import model.querycorrectors.ColumnMatch;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;

@Singleton
public class DeleteCorrector extends ChangeCorrector<Delete> {

  public DeleteCorrector() {
    super("DELETE");
  }

  @Override
  protected MatchingResult<Column, ColumnMatch<Column>> compareColumns(Delete userQuery,
      Map<String, String> userTableAliases, Delete sampleQuery, Map<String, String> sampleTableAliases) {
    return null;
  }

  @Override
  protected List<String> getTableNames(Delete userQuery) {
    return Arrays.asList(userQuery.getTable().getName());
  }

  @Override
  protected List<Table> getTables(Delete query) {
    return query.getTables();
  }

  @Override
  protected Expression getWhere(Delete query) {
    return query.getWhere();
  }
  
}
